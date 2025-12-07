package hse.kpo.filestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionRequest(
        @NotBlank(message = "Имя студента обязательно")
        String studentName,

        @NotNull(message = "ID задания обязательно")
        Long assignmentId
) {}

