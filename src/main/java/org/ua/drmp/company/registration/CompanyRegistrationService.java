package org.ua.drmp.company.registration;

public interface CompanyRegistrationService {
	void registerCompany(CompanyRegisterRequest request);

	void approveCompany(Long companyId);

	void rejectCompany(Long companyId, RejectCompanyRequest request);
}
