package com.quest_exfo.backend.service.member;

import com.quest_exfo.backend.common.ResourceNotFoundException;
import com.quest_exfo.backend.dto.request.MemberDeleteDTO;
import com.quest_exfo.backend.dto.request.MemberLoginDTO;
import com.quest_exfo.backend.dto.request.MemberSignupDTO;
import com.quest_exfo.backend.dto.request.MemberUpdateDTO;
import com.quest_exfo.backend.dto.response.MemberResponseDTO;
import com.quest_exfo.backend.dto.response.MemberTokenDTO;
import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.MemberRepository;
import com.quest_exfo.backend.security.jwt.JwtTokenProvider;
import java.io.IOException;
import java.util.UUID;

import com.quest_exfo.backend.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


@Service
public class MemberServiceImpl implements MemberService {
    //    메소드 정의 클래스
//    수정 시 이 클래스만 수정해도 문제없도록

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public MemberServiceImpl(PasswordEncoder passwordEncoder, MemberRepository memberRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    private S3Client s3Client;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;


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
    public MemberResponseDTO update(Member member, MemberUpdateDTO updateDTO, MultipartFile file) throws IOException {
        checkPwd(updateDTO.getPassword(), updateDTO.getPasswordCheck());
        String encodePwd = passwordEncoder.encode(updateDTO.getPassword());
        Member updateMember =  memberRepository.findByEmail(updateDTO.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Member", "Member Email", member.getEmail())
        );
        // 비밀번호 업데이트
        updateMember.setPassword(encodePwd);

        // 이름 업데이트
        updateMember.setName(updateDTO.getName());

        // UUID + 파일명으로 파일명 중복 처리
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = "member_profile_img/" + UUID.randomUUID().toString() + "_" + originalFileName;

        // 파일을 S3에 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(uniqueFileName)
            .build();

        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        if (putObjectResponse.sdkHttpResponse().isSuccessful()) {
            String fileUrl = s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(uniqueFileName)).toExternalForm();
            updateMember.setProfileImgPath(fileUrl);
        } else {
            throw new IOException("Could not upload file to S3");
        }

        // 데이터베이스에 변경 사항 저장
        memberRepository.save(updateMember);

        // 업데이트된 회원 정보를 DTO로 변환하여 반환
        return MemberResponseDTO.fromEntity(updateMember);
    }

    private void checkPwd(String password, String passwordCheck){
        if(!password.equals(passwordCheck)){
            throw new RuntimeException("패스워드가 일치하지 않습니다");
        }
    }

    // private void checkEncodePassword(String rawPassword, String encodedPassword) {
    //     if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
    //         throw new RuntimeException("패스워드 불일치");
    //     }
    // }
    public MemberResponseDTO delete(Member member, MemberDeleteDTO deleteDTO) {
        Member deleteMember = memberRepository.findById(deleteDTO.getMember_id()).orElseThrow(
                ()-> new ResourceNotFoundException("Member", "Member ID", String.valueOf(member.getMember_id()))
        );
        memberRepository.delete(deleteMember);
        return MemberResponseDTO.fromEntity(deleteMember);
    }

}