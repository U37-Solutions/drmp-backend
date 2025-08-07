package org.ua.drmp.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.CompanyDto;
import org.ua.drmp.company.dto.OfficeDto;
import org.ua.drmp.exception.ResourceNotFoundException;

@Service
public class ChangeLogServiceImpl implements ChangeLogService {

	private static final String BASE_LOG_PATH = "/app/logs";
	private static final String USER_LOG_FILE = BASE_LOG_PATH + "/users/users.log";
	private static final String COMPANY_LOG_DIR = BASE_LOG_PATH + "/companies";
	private static final String OFFICE_LOG_DIR = BASE_LOG_PATH + "/offices";
	@Override
	public void logUserChange(String email, String action, String prevValue, String newValue) {
		try {
			Files.createDirectories(Paths.get("logs/users"));
			LocalDateTime timestamp = LocalDateTime.now();

			String logLine = String.format(
				"%s | %s | action: %s | prev: %s | new: %s",
				timestamp,
				email,
				action,
				prevValue == null ? "null" : prevValue,
				newValue == null ? "null" : newValue
			);

			Files.write(
				Paths.get(USER_LOG_FILE),
				(logLine + System.lineSeparator()).getBytes(),
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write user changelog", e);
		}
	}

	@Override
	public List<ChangeLogEntry> readUserChangeLogs() {
		Path userLogFile = Paths.get(USER_LOG_FILE);
		if (!Files.exists(userLogFile)) {
			throw new ResourceNotFoundException("Not found file");
		}

		try (Stream<String> lines = Files.lines(userLogFile)) {
			return lines.map(this::parseLogLine).filter(Objects::nonNull).toList();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read user changelog", e);
		}
	}

	@Override
	public void logCompanyChange(Long companyId, String email, String action, CompanyDto prevValue, CompanyDto newValue) {
		try {
			Files.createDirectories(Paths.get(COMPANY_LOG_DIR));
			LocalDateTime timestamp = LocalDateTime.now();

			String logLine = String.format(
				"%s | %s | action: %s | prev: %s | new: %s",
				timestamp,
				email,
				action,
				new ObjectMapper().writeValueAsString(prevValue),
				new ObjectMapper().writeValueAsString(newValue)
			);

			Path path = Paths.get(COMPANY_LOG_DIR, "company-" + companyId + ".log");
			Files.write(path, (logLine + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write company changelog", e);
		}
	}

	@Override
	public List<ChangeLogEntry> readCompanyChangeLogs(Long companyId) {
		Path logFile = Paths.get(COMPANY_LOG_DIR, "company-" + companyId + ".log");
		if (!Files.exists(logFile)) {
			throw new ResourceNotFoundException("Company log file not found");
		}
		try (Stream<String> lines = Files.lines(logFile)) {
			return lines.map(this::parseLogLine).filter(Objects::nonNull).toList();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read company changelog", e);
		}
	}

	@Override
	public void logOfficeChange(Long officeId, String email, String action, OfficeDto prevValue, OfficeDto newValue) {
		try {
			Path directory = Paths.get(OFFICE_LOG_DIR);
			Files.createDirectories(directory);

			LocalDateTime timestamp = LocalDateTime.now();

			String logLine = String.format(
				"%s | %s | action: %s | prev: %s | new: %s",
				timestamp,
				email,
				action,
				new ObjectMapper().writeValueAsString(prevValue),
				new ObjectMapper().writeValueAsString(newValue)
			);

			Path officeLogFile = directory.resolve("office-" + officeId + ".log");
			Files.write(
				officeLogFile,
				(logLine + System.lineSeparator()).getBytes(),
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write office changelog", e);
		}
	}

	@Override
	public List<ChangeLogEntry> readOfficeChangeLogs(Long officeId) {
		Path officeLogFile = Paths.get(OFFICE_LOG_DIR, "office-" + officeId + ".log");

		if (!Files.exists(officeLogFile)) {
			throw new ResourceNotFoundException("Log file not found for office with id " + officeId);
		}

		try (Stream<String> lines = Files.lines(officeLogFile)) {
			return lines.map(this::parseLogLine).filter(Objects::nonNull).toList();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read office changelog", e);
		}
	}

	private ChangeLogEntry parseLogLine(String line) {
		try {
			// Expected format:
			// 2025-07-21 12:34:56 | john@example.com | action: update | prev: ... | new: ...
			String[] parts = line.split(" \\| ");
			if (parts.length < 5) return null;

			LocalDateTime timestamp = LocalDateTime.parse(parts[0].trim());
			String email = parts[1].trim();
			String action = parts[2].replace("action: ", "").trim();
			String prevValue = parts[3].replace("prev: ", "").trim();
			String newValue = parts[4].replace("new: ", "").trim();

			return new ChangeLogEntry(timestamp, email, action, prevValue.equals("null") ? null : prevValue, newValue);
		} catch (Exception e) {
			return null;
		}
	}
}
