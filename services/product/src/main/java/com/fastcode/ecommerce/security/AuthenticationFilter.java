package com.fastcode.ecommerce.security;

import com.fastcode.ecommerce.client.UserServiceClient;
import com.fastcode.ecommerce.model.dto.response.JwtClaims;
import com.fastcode.ecommerce.model.dto.response.UserResponse;
import com.fastcode.ecommerce.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;
    private final String AUTH_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader(AUTH_HEADER);
        if (bearerToken != null && jwtService.verifyJwtToken(bearerToken)) {
            JwtClaims jwtClaims = jwtService.getClaimsByToken(bearerToken);
            UserResponse userAccount = userServiceClient.getUserByToken(bearerToken);
            System.out.println(userAccount);

            List<GrantedAuthority> authorities = userAccount.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());


            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userAccount.getUsername(),
                    null,
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}