package com.dvlpr.CampusJobBoardSystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "JOB_APPLICATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"job_id", "student_id"})
})
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED; // Default

    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    // Relationship: Many Applications -> One Job
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // Relationship: Many Applications -> One Student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
    }

    // Generate Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
}