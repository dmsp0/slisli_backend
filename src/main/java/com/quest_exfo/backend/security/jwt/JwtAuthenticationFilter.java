 package com.quest_exfo.backend.security.jwt;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import java.io.IOException;

// @Component
// public class JwtAuthenticationFilter  extends OncePerRequestFilter {
//     //jwt 토큰 유효성 검사&인증
//     private final JwtTokenProvider jwtTokenProvider;

//     public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider){
//         this.jwtTokenProvider=jwtTokenProvider;
//     }

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//         String token = resolveToken(request);
//         if(token != null && jwtTokenProvider.validateToken(token)){
//             String username = jwtTokenProvider.getUsername(token);
//             // 여기서 사용자 정보를 로드하고 권한을 설정합니다.
// //             UserDetails userDetails = userDetailsService.loadUserByUsername(username);
// //             UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
// //             auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
// //             SecurityContextHolder.getContext().setAuthentication(auth);
//         }
//         filterChain.doFilter(request,response);
//     }

//     private String resolveToken(HttpServletRequest request){
//         String bearerToken = request.getHeader("Authorization");
//         if(bearerToken !=null && bearerToken.startsWith("Bearer ")){
//             return bearerToken.substring(7);
//         }
//         return null;
//     }
// }






import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.quest_exfo.backend.service.CustomUserDetails;
import com.quest_exfo.backend.service.CustomUserDetailsService;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        logger.info("Extracted Token: " + token);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                logger.info("Username from token: " + username);

                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("User authenticated: " + username);
            } else {
                logger.warn("Token validation failed or token is null.");
            }
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
