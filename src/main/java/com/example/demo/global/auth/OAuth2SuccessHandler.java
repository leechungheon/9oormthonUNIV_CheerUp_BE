package com.example.demo.global.auth;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
        User user = userService.processOAuth2User(oauth2User);

        // Redirect to internal test page with user ID
        response.sendRedirect("/api/users/test?userId=" + user.getId());
    }
}
