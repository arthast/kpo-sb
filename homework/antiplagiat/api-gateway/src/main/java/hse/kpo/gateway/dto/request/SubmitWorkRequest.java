package hse.kpo.gateway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitWorkRequest(
        @NotBlank(message = "Имя студента обязательно")
        String studentName,

        @NotNull(message = "ID задания обязательно")
        Long assignmentId
) {}

