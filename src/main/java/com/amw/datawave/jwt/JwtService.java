package com.amw.datawave.jwt;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "4a66e14d349d8837959f5b51641140e354240cb92e15e4fef1d6ca10f7f4f432";
    /*
     * extractUsername(String token): Ta metoda jest używana do
     * wyodrębnienia nazwy użytkownika (username) z podanego tokena JWT.
     * Wykorzystuje ona metodę extractClaim do uzyskania roszczenia (claim) o nazwie "subject" z tokena,
     * które często jest używane do przechowywania nazwy użytkownika.
     */
    public String extractUsername(String token) { //this method will extract the username from the JWT token
        return extractClaim(token, Claims::getSubject);
    }


    /*
     * extractClaim(String token, Function<Claims, T> claimsResolver): Ta metoda jest używana do wyodrębnienia
     * konkretnego roszczenia z tokena JWT. Najpierw wywołuje metodę extractAllClaims(token),
     * aby uzyskać wszystkie roszczenia z tokena jako obiekt Claims.
     * Następnie wywołuje funkcję claimsResolver na obiekcie Claims, aby uzyskać konkretną wartość roszczenia.
     *
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { //this method will extract the claim from the JWT token
        final Claims claims = extractAllClaims(token); //we will extract all the claims from the JWT token using the extractAllClaims method
        return claimsResolver.apply(claims); //we will return the claim from the JWT token using the claimsResolver
    }

    /*
    generateToken(UserDetails userDetails): Ta metoda jest używana do generowania tokena JWT dla podanego użytkownika.
    Wywołuje ona inną metodę generateToken z pustą mapą jako dodatkowymi roszczeniami.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /*
    generateToken(Map<String, Object> extraClaims, UserDetails userDetails): Ta metoda jest używana do generowania
    tokena JWT z dodatkowymi roszczeniami i szczegółami użytkownika.
    Używa ona biblioteki JJWT do budowania i podpisywania tokena.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) //helps to check if the token is still valid
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)) //the token will expire after 1 hour
//                .setExpiration(new Date(System.currentTimeMillis() + 1000)) //the token will expire after 1 second (DEBUGGING PURPOSE)
                .signWith(getSingInKey(), SignatureAlgorithm.HS256) //signing key which is used to verify that the sender of the JWT is who it claims to be and ensures that the message was not changed along the way, ensure that the same person who sent the JWT signed it with the same key
                .compact(); //generates and returns the JWT token
    }

    /*
     * isTokenValid(String token, UserDetails userDetails): Ta metoda sprawdza,
     * czy podany token JWT jest ważny dla podanego użytkownika. Sprawdza, czy nazwa użytkownika w tokenie jest taka sama
     * jak nazwa użytkownika w szczegółach użytkownika i czy token nie jest wygasły.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) { //method that validates if the token is here belong to that userDetails right here
        final String username = extractUsername(token); //we will extract the username from the JWT token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); //we will check if the username from the JWT token is equal to the username from the userDetails and if the token is not expired

    }
    /*
     * isTokenExpired(String token): Ta metoda sprawdza, czy podany token JWT jest wygasły.
     * Wywołuje ona metodę extractExpiration do uzyskania daty wygaśnięcia tokena, a następnie porównuje ją z obecną datą.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); //we will check if the token is expired
    }

    /*
     * extractExpiration(String token): Ta metoda jest używana do wyodrębnienia daty wygaśnięcia z podanego tokena JWT.
     * Wywołuje ona metodę extractClaim do uzyskania roszczenia o nazwie "expiration" z tokena.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); //this method will extract the expiration date from the JWT token
    }


    /*
     * extractAllClaims(String token): Ta metoda jest używana do wyodrębnienia wszystkich roszczeń z podanego tokena JWT.
     *  Używa ona biblioteki JJWT do parsowania tokena i zwraca ciało tokena jako obiekt Claims.
     */

    private Claims extractAllClaims(String token) { //this method will extract all the claims from the JWT token
        return Jwts
                .parserBuilder() //we will build the parser
                .setSigningKey(getSingInKey()) //signing key which is used to verify that the sender of the JWT is who it claims to be and ensures that the message was not changed along the way, ensure that the same person who sent the JWT signed it with the same key
                .build() //we will build the parser
                .parseClaimsJws(token)  //we will parse the claims
                .getBody(); //this method will return the body of the JWT token
    }

    /*
     * getSingInKey(): Ta metoda zwraca klucz, który jest używany do podpisywania tokenów JWT.
     * Klucz jest generowany na podstawie tajnego klucza,
     * który jest dekodowany z formatu Base64.
     */
    private Key getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); //we will decode the secret key
        return Keys.hmacShaKeyFor(keyBytes); //we will return the secret key as a Key object that will be used to verify the JWT token
    }
}