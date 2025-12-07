package hse.kpo.gateway.dto.response;

import java.time.LocalDateTime;

public record SubmissionResponse(
        Long id,
        String studentName,
        Long assignmentId,
        String fileName,
        LocalDateTime submittedAt
) {}

