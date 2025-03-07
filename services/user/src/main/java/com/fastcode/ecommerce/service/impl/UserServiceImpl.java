package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.request.UserRequest;
import com.fastcode.ecommerce.model.dto.response.JwtClaims;
import com.fastcode.ecommerce.model.dto.response.UserResponse;
import com.fastcode.ecommerce.model.dto.response.UserResponseByToken;
import com.fastcode.ecommerce.model.entity.User;
import com.fastcode.ecommerce.model.entity.UserAccount;
import com.fastcode.ecommerce.repository.UserAccountRepository;
import com.fastcode.ecommerce.repository.UserRepository;
import com.fastcode.ecommerce.service.UserService;
import com.fastcode.ecommerce.utils.exceptions.ResourceNotFoundException;
import com.fastcode.ecommerce.utils.mapper.UserMapper;
import com.fastcode.ecommerce.utils.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserMapper userMapper;
    private final JwtServiceImpl jwtService;
    @Override
    public UserResponse create(UserRequest userRequest) {
        User user = userMapper.requestToEntity(userRequest);
        user = userRepository.save(user);
        return userMapper.entityToResponse(user);
    }

    @Override
    public Page<UserResponse> getAll(SearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Specification<User> specification = UserSpecification.getSpecification(request.getQuery());
        return userRepository.findAll(specification, pageable).map(userMapper::entityToResponse);
    }

    @Override
    public UserResponse getById(String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        UserAccount userAccount = userAccountRepository.findById(authentication.getPrincipal().toString()).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        User user = userRepository.findByUserAccountId(userAccount.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User not found"));

        if (!isAdmin && !user.getId().equals(id)) {
            throw new AccessDeniedException("You are not allowed to access this user");
        }

        return userMapper.entityToResponse(findByIdOrThrowNotFound(id));
    }


    @Override
    public UserResponse updatePut(UserRequest userRequest) {
        findByIdOrThrowNotFound(userRequest.getId());
        User user = userMapper.requestToEntity(userRequest);
        user = userRepository.save(user);
        return userMapper.entityToResponse(user);
    }

    @Override
    public UserResponse updatePatch(UserRequest userRequest) {
        User existingUser = findByIdOrThrowNotFound(userRequest.getId());

        if (userRequest.getFullName() != null) existingUser.setFullName(userRequest.getFullName());
        if (userRequest.getEmail() != null) existingUser.setEmail(userRequest.getEmail());
        if (userRequest.getPhone() != null) existingUser.setPhone(userRequest.getPhone());

        return userMapper.entityToResponse(userRepository.saveAndFlush(existingUser));
    }

    @Override
    public void deleteById(String id) {
        User existingUser = findByIdOrThrowNotFound(id);
        userRepository.delete(existingUser);
    }

    @Override
    public UserResponse deactivateUser(String id) {
        User existingUser = findByIdOrThrowNotFound(id);
        if(!existingUser.getUserAccount().getIsActive() == false) {
            throw new IllegalArgumentException("User is already disabled");
        }
        existingUser.getUserAccount().setIsActive(true);
        return userMapper.entityToResponse(userRepository.saveAndFlush(existingUser));
    }

    @Override
    public UserResponse activateUser(String id) {
        User existingUser = findByIdOrThrowNotFound(id);
        if(existingUser.getUserAccount().getIsActive() == true) {
            throw new IllegalArgumentException("User is already active");
        }
        existingUser.getUserAccount().setIsActive(true);
        return userMapper.entityToResponse(userRepository.saveAndFlush(existingUser));
    }

    @Override
    public UserResponseByToken getUserByToken(String token) {
        JwtClaims jwtClaims = jwtService.getClaimsByToken(token);
        User user = findByUserAccountIdOrThrowNotFound(jwtClaims.getUserAccountId());
        return UserResponseByToken.builder()
                .id(user.getId())
                .username(user.getUserAccount().getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getUserAccount().getRoles().stream().map(role -> role.getRole().name()).toList())
                .build();
    }

    private User findByIdOrThrowNotFound(String id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")
                );
    }

    private User findByUserAccountIdOrThrowNotFound(String id){
        return userRepository.findByUserAccountId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")
                );
    }
}
