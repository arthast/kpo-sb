package hse.kpo.gateway.dto.response;

public record FileContentResponse(
        Long submissionId,
        String studentName,
        Long assignmentId,
        String fileName,
        String content
) {}

