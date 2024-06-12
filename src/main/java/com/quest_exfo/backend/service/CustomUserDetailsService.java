package com.quest_exfo.backend.service;

import com.quest_exfo.backend.entity.Member;
import com.quest_exfo.backend.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다: " + username));

        return new CustomUserDetails(
                member.getEmail(),
                member.getName(),
                member.getPassword(),
                member.getMember_id(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" +member.getRole().name()))
        );
    }
}
