package com.ducks.goodsduck.commons.service;

import com.ducks.goodsduck.commons.model.dto.*;
import com.ducks.goodsduck.commons.model.entity.SocialAccount;
import com.ducks.goodsduck.commons.model.entity.User;
import com.ducks.goodsduck.commons.model.enums.SocialType;
import com.ducks.goodsduck.commons.model.enums.UserRole;
import com.ducks.goodsduck.commons.repository.SocialAccountRepository;
import com.ducks.goodsduck.commons.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private static final String TOKEN_USAGE = "For member-checking";

    private final JwtService jwtService;
    private final OauthKakaoService oauthKakaoService;
    private final OauthNaverService oauthNaverService;

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;

    // 네이버 소셜로그인을 통한 유저 정보 반환
    public UserDto oauth2AuthorizationNaver(String code, String state) {
        AuthorizationNaverDto authorizationNaverDto = oauthNaverService.callTokenApi(code, state);

        // 소셜로그인 정보
        String userInfoFromNaver = oauthNaverService.callGetUserByAccessToken(authorizationNaverDto.getAccess_token());

        // 비회원 체크
        JSONObject jsonUserInfo = new JSONObject(userInfoFromNaver);
        JSONObject jsonResponseInfo = (JSONObject) jsonUserInfo.get("response");
        String userSocialAccountId = jsonResponseInfo.get("id").toString();

        log.info(userSocialAccountId);

        return socialAccountRepository.findById(userSocialAccountId)
                // socialAccount가 이미 등록되어 있는 경우, 기존 정보를 담은 userDto(USER) 반환
                .map( socialAccount -> {
                    User user = socialAccount.getUser();
                    UserDto userDto = new UserDto(user);
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setJwt(jwtService.createJwt(TOKEN_USAGE, new JwtDto(user.getId())));

                    return userDto;
                })
                // socialAccount가 등록되어 있지 않은 경우, userDto(ANONUMOUS) 반환
                .orElseGet( () -> {
                    UserDto userDto = new UserDto();
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setRole(UserRole.ANONYMOUS);

                    return userDto;
                });
    }

    // 카카오로 인증받기
    public UserDto oauth2AuthorizationKakao(String code) {

        AuthorizationKakaoDto authorizationKakaoDto = oauthKakaoService.callTokenApi(code);

        // 소셜로그인 정보
        String userInfoFromKakao = oauthKakaoService.callGetUserByAccessToken(authorizationKakaoDto.getAccess_token());

        // 비회원 체크
        JSONObject jsonUserInfo = new JSONObject(userInfoFromKakao);
        String userSocialAccountId = jsonUserInfo.get("id").toString();

        // 회원 로그인, 비회원 로그인 체크
        return socialAccountRepository.findById(userSocialAccountId)
                .map(socialAccount -> {
                    User user = socialAccount.getUser();
                    UserDto userDto = new UserDto(user);
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setJwt(jwtService.createJwt(TOKEN_USAGE, new JwtDto(user.getId())));

                    return userDto;
                })
                .orElseGet(() -> {
                    UserDto userDto = new UserDto();
                    userDto.setSocialAccountId(userSocialAccountId);
                    userDto.setRole(UserRole.ANONYMOUS);

                    return userDto;
                });
    }

    // 회원가입
    public UserDto signUp(UserSignUpRequest userSignUpRequest) {

        SocialAccount socialAccount = socialAccountRepository.save(
                new SocialAccount(
                        userSignUpRequest.getSocialAccountId(),
                        userSignUpRequest.getSocialAccountType()
                )
        );

        User user = userRepository.save(
                new User(userSignUpRequest.getNickName(),
                        userSignUpRequest.getEmail(),
                        userSignUpRequest.getPhoneNumber())
        );
        user.addSocialAccount(socialAccount);

        String jwt = jwtService.createJwt(TOKEN_USAGE, new JwtDto(user.getId()));

        UserDto userDto = new UserDto(user);
        userDto.setJwt(jwt);
        return userDto;
    }

    /** jwt 검증을 통한 유저 정보 반환 및 토큰 재발급 로직 */
    public UserDto checkLoginStatus(String token) {

        Map<String, Object> payloads = new HashMap<>();
        try {
            payloads = jwtService.getPayloads(token);
        } catch (JwtException e) {
            // 비밀키 상이(SignatureException), 토큰 정보 위조(MalformedJwtException) , 만료된 경우(ExpiredJwtException)
            log.debug("Cannot  ", e.getMessage());
            return UserDto.createUserDto(UserRole.ANONYMOUS);
        } catch (Exception e) {
            log.debug("", e.getMessage());
            return UserDto.createUserDto(UserRole.ANONYMOUS);
        }

        // 토큰의 만료 기한이 다 된 경우
        Long userId = Long.valueOf((Integer) payloads.get("userId"));

        return userRepository.findById(userId)
                .map(user -> {
                    user.updateLastLoginAt();
                    UserDto userDto = new UserDto(user);
                    userDto.setJwt(jwtService.createJwt(TOKEN_USAGE, new JwtDto(user.getId())));

                    return userDto;
                })
                .orElseGet(() -> UserDto.createUserDto(UserRole.ANONYMOUS));
    }

    public UserDto find(Long user_id) {
        return userRepository.findById(user_id)
                .map(user -> new UserDto(user))
                .orElseGet(() -> UserDto.createUserDto(UserRole.ANONYMOUS)); // user를 못찾으면 빈 UserDto(UserRole.ANONYMOUS) 반환
    }

    // 유저 전체 리스트 조회
    public List<UserDto> findAll(){
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDto(user))
                .collect(Collectors.toList());
    }
}
