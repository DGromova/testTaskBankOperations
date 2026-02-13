package com.bank.service;

import com.bank.models.Account;
import com.bank.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class InterestScheduledExecutorService {
    private AccountRepository accountRepository;
    private final ScheduledExecutorService executor;
    private final TransactionTemplate transactionTemplate;

    public InterestScheduledExecutorService(AccountRepository accountRepository, PlatformTransactionManager transactionManager) {
        this.accountRepository = accountRepository;
        this.executor = Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 2, 1));
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }
     //ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 2, 1));

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        start();
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        shutdown();
    }

    private void start() {
        executor.scheduleAtFixedRate(chargeInterest, 0, 1, TimeUnit.MINUTES);
    }

    Runnable chargeInterest = () -> {
        long startTime = System.currentTimeMillis();
        log.info("Interest accrual has started");

        int batchSize = 100;
        Pageable firstPageWithBatchSize = PageRequest.of(0, batchSize);

        //Locking slice
        Slice<Account> slice = accountRepository.findAllBy(firstPageWithBatchSize);
        List<Account> accountsInBatch = slice.getContent();
        executeUpdateBalanceInBatchWithTransactionTemplate(accountsInBatch);

        while (slice.hasNext()) {
            //Locking slice
            slice = accountRepository.findAllBy(slice.nextPageable());
            accountsInBatch = slice.getContent();
            executeUpdateBalanceInBatchWithTransactionTemplate(accountsInBatch);
        }

        long endTime = System.currentTimeMillis();
        log.info("Interest accrual completed in {} ms", endTime - startTime);
    };

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 10,
                    maxDelay = 820,
                    multiplier = 3.0,
                    random = true
            )
    )
    public void executeUpdateBalanceInBatchWithTransactionTemplate(List<Account> accountsInBatch) {
        transactionTemplate.execute(status -> {
            updateBalanceInBatch(accountsInBatch);
            return null;
        });
    }

   // Transactional
    private void updateBalanceInBatch(List<Account> accountsInBatch) {

        if (accountsInBatch == null || accountsInBatch.isEmpty()) {
            log.debug("Empty batch received, processing skipped");
            return;
        }

        for (Account a : accountsInBatch) {
            BigDecimal currentBalance = a.getCurrentBalance();
            BigDecimal initialBalance = a.getInitialBalance();

            BigDecimal newBalance = currentBalance
                    .multiply(BigDecimal.valueOf(1.05))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal accrualLimit = initialBalance
                    .multiply(BigDecimal.valueOf(2.07))
                    .setScale(2, RoundingMode.HALF_UP);

            if (newBalance.compareTo(accrualLimit) <= 0) {
                a.setCurrentBalance(newBalance);
            }
        }

        accountRepository.saveAll(accountsInBatch);
    }

    private void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

}
