package org.glazweq.eduportal.appUser.licenseRequest;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.user.AppUserService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LicenseRequestService {
    private LicenseRequestRepository licenseRequestRepository;
    private AppUserService appUserService;


    public LicenseRequest findLicenseRequestById(Long requestId) {
    return licenseRequestRepository.findById(requestId).orElse(null);

    }
}
