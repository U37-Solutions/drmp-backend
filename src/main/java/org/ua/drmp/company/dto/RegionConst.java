package org.ua.drmp.company.dto;

import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RegionConst {
	public static final Map<Integer, String> regions = Map.ofEntries(
		Map.entry(1, "Автономна Республіка Крим"),
		Map.entry(2, "Вінницька"),
		Map.entry(3, "Волинська"),
		Map.entry(4, "Дніпропетровська"),
		Map.entry(5, "Донецька"),
		Map.entry(6, "Житомирська"),
		Map.entry(7, "Закарпатська"),
		Map.entry(8, "Запорізька"),
		Map.entry(9, "Івано-Франківська"),
		Map.entry(10, "Київська"),
		Map.entry(11, "Кіровоградська"),
		Map.entry(12, "Луганська"),
		Map.entry(13, "Львівська"),
		Map.entry(14, "Миколаївська"),
		Map.entry(15, "Одеська"),
		Map.entry(16, "Полтавська"),
		Map.entry(17, "Рівненська"),
		Map.entry(18, "Сумська"),
		Map.entry(19, "Тернопільська"),
		Map.entry(20, "Харківська"),
		Map.entry(21, "Херсонська"),
		Map.entry(22, "Хмельницька"),
		Map.entry(23, "Черкаська"),
		Map.entry(24, "Чернівецька"),
		Map.entry(25, "Чернігівська")
	);

}
