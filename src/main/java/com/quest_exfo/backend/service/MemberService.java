package com.quest_exfo.backend.service;

import com.quest_exfo.backend.dto.request.MemberLoginDTO;
import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.dto.request.MemberUpdateDTO;
import com.quest_exfo.backend.dto.response.MemberResponseDTO;
import com.quest_exfo.backend.dto.response.MemberTokenDTO;
import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.MemberRepository;
import com.quest_exfo.backend.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class MemberService implements MemberServiceItf{
    //    메소드 정의 클래스
//    수정 시 이 클래스만 수정해도 문제없도록

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public HttpStatus checkEmailDuplicate(String email){
        boolean exist = memberRepository.existsByEmail(email);
        if(exist){
            return HttpStatus.CONFLICT; //409 error = 이메일 이미 존재
        }
        return HttpStatus.OK; //200 OK = 이메일 사용 가능
    }

    @Override
    public MemberResponseDTO signup(MemberSignupDTO signupDTO) {
        boolean exist = memberRepository.existsByEmail(signupDTO.getEmail());
        if (exist) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        checkPwd(signupDTO.getPassword(), signupDTO.getPasswordCheck());

        //비밀번호 암호화해서 저장
        String encodePwd = passwordEncoder.encode(signupDTO.getPassword());
        signupDTO.setPassword(encodePwd);

        //DTO > Entity로 변환해서 저장
        Member newMember = memberRepository.save(MemberSignupDTO.ofEntity(signupDTO));

        //응답(비밀번호 제외)
        return MemberResponseDTO.fromEntity(newMember);
    }

    @Override
    public MemberTokenDTO login(MemberLoginDTO loginDTO) {
        try {
            // 이메일과 비밀번호로 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            // 인증된 사용자 정보 가져오기
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = memberRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

            // JWT 엑세스 토큰 생성
            String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
            // 리프레시 토큰 생성
            String refreshToken = jwtTokenProvider.generateRefreshToken(member.getEmail());

            // 토큰과 사용자 정보를 포함한 응답 DTO 생성
            return MemberTokenDTO.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .email(member.getEmail())
                    .name(member.getName())
                    .member_id(member.getMember_id())
                    .build();
        } catch (AuthenticationException e) {
            throw new RuntimeException("로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.");
        }
    }

    @Override
    public MemberResponseDTO check(Member member, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return MemberResponseDTO.fromEntity(member);
    }


    @Override
    public MemberResponseDTO update(Member member, MemberUpdateDTO updateDTO) {
        // 비밀번호 체크
        if (!passwordEncoder.matches(updateDTO.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        //바꿀 비밀번호 한번더 확인
        checkPwd(updateDTO.getNewPassword(), updateDTO.getNewPasswordCheck());
        //바꿀 비밀번호 암호화
        String encodePwd = passwordEncoder.encode(updateDTO.getNewPassword());

        //바꾼 비밀번호 , 이름 저장
        member.update(encodePwd, updateDTO.getNewName(),updateDTO.getNewProfileImg());

        return MemberResponseDTO.fromEntity(member);
    }

    private void checkPwd(String password, String passwordCheck){
        if(!password.equals(passwordCheck)){
            throw new RuntimeException("패스워드가 일치하지 않습니다");
        }
    }

}
