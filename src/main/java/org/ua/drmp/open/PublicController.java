package org.ua.drmp.open;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.company.service.CompanyService;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
	private final CompanyService companyService;
	private final PublicMapService publicMapService;

	@GetMapping("/companies")
	public ResponseEntity<List<PublicCompanyDto>> fetchAllPublicCompanies() {
		return ResponseEntity.ok(companyService.fetchAllPublicCompanies());
	}

	@GetMapping("/map-points")
	public List<PublicMapPointDto> getMapPoints(
		@RequestParam(required = false) String boundaries,
		@RequestParam(required = false) String search_by,
		@RequestParam(required = false) String search,
		@RequestParam(required = false) Long regionId,
		@RequestParam(required = false) String city,
		@RequestParam(required = false) List<String> categories,
		@RequestParam(required = false) List<String> services,
		@RequestParam(required = false) Boolean isFree
	) {
		Optional<double[]> parsedBounds = Optional.empty();

		if (boundaries != null && !boundaries.isBlank()) {
			String[] parts = boundaries.split(",");
			if (parts.length == 4) {
				double[] coords = Arrays.stream(parts)
					.mapToDouble(Double::parseDouble)
					.toArray();
				parsedBounds = Optional.of(coords);
			}
		}

		return publicMapService.searchOffices(
			search_by, search,
			regionId, city,
			categories, services,
			isFree,
			parsedBounds
		);
	}
}
