package net.regnology.lucy.web.rest.v1;

import net.regnology.lucy.security.jwt.TokenProvider;
import net.regnology.lucy.web.rest.UserJWTController;
import net.regnology.lucy.web.rest.vm.LoginVM;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Custom Controller to authenticate user ({@link LoginVM})
 * where changes are inserted that are different from the base class generated by the JHipster generator.<br>
 * API base path: {@code /api/v1}
 */
@RestController
@RequestMapping("/api/v1")
public class UserJWTCustomController extends UserJWTController {

    public UserJWTCustomController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        super(tokenProvider, authenticationManagerBuilder);
    }
}
