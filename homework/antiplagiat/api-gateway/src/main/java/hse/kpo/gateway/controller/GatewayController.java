package hse.kpo.gateway.controller;

import hse.kpo.gateway.dto.response.ReportResponse;
import hse.kpo.gateway.dto.response.SubmissionResponse;
import hse.kpo.gateway.dto.response.SubmitWorkResponse;
import hse.kpo.gateway.dto.response.WordCloudResponse;
import hse.kpo.gateway.service.GatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Antiplagiat API", description = "Главный API системы антиплагиата")
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping(value = "/works/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Отправить работу на проверку",
            description = "Загружает файл работы и автоматически запускает анализ на плагиат")
    public ResponseEntity<SubmitWorkResponse> submitWork(
            @RequestParam("file") MultipartFile file,
            @RequestParam("studentName") String studentName,
            @RequestParam("assignmentId") Long assignmentId) {

        SubmitWorkResponse response = gatewayService.submitWork(file, studentName, assignmentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/works/{assignmentId}/reports")
    @Operation(summary = "Получить отчеты по заданию",
            description = "Возвращает все отчеты с флагом плагиата для данного задания")
    public ResponseEntity<List<ReportResponse>> getWorkReports(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(gatewayService.getWorkReports(assignmentId));
    }

    @GetMapping("/submissions")
    @Operation(summary = "Получить все сдачи")
    public ResponseEntity<List<SubmissionResponse>> getAllSubmissions() {
        return ResponseEntity.ok(gatewayService.getAllSubmissions());
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @Operation(summary = "Получить сдачи по заданию")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(gatewayService.getSubmissionsByAssignment(assignmentId));
    }

    @GetMapping("/submissions/{submissionId}/report")
    @Operation(summary = "Получить отчет для конкретной сдачи")
    public ResponseEntity<ReportResponse> getReportBySubmission(@PathVariable Long submissionId) {
        return ResponseEntity.ok(gatewayService.getReportBySubmission(submissionId));
    }

    @GetMapping("/reports")
    @Operation(summary = "Получить все отчеты")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(gatewayService.getAllReports());
    }

    @GetMapping("/submissions/{submissionId}/wordcloud")
    @Operation(summary = "Получить URL облака слов для работы")
    public ResponseEntity<WordCloudResponse> getWordCloud(@PathVariable Long submissionId) {
        return ResponseEntity.ok(gatewayService.getWordCloud(submissionId));
    }
}

