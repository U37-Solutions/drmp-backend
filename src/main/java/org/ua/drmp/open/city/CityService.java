package org.ua.drmp.open.city;

import java.util.List;

public interface CityService {
	List<CityEntryDto> getCitiesByRegionId(int regionId);
}
