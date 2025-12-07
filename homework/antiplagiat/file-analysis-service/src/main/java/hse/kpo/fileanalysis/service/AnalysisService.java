package hse.kpo.fileanalysis.service;

import hse.kpo.fileanalysis.domain.Report;
import hse.kpo.fileanalysis.dto.request.AnalysisRequest;
import hse.kpo.fileanalysis.dto.response.ReportResponse;
import hse.kpo.fileanalysis.dto.response.WordCloudResponse;
import hse.kpo.fileanalysis.exception.ReportNotFoundException;
import hse.kpo.fileanalysis.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final ReportRepository reportRepository;
    private final TextComparisonService textComparisonService;

    // Храним содержимое работ для сравнения (в реальной системе использовался бы кэш или БД)
    private final Map<Long, String> submissionContents = new HashMap<>();

    /**
     * Анализирует работу на плагиат путем сравнения с ранее сданными работами.
     */
    public ReportResponse analyzeSubmission(AnalysisRequest request) {
        log.info("Начинаем анализ работы: submissionId={}, student={}",
                request.submissionId(), request.studentName());

        // Сохраняем содержимое для последующих сравнений
        submissionContents.put(request.submissionId(), request.content());

        // Ищем все предыдущие отчеты по этому заданию
        List<Report> previousReports = reportRepository.findByAssignmentId(request.assignmentId());

        Report report = new Report();
        report.setSubmissionId(request.submissionId());
        report.setAssignmentId(request.assignmentId());
        report.setStudentName(request.studentName());
        report.setStatus("COMPLETED");
        report.setIsPlagiarism(false);
        report.setSimilarityPercentage(0.0);

        double maxSimilarity = 0.0;
        Long mostSimilarSubmissionId = null;
        String mostSimilarStudentName = null;

        // Сравниваем с каждой предыдущей работой
        for (Report prevReport : previousReports) {
            String prevContent = submissionContents.get(prevReport.getSubmissionId());
            if (prevContent == null) {
                continue;
            }

            // Не сравниваем с собой
            if (prevReport.getSubmissionId().equals(request.submissionId())) {
                continue;
            }

            double similarity = textComparisonService.calculateSimilarity(request.content(), prevContent);

            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarSubmissionId = prevReport.getSubmissionId();
                mostSimilarStudentName = prevReport.getStudentName();
            }
        }

        // Проверяем на плагиат
        if (textComparisonService.isPlagiarism(maxSimilarity)) {
            report.setIsPlagiarism(true);
            report.setSimilarToSubmissionId(mostSimilarSubmissionId);
            report.setSimilarToStudentName(mostSimilarStudentName);

            String prevContent = submissionContents.get(mostSimilarSubmissionId);
            report.setDetails(textComparisonService.getComparisonDetails(
                    request.content(), prevContent, maxSimilarity));

            log.warn("Обнаружен плагиат! Работа {} похожа на работу {} ({}%)",
                    request.submissionId(), mostSimilarSubmissionId, maxSimilarity);
        } else {
            report.setDetails("Плагиат не обнаружен. Порог схожести: " +
                    textComparisonService.getPlagiarismThreshold() + "%");
        }

        report.setSimilarityPercentage(maxSimilarity);
        Report saved = reportRepository.save(report);

        return toResponse(saved);
    }

    /**
     * Возвращает отчет по ID.
     */
    public ReportResponse getReport(Long id) {
        return reportRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ReportNotFoundException("Отчет с ID " + id + " не найден"));
    }

    /**
     * Возвращает отчет по ID сдачи.
     */
    public ReportResponse getReportBySubmission(Long submissionId) {
        return reportRepository.findBySubmissionId(submissionId)
                .map(this::toResponse)
                .orElseThrow(() -> new ReportNotFoundException(
                        "Отчет для работы с ID " + submissionId + " не найден"));
    }

    /**
     * Возвращает все отчеты по заданию.
     */
    public List<ReportResponse> getReportsByAssignment(Long assignmentId) {
        return reportRepository.findByAssignmentId(assignmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Возвращает все отчеты.
     */
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Генерирует URL для облака слов через QuickChart API.
     */
    public WordCloudResponse generateWordCloud(Long submissionId) {
        String content = submissionContents.get(submissionId);
        if (content == null) {
            throw new ReportNotFoundException("Содержимое работы с ID " + submissionId + " не найдено");
        }

        // Формируем текст для облака слов
        String text = content.replaceAll("[^a-zA-Zа-яА-ЯёЁ\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String imageUrl = "https://quickchart.io/wordcloud?text=" + encodedText;

        return new WordCloudResponse(submissionId, imageUrl);
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getSubmissionId(),
                report.getAssignmentId(),
                report.getStudentName(),
                report.getStatus(),
                report.getIsPlagiarism(),
                report.getSimilarityPercentage(),
                report.getSimilarToSubmissionId(),
                report.getSimilarToStudentName(),
                report.getDetails(),
                report.getAnalyzedAt()
        );
    }
}

