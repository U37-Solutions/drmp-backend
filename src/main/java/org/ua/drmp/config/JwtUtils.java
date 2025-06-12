package org.ua.drmp.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.ua.drmp.dto.JwtProperties;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

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

	public Optional<String> tryGetEmail(String token) {
		try {
			return Optional.of(getClaims(token).getSubject());
		} catch (JwtException | IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	public boolean validateJwtToken(String token) {
		try {
			getClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaims(token).getExpiration();
	}

	private Claims getClaims(String token) {
		return Jwts
			.parserBuilder()
			.setSigningKey(getSignKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	private Key getSignKey() {
		byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
