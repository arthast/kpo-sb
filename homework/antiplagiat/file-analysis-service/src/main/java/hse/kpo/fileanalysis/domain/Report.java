package hse.kpo.fileanalysis.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность для хранения отчета о проверке на плагиат.
 */
@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long submissionId;

    @Column(nullable = false)
    private Long assignmentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Boolean isPlagiarism;

    private Double similarityPercentage;

    private Long similarToSubmissionId;

    private String similarToStudentName;

    @Column(length = 2000)
    private String details;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }
}

