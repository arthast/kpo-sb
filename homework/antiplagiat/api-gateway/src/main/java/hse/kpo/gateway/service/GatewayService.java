package hse.kpo.gateway.service;

import hse.kpo.gateway.client.FileAnalysisClient;
import hse.kpo.gateway.client.FileStorageClient;
import hse.kpo.gateway.dto.response.FileContentResponse;
import hse.kpo.gateway.dto.response.ReportResponse;
import hse.kpo.gateway.dto.response.SubmissionResponse;
import hse.kpo.gateway.dto.response.SubmitWorkResponse;
import hse.kpo.gateway.dto.response.WordCloudResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayService {

    private final FileStorageClient fileStorageClient;
    private final FileAnalysisClient fileAnalysisClient;

    /**
     * Отправляет работу на проверку: загружает файл и запускает анализ.
     */
    public SubmitWorkResponse submitWork(MultipartFile file, String studentName, Long assignmentId) {
        log.info("Отправка работы: student={}, assignment={}", studentName, assignmentId);

        // 1. Загружаем файл в File Storage Service
        SubmissionResponse submission = fileStorageClient.uploadFile(file, studentName, assignmentId);
        log.info("Файл загружен: submissionId={}", submission.id());

        // 2. Получаем содержимое файла
        FileContentResponse fileContent = fileStorageClient.getFileContent(submission.id());

        // 3. Запускаем анализ на плагиат
        ReportResponse report = fileAnalysisClient.analyzeSubmission(
                submission.id(),
                submission.assignmentId(),
                submission.studentName(),
                fileContent.content()
        );
        log.info("Анализ завершен: reportId={}, isPlagiarism={}", report.id(), report.isPlagiarism());

        return new SubmitWorkResponse(
                submission.id(),
                submission.studentName(),
                submission.assignmentId(),
                submission.fileName(),
                submission.submittedAt(),
                report
        );
    }

    /**
     * Получает отчеты по заданию.
     */
    public List<ReportResponse> getWorkReports(Long assignmentId) {
        return fileAnalysisClient.getReportsByAssignment(assignmentId);
    }

    /**
     * Получает все сдачи.
     */
    public List<SubmissionResponse> getAllSubmissions() {
        return fileStorageClient.getAllSubmissions();
    }

    /**
     * Получает сдачи по заданию.
     */
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return fileStorageClient.getSubmissionsByAssignment(assignmentId);
    }

    /**
     * Получает отчет по сдаче.
     */
    public ReportResponse getReportBySubmission(Long submissionId) {
        return fileAnalysisClient.getReportBySubmission(submissionId);
    }

    /**
     * Получает все отчеты.
     */
    public List<ReportResponse> getAllReports() {
        return fileAnalysisClient.getAllReports();
    }

    /**
     * Получает облако слов для работы.
     */
    public WordCloudResponse getWordCloud(Long submissionId) {
        return fileAnalysisClient.getWordCloud(submissionId);
    }
}

