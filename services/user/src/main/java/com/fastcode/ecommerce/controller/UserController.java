package com.fastcode.ecommerce.controller;

import com.fastcode.ecommerce.constant.APIUrl;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.request.UserRequest;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.model.dto.response.PagingResponse;
import com.fastcode.ecommerce.model.dto.response.UserResponse;
import com.fastcode.ecommerce.model.dto.response.UserResponseByToken;
import com.fastcode.ecommerce.service.UserService;
import com.fastcode.ecommerce.utils.validation.PagingUtil;
import com.fastcode.ecommerce.utils.validation.SortingUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.USER_API)
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(name = "search",required = false) String search,
            @RequestParam(required = false,defaultValue = "1") String page,
            @RequestParam(required = false,defaultValue = "10") String size,
            @RequestParam(required = false,defaultValue = "asc") String direction,
            @RequestParam(required = false,defaultValue = "fullName") String sortBy
    ) {
        Integer safePage = PagingUtil.validatePage(page);
        Integer safeSize = PagingUtil.validateSize(size);
        direction = PagingUtil.validateDirection(direction);
        sortBy = SortingUtil.sortByValidation(UserResponse.class, sortBy, "fullName");

        SearchRequest request = SearchRequest.builder()
                .query(search)
                .page(Math.max(safePage-1,0))
                .size(safeSize)
                .direction(direction)
                .sortBy(sortBy)
                .build();

        Page<UserResponse> users = userService.getAll(request);
        PagingResponse paging = PagingResponse.builder()
                .totalPages(users.getTotalPages())
                .totalElement(users.getTotalElements())
                .page(request.getPage()+1)
                .size(request.getSize())
                .hashNext(users.hasNext())
                .hashPrevious(users.hasPrevious())
                .build();

        CommonResponse<List<UserResponse>> response = CommonResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All users retrieved")
                .data(users.getContent())
                .paging(paging)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseByToken> getUser(@RequestHeader("Authorization") String token) {
        UserResponseByToken user = userService.getUserByToken(token);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<CommonResponse<UserResponse>> getUserById (@PathVariable String id) {
        UserResponse user = userService.getById(id);

        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User retrieved")
                .data(user)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponse>> updatedUser(@Valid @RequestBody UserRequest request){
        UserResponse user = userService.updatePut(request);

        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User updated")
                .data(user)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PatchMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponse>> updatedUserPartially(@Valid @RequestBody UserRequest request){
        UserResponse user = userService.updatePatch(request);

        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User partially updated")
                .data(user)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<String>> deleteById(@PathVariable String id){
        userService.deleteById(id);

        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User deleted successfully")
                .data("User with ID: " + id + " has been deleted")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PutMapping(path = "/deactivate/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponse>> deactivateUser(@PathVariable String id) {
        UserResponse user = userService.deactivateUser(id);
        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User is disabled")
                .data(user)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PutMapping(path = "/activate/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<UserResponse>> activateUser(@PathVariable String id) {
        UserResponse user = userService.activateUser(id);
        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User is activated")
                .data(user)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }
}

