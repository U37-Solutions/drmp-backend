package org.ua.drmp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.ua.drmp.dto.JwtProperties;

@Component
@RequiredArgsConstructor
public class JwtUtils {
	private final JwtProperties jwtProperties;

	public String generateAccessToken(UserDetails userDetails) {
		return Jwts.builder()
			.setSubject(userDetails.getUsername())
			.signWith(getSignKey(), SignatureAlgorithm.HS256)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
			.compact();
	}

	public String generateRefreshToken(UserDetails userDetails) {
		return Jwts.builder()
			.setSubject(userDetails.getUsername())
			.signWith(getSignKey(), SignatureAlgorithm.HS256)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration() * 5))
			.compact();
	}

	public String getEmailFromJwtToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSignKey())
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(authToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Date getExpirationDateFromToken(String token) {
		Claims claims = Jwts
			.parserBuilder()
			.setSigningKey(getSignKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
		return claims.getExpiration();
	}

	private Key getSignKey() {
		byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
