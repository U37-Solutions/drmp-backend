package org.ua.drmp.open;

import java.util.List;
import java.util.Optional;

public interface PublicMapService {
	List<PublicMapPointDto> getOfficesInBounds(Optional<double[]> boundaries);
}
