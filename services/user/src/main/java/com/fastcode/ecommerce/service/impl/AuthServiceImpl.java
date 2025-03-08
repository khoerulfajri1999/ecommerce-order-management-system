package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.constant.FormatValidation;
import com.fastcode.ecommerce.constant.UserRole;
import com.fastcode.ecommerce.model.dto.request.AuthRequest;
import com.fastcode.ecommerce.model.dto.request.RegisterRequest;
import com.fastcode.ecommerce.model.dto.request.UserRequest;
import com.fastcode.ecommerce.model.dto.response.LoginResponse;
import com.fastcode.ecommerce.model.dto.response.RegisterResponse;
import com.fastcode.ecommerce.model.entity.Role;
import com.fastcode.ecommerce.model.entity.User;
import com.fastcode.ecommerce.model.entity.UserAccount;
import com.fastcode.ecommerce.repository.UserAccountRepository;
import com.fastcode.ecommerce.service.AuthService;
import com.fastcode.ecommerce.service.JwtService;
import com.fastcode.ecommerce.service.RoleService;
import com.fastcode.ecommerce.service.UserService;
import com.fastcode.ecommerce.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse register(RegisterRequest request) {
        validateRegisterRequest(request);
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // TODO: get or insert new Role
        Role userRole = roleService.getOrSave(UserRole.USER);

        // TODO: insert new userAccount
        UserAccount userAccount = UserAccount.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .roles(List.of(userRole))
                .isActive(true)
                .build();
        userAccount = userAccountRepository.saveAndFlush(userAccount);
        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .userAccount(userAccount)
                .build();
        UserRequest userRequest = userMapper.entityToRequest(user);
        userService.create(userRequest);
        return RegisterResponse.builder()
                .userId(userAccount.getId())
                .fullName(user.getFullName())
                .roles(List.of(userRole.getRole().toString()))
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse registerAdmin(RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        validateRegisterRequest(request);

        // TODO: get or insert new Role
        Role userRole = roleService.getOrSave(UserRole.ADMIN);

        // TODO: insert new userAccount
        UserAccount userAccount = UserAccount.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .roles(List.of(userRole))
                .isActive(true)
                .build();
        userAccount = userAccountRepository.saveAndFlush(userAccount);
        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .userAccount(userAccount)
                .build();
        UserRequest userRequest = userMapper.entityToRequest(user);
        userService.create(userRequest);
        return RegisterResponse.builder()
                .userId(userAccount.getId())
                .fullName(user.getFullName())
                .roles(List.of(userRole.getRole().toString()))
                .build();
    }


    @Override
    public LoginResponse login(AuthRequest request) {
        System.out.println("Received login request for user: " + request.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        Authentication authenticate = authenticationManager.authenticate(authentication);
        if (!authenticate.isAuthenticated()) {
            throw new BadCredentialsException("Wrong username or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        System.out.println("User authenticated successfully.");

        if (!(authenticate.getPrincipal() instanceof UserAccount userAccount)) {
            throw new BadCredentialsException("Invalid user details");
        }

        String token = jwtService.generateToken(userAccount);
        System.out.println("Generated token successfully.");

        return LoginResponse.builder()
                .userId(userAccount.getId())
                .username(userAccount.getUsername())
                .token(token)
                .role(userAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (!Pattern.matches(FormatValidation.INDONESIAN_PHONE_REGEX, request.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format. Must be an Indonesian number.");
        }
        if (!Pattern.matches(FormatValidation.EMAIL_REGEX, request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
    }

}
