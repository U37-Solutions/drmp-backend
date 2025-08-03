package org.ua.drmp.open;

import java.util.List;
import java.util.Optional;
import org.ua.drmp.company.entity.Office;

public interface PublicOfficeRepositoryCustom {
	List<Office> findOfficesWithinBounds(double lat1, double lon1, double lat2, double lon2);

	List<Office> searchWithFilters(String searchBy, String search,
		String regionName, String city,
		List<String> categories, List<String> services,
		Boolean isFree,
		Optional<double[]> boundaries);
}
