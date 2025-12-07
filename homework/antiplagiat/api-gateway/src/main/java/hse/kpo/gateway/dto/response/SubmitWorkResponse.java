package hse.kpo.gateway.dto.response;

import java.time.LocalDateTime;

public record SubmitWorkResponse(
        Long submissionId,
        String studentName,
        Long assignmentId,
        String fileName,
        LocalDateTime submittedAt,
        ReportResponse report
) {}

