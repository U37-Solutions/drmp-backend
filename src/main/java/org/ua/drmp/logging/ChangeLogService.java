package org.ua.drmp.logging;

import java.util.List;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.entity.User;

public interface ChangeLogService {
	void logUserChange(String email, String action, User prevValue, User newValue);
	List<ChangeLogEntry> readUserChangeLogs();

	void logCompanyChange(Long companyId, String email, String action, Company prevValue, Company newValue);
	List<ChangeLogEntry> readCompanyChangeLogs(Long companyId);

	void logOfficeChange(Long officeId, String email, String action, Office prevValue, Office newValue);

	List<ChangeLogEntry> readOfficeChangeLogs(Long officeId);
}
