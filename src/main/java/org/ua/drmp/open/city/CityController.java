package org.ua.drmp.open.city;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionary/cities")
@RequiredArgsConstructor
public class CityController {

	private final CityService cityService;

	@GetMapping("/{regionId}")
	public List<CityEntryDto> getCities(@PathVariable int regionId) {
		return cityService.getCitiesByRegionId(regionId);
	}
}
