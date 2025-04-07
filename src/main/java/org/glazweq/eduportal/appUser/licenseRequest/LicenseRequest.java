package org.glazweq.eduportal.appUser.licenseRequest;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.glazweq.eduportal.appUser.user.AppUser;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "license_requests")
@Getter
public class LicenseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user; // The user who submitted the request

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // When the request was submitted

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LicenseRequestStatus status; // Status of the request (PENDING, APPROVED, REJECTED)

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private AppUser approvedBy; // User who approved the request (ADMIN)

    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // When the request was approved
}