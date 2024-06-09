package com.amw.datawave.authentication;

import com.amw.datawave.exception.EmailAlreadyExistsException;
import com.amw.datawave.jwt.JwtService;
import com.amw.datawave.user.Role;
import com.amw.datawave.user.User;
import com.amw.datawave.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest registerRequest) {
        try {
            User user = User.builder()
                    .name(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            String token = jwtService.generateToken(user);
            return new AuthenticationResponse(token);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("User with this email already exists");
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(user);

        Date jwtIssuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
        Date jwtExpiration = jwtService.extractClaim(token, Claims::getExpiration);
        long jwtDuration = (jwtExpiration.getTime() - jwtIssuedAt.getTime()) / 1000;

        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setHttpOnly(true); // zabezpieczenie przed atakami XSS
        jwtCookie.setSecure(true); // zabezpieczenie przed atakami CSRF
        jwtCookie.setMaxAge((int) jwtDuration); // czas życia ciasteczka
        jwtCookie.setPath("/"); // "/", oznacza, że ciasteczko będzie dostępne dla całej strony

        // dodanie ciasteczka do odpowiedzi
        response.addCookie(jwtCookie);

        return new AuthenticationResponse(token);
    }

}
