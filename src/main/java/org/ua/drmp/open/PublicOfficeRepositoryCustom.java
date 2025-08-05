package org.ua.drmp.open;

import java.util.List;
import java.util.Optional;
import org.ua.drmp.company.entity.Office;

public interface PublicOfficeRepositoryCustom {
	List<Office> findOfficesWithinBounds(double lat1, double lon1, double lat2, double lon2);

	List<Office> searchWithFilters(String searchBy, String search,
		Long regionId, String city,
		List<Long> categoryIds, List<Long> serviceIds,
		Boolean isFree,
		Optional<double[]> boundaries);
}
