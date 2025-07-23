package org.ua.drmp.logging;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/changelog")
@RequiredArgsConstructor
public class ChangeLogController {

	private final ChangeLogService changelogService;

	@GetMapping("/users")
	public List<ChangeLogEntry> getUserChangelog() {
		return changelogService.readUserChangeLogs();
	}

	@GetMapping("companies/{id}")
	public List<ChangeLogEntry> getCompanyChangelog(@PathVariable Long id) {
		return changelogService.readCompanyChangeLogs(id);
	}

	@GetMapping("offices/{id}")
	public List<ChangeLogEntry> getOfficeChangelog(@PathVariable Long id) {
		return changelogService.readOfficeChangeLogs(id);
	}
}
