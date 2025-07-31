package org.ua.drmp.open;

import java.util.List;
import org.ua.drmp.company.entity.Office;

public interface PublicOfficeRepositoryCustom {
	List<Office> findOfficesWithinBounds(double lat1, double lon1, double lat2, double lon2);
}
