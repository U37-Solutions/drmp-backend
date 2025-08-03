package org.ua.drmp.open;

import java.util.List;
import java.util.Optional;

public interface PublicMapService {
	List<PublicMapPointDto> searchOffices(String searchBy, String search,
		String regionName, String city,
		List<String> categories, List<String> services,
		Boolean isFree,
		Optional<double[]> boundaries);

}
