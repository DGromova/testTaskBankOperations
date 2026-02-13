package com.bank.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.bank.exception.ArgumentValidationException;
import com.bank.models.UserDocument;
import com.bank.models.UserSearchParameters;
import com.bank.service.UserElasticsearchService;
import com.bank.util.BirthdateConverter;
import com.bank.util.PhoneConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserElasticsearchServiceImpl implements UserElasticsearchService {

    @Autowired
    private ElasticsearchClient esClient;

    @Override
    @SneakyThrows
    public SearchResponse<UserDocument> findUsers(UserSearchParameters userSearchParameters, int page, int size, String sortField, String direction) {
        if (userSearchParameters.getBirthdate() == null
                && userSearchParameters.getFullName() == null
                && userSearchParameters.getPhone() == null
                && userSearchParameters.getEmail() == null) {
            log.info("The user did not set any parameters for the search");
            throw new ArgumentValidationException("No parameters are set for the search");
        }

        log.debug("Starting user search using Elasticsearch");

        Query byBirthdate;
        if (userSearchParameters.getBirthdate() != null) {
            LocalDate convertedBirthdate = BirthdateConverter.convertDateFormat(userSearchParameters.getBirthdate());
            byBirthdate = DateRangeQuery.of(r -> r
                            .field("birthdate")
                            .gt(String.valueOf(convertedBirthdate))
            )._toRangeQuery()._toQuery();
        } else {
            byBirthdate = null;
        }

        Query byFullName;
        if (userSearchParameters.getFullName() != null) {
            byFullName = WildcardQuery.of(w -> w
                    .field("fullName")
                    .value(userSearchParameters.getFullName() + "*")
                    .caseInsensitive(true)
            )._toQuery();
        } else {
            byFullName = null;
        }

        Query byPhone;
        if (userSearchParameters.getPhone() != null) {
            String convertedPhone = PhoneConverter.convertPhoneFormat(userSearchParameters.getPhone());
            byPhone = TermQuery.of(t -> t
                    .field("phone")
                    .value(convertedPhone)
            )._toQuery();
        } else {
            byPhone = null;
        }

        Query byEmail;
        if (userSearchParameters.getEmail() != null) {
            String convertedEmail = userSearchParameters.getEmail().toLowerCase();
            byEmail = TermQuery.of(t -> t
                    .field("email")
                    .value(convertedEmail)
            )._toQuery();
        } else {
            byEmail = null;
        }

        SearchResponse<UserDocument> response = esClient.search(s -> s
                        .index("users")
                        .query(q -> q
                                .bool(b -> b
                                        .must(byBirthdate != null ? byBirthdate : Query.of(q2 -> q2.matchAll(m -> m)))
                                        .must(byFullName != null ? byFullName : Query.of(q2 -> q2.matchAll(m -> m)))
                                        .must(byPhone != null ? byPhone : Query.of(q2 -> q2.matchAll(m -> m)))
                                        .must(byEmail != null ? byEmail : Query.of(q2 -> q2.matchAll(m -> m)))
                                )
                        ).size(size)
                        .from(page)
                        .sort(sort -> sort
                                .field(field -> field
                                        .field(sortField)
                                        .order(direction.equalsIgnoreCase("asc") ?
                                                SortOrder.Asc : SortOrder.Desc)
                                )
                        )
                , UserDocument.class
        );

        //Getting the number of users found for debugging
        log.debug("Received {} search results", response.hits().total() != null ? response.hits().total().value() : 0);

        esClient.close();
        log.debug("The Elasticsearch client is closed");

        return response;
    }

}
