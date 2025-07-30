package org.ua.drmp.open;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.CompanyMapper;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Office;

@Service
@RequiredArgsConstructor
public class PublicMapServiceImpl implements PublicMapService {

	private final PublicOfficeRepository officeRepository;
	private final CompanyMapper companyMapper;
	@Override
	public List<PublicMapPointDto> getOfficesInBounds(Optional<double[]> boundaries) {
		List<Office> offices;

		if (boundaries.isPresent() && boundaries.get().length == 4) {
			double[] b = boundaries.get();
			offices = officeRepository.findOfficesWithinBounds(b[0], b[1], b[2], b[3]);
		} else {
			offices = officeRepository.findAllWithCoordinates();
		}

		return offices.stream().map(this::mapToDto).toList();
	}

	private PublicMapPointDto mapToDto(Office office) {
		Company company = office.getCompany();
		return PublicMapPointDto.builder()
			.id(office.getId())
			.locationName(office.getLocationName())
			.latitude(office.getLatitude())
			.longitude(office.getLongitude())
			.companyName(company.getName())
			.contactName(company.getContactName())
			.phone(company.getPhone())
			.email(company.getEmail())
			.socials(company.getSocials().stream()
				.map(companyMapper::mapSocial)
				.toList())
			.build();
	}
}
