package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.LoginRequest;
import com.example.demo.domain.user.dto.RegisterRequest;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void signup(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .role("USER")
                .build();

        userRepository.save(user);
    }

    @Transactional
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 혹은 비밀번호를 다시 확인하세요."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 혹은 비밀번호를 다시 확인하세요.");
        }

        String token = jwtTokenProvider.createToken(user);

        return token;
    }

    public User processOAuth2User(OAuth2User oauthUser) {
        // OAuth2 사용자 정보 처리 로직
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .username(name)
                        .password("") // OAuth2 로그인에서는 비밀번호가 없으므로 빈 문자열
                        .role("ROLE_USER")
                        .build())
                );
        return user;
    }
}
