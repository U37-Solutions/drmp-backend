package org.ua.drmp.config;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class CustomGrantedAuthority implements GrantedAuthority {

	private final String authority;

	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CustomGrantedAuthority that)) return false;
		return authority.equals(that.authority);
	}

	@Override
	public int hashCode() {
		return authority.hashCode();
	}
}
