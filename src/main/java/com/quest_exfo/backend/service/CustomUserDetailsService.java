// package com.quest_exfo.backend.service;

// import com.quest_exfo.backend.entity.Member;
// import com.quest_exfo.backend.repository.MemberRepository;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import java.util.Collections;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {
//     private final MemberRepository memberRepository;

//     public CustomUserDetailsService(MemberRepository memberRepository){
//         this.memberRepository = memberRepository;
//     }

//     @Override
//     public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         Member member = memberRepository.findByEmail(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다: " + username));

//         return new CustomUserDetails(
//                 member.getEmail(),
//                 member.getName(),
//                 member.getPassword(),
//                 member.getMember_id(),
//                 member.getProfileImgPath(),
//                 Collections.singletonList(new SimpleGrantedAuthority("ROLE_" +member.getRole().name()))
//         );
//     }
// }

package com.quest_exfo.backend.service;

import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: " + username);
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found: " + username);
                    return new UsernameNotFoundException("회원 정보를 찾을 수 없습니다: " + username);
                });

        return new CustomUserDetails(
                member.getEmail(),
                member.getName(),
                member.getPassword(),
                member.getMember_id(),
                member.getProfileImgPath(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
        );
    }
}
