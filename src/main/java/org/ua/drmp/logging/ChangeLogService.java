package org.ua.drmp.logging;

import java.util.List;
import org.ua.drmp.company.dto.OfficeDto;

public interface ChangeLogService {
	void logUserChange(String email, String action, String prevValue, String newValue);
	List<ChangeLogEntry> readUserChangeLogs();

	void logCompanyChange(Long companyId, String email, String action, String prevValue, String newValue);
	List<ChangeLogEntry> readCompanyChangeLogs(Long companyId);

	void logOfficeChange(Long officeId, String email, String action, OfficeDto prevValue, OfficeDto newValue);

	List<ChangeLogEntry> readOfficeChangeLogs(Long officeId);
}
