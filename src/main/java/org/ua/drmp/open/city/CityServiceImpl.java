package org.ua.drmp.open.city;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.RegionConst;
import org.ua.drmp.exception.BadRequestException;

@RequiredArgsConstructor
@Service

public class CityServiceImpl implements CityService {
	private final ObjectMapper objectMapper;
	@Override
	public List<CityEntryDto> getCitiesByRegionId(int regionId) {
		String regionName = RegionConst.regions.get(regionId);
		if (regionName == null) {
			throw new BadRequestException("Invalid region ID: " + regionId);
		}

		String fileRegionName = regionName.equals("Автономна Республіка Крим")
			? "Автономна-Республіка-Крим"
			: regionName + "-область";

		String fileName = fileRegionName.replace(" ", "-") + ".json";
		Path path = Path.of("src/main/resources/static/cities", fileName);

		if (!Files.exists(path)) {
			throw new BadRequestException("File not found for region: " + regionName);
		}

		try (InputStream is = Files.newInputStream(path)) {
			return objectMapper.readValue(is, new TypeReference<>() {});
		} catch (Exception e) {
			throw new RuntimeException("Failed to read city file for region " + regionName, e);
		}
	}

}
