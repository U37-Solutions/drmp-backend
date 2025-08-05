package org.ua.drmp.open;

import java.util.List;
import java.util.Optional;

public interface PublicMapService {
	List<PublicMapPointDto> searchOffices(String searchBy, String search,
		Long regionId, String city,
		List<Long> categoryIds, List<Long> serviceIds,
		Boolean isFree,
		Optional<double[]> boundaries);

}
