package org.ua.drmp.company.dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.CompanyStatus;

@Component
public class CompanyStatusConverter implements Converter<String, CompanyStatus> {
	@Override
	public CompanyStatus convert(String source) {
		return CompanyStatus.valueOf(source.toUpperCase());
	}
}
