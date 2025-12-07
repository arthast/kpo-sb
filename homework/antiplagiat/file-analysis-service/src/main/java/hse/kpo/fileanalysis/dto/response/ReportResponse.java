package hse.kpo.fileanalysis.dto.response;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id,
        Long submissionId,
        Long assignmentId,
        String studentName,
        String status,
        Boolean isPlagiarism,
        Double similarityPercentage,
        Long similarToSubmissionId,
        String similarToStudentName,
        String details,
        LocalDateTime analyzedAt
) {}

