package org.glazweq.eduportal.appUser.licenseRequest;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRequestRepository extends JpaRepository<LicenseRequest, Long> {
}