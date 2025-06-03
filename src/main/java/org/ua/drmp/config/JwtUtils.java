package org.ua.drmp.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.ua.drmp.dto.JwtProperties;

@Component
@RequiredArgsConstructor
public class JwtUtils {
	private final JwtProperties jwtProperties;

	public String generateJwtToken(UserDetails userDetails) {
		return Jwts.builder()
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + jwtProperties.getExpiration()))
			.signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()), SignatureAlgorithm.HS512)
			.compact();
	}

	public String getEmailFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(jwtProperties.getSecret().getBytes()).build()
			.parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(jwtProperties.getSecret().getBytes()).build().parseClaimsJws(authToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}
