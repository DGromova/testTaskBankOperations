//package com.bank.security;

import com.bank.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByLogin(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .map(user -> new User(user.getLogin(),
                        user.getPassword())
                ).orElseThrow(() -> new UsernameNotFoundException(String.format("%s - not found", login)));
    }

}*/
