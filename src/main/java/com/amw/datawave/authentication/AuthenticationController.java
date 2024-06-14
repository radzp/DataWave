package com.amw.datawave.authentication;

import com.amw.datawave.user.User;
import com.amw.datawave.user.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.authenticate(request, response);
        return ResponseEntity.ok(authResponse);
    }

    // check if user is authenticated
    @GetMapping("/check")
    public ResponseEntity<?> check(HttpServletRequest request) {
        boolean isAuthenticated = authenticationService.checkAuthentication(request);
        if (isAuthenticated) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
    }
}
