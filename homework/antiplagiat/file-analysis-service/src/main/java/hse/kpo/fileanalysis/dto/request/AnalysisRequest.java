package hse.kpo.fileanalysis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnalysisRequest(
        @NotNull(message = "ID сдачи обязательно")
        Long submissionId,

        @NotNull(message = "ID задания обязательно")
        Long assignmentId,

        @NotBlank(message = "Имя студента обязательно")
        String studentName,

        @NotBlank(message = "Содержимое файла обязательно")
        String content
) {}

