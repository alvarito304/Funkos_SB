package dev.alvaroherrero.funkosb.auth.services.users;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAuthUsersService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);
}
