package dev.alvaroherrero.funkosb.auth.services.authentication;

import dev.alvaroherrero.funkosb.auth.dto.JwtAuthResponse;
import dev.alvaroherrero.funkosb.auth.dto.UserSignInRequest;
import dev.alvaroherrero.funkosb.auth.dto.UserSignUpRequest;

public interface IAuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
