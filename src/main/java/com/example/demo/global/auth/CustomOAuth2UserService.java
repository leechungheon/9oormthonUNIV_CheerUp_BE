package com.example.demo.global.auth;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
          String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email;
        String name;
        
        System.out.println("OAuth Provider: " + registrationId);
        
        if ("naver".equals(registrationId)) {
            // 네이버의 경우 response 객체 안에 정보가 있음
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            name = (String) response.get("name");
        } else if ("kakao".equals(registrationId)) {
            // 카카오의 경우 kakao_account 객체 안에 정보가 있음
            @SuppressWarnings("unchecked")
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
        } else {
            // 구글의 경우 기존 로직
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");        }
        
        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // 기존 사용자의 provider 정보 업데이트
                    System.out.println("기존 사용자 발견 - 이메일: " + email + ", 기존 Provider: " + existingUser.getProvider() + ", 새 Provider: " + registrationId);
                    if (existingUser.getProvider() == null || !existingUser.getProvider().equals(registrationId)) {
                        existingUser.setProvider(registrationId);
                        User savedUser = userRepository.save(existingUser);
                        System.out.println("Provider 업데이트 완료: " + savedUser.getProvider());
                        return savedUser;
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    System.out.println("새 사용자 생성 - 이메일: " + email + ", Provider: " + registrationId);
                    
                    // username을 고유하게 만들기 위해 제공자와 조합
                    String uniqueUsername = generateUniqueUsername(name, registrationId, email);
                    
                    User newUser = User.builder()
                            .email(email)
                            .username(uniqueUsername)
                            .password("")
                            .role("ROLE_USER")
                            .provider(registrationId) // OAuth 제공자 정보 저장
                            .build();
                    User savedUser = userRepository.save(newUser);
                    System.out.println("새 사용자 저장 완료 - Username: " + savedUser.getUsername() + ", Provider: " + savedUser.getProvider());
                    return savedUser;
                });

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );

        // Return CustomOAuth2User with attached User to reuse in success handler
        return new CustomOAuth2User(authorities, attributes, userNameAttributeName, user);
    }
      /**
     * 고유한 username을 생성하는 메서드
     * 중복을 방지하기 위해 제공자와 함께 조합하거나 이메일의 일부를 사용
     */
    private String generateUniqueUsername(String name, String provider, String email) {
        // 1차: 이름_제공자 형태로 시도
        String baseUsername = name + "_" + provider;
        
        // 중복 확인
        if (userRepository.findByUsername(baseUsername).isEmpty()) {
            return baseUsername;
        }
        
        // 2차: 이메일의 @ 앞부분 + 제공자
        String emailPrefix = email.split("@")[0];
        String emailBasedUsername = emailPrefix + "_" + provider;
        
        if (userRepository.findByUsername(emailBasedUsername).isEmpty()) {
            return emailBasedUsername;
        }
        
        // 3차: 타임스탬프를 추가해서 고유성 보장
        String timestampUsername = name + "_" + provider + "_" + System.currentTimeMillis();
        return timestampUsername;
    }
}
