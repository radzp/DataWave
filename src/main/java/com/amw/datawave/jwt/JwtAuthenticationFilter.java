package com.amw.datawave.jwt;

import com.amw.datawave.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Pobieram ciasteczko z tokenem JWT
        Cookie jwtCookie = null;
        if (request.getCookies() != null) {
            jwtCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> "jwtToken".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);
        }

        String jwtToken = null;
        String userEmail = null;

        // Sprawdzam czy ciasteczko z tokenem JWT istnieje
        if(jwtCookie != null){
            jwtToken = jwtCookie.getValue();
            userEmail = jwtService.extractUsername(jwtToken);
        }

        // Gdy uzytkowniik istnieje i nie jest zalogowany
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // Pobieram dane uzytkownika z bazy danych
            UserDetails userDetails = userService.loadUserByUsername(userEmail);
            // Sprawdzam czy token jest poprawny, argumenty to token i dane uzytkownika,
            // dane uzytkownika sa potrzebne do sprawdzenia czy token jest przypisany do tego uzytkownika
            if (jwtService.isTokenValid(jwtToken, userDetails)){
                // Tworze obiekt UsernamePasswordAuthenticationToken, ktory jest potrzebny do zalogowania uzytkownika
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Ustawiam obiekt UsernamePasswordAuthenticationToken w SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("User {} has been authenticated", userEmail);
            }
        }


        filterChain.doFilter(request, response);
    }
}
