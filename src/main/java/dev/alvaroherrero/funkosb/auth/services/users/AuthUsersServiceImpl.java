package dev.alvaroherrero.funkosb.auth.services.users;

import dev.alvaroherrero.funkosb.auth.repository.IAuthUsersRepository;
import dev.alvaroherrero.funkosb.users.exceptions.UserNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthUsersServiceImpl implements IAuthUsersService{

    private final IAuthUsersRepository authUsersRepository;

    @Autowired
    public AuthUsersServiceImpl(IAuthUsersRepository authUsersRepository) {
        this.authUsersRepository = authUsersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("Usuario con username " + username + " no encontrado"));
    }
}
