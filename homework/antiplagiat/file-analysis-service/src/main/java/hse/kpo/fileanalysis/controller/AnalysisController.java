package hse.kpo.fileanalysis.controller;

import hse.kpo.fileanalysis.dto.request.AnalysisRequest;
import hse.kpo.fileanalysis.dto.response.ReportResponse;
import hse.kpo.fileanalysis.dto.response.WordCloudResponse;
import hse.kpo.fileanalysis.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "File Analysis", description = "API для анализа работ на плагиат")
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/analyze")
    @Operation(summary = "Запустить анализ работы на плагиат")
    public ResponseEntity<ReportResponse> analyzeSubmission(
            @Valid @RequestBody AnalysisRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(analysisService.analyzeSubmission(request));
    }

    @GetMapping("/reports/{id}")
    @Operation(summary = "Получить отчет по ID")
    public ResponseEntity<ReportResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getReport(id));
    }

    @GetMapping("/reports")
    @Operation(summary = "Получить все отчеты")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(analysisService.getAllReports());
    }

    @GetMapping("/submissions/{submissionId}/report")
    @Operation(summary = "Получить отчет по ID сдачи")
    public ResponseEntity<ReportResponse> getReportBySubmission(@PathVariable Long submissionId) {
        return ResponseEntity.ok(analysisService.getReportBySubmission(submissionId));
    }

    @GetMapping("/assignments/{assignmentId}/reports")
    @Operation(summary = "Получить все отчеты по заданию")
    public ResponseEntity<List<ReportResponse>> getReportsByAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(analysisService.getReportsByAssignment(assignmentId));
    }

    @GetMapping("/submissions/{submissionId}/wordcloud")
    @Operation(summary = "Получить URL облака слов для работы")
    public ResponseEntity<WordCloudResponse> getWordCloud(@PathVariable Long submissionId) {
        return ResponseEntity.ok(analysisService.generateWordCloud(submissionId));
    }
}

