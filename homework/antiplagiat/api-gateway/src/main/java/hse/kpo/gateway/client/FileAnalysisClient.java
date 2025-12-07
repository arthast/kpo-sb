package hse.kpo.gateway.client;

import hse.kpo.gateway.dto.response.ReportResponse;
import hse.kpo.gateway.dto.response.WordCloudResponse;
import hse.kpo.gateway.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileAnalysisClient {

    private final RestTemplate restTemplate;

    @Value("${services.file-analysis.url}")
    private String fileAnalysisUrl;

    /**
     * Запускает анализ работы на плагиат.
     */
    public ReportResponse analyzeSubmission(Long submissionId, Long assignmentId,
                                            String studentName, String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> request = Map.of(
                    "submissionId", submissionId,
                    "assignmentId", assignmentId,
                    "studentName", studentName,
                    "content", content
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            return restTemplate.postForObject(
                    fileAnalysisUrl + "/api/analysis/analyze",
                    entity,
                    ReportResponse.class
            );
        } catch (RestClientException e) {
            log.error("File Analysis Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Analysis Service недоступен");
        }
    }

    /**
     * Получает отчет по ID.
     */
    public ReportResponse getReport(Long reportId) {
        try {
            return restTemplate.getForObject(
                    fileAnalysisUrl + "/api/analysis/reports/{id}",
                    ReportResponse.class,
                    reportId
            );
        } catch (RestClientException e) {
            log.error("File Analysis Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Analysis Service недоступен");
        }
    }

    /**
     * Получает все отчеты.
     */
    public List<ReportResponse> getAllReports() {
        try {
            ReportResponse[] response = restTemplate.getForObject(
                    fileAnalysisUrl + "/api/analysis/reports",
                    ReportResponse[].class
            );
            return response != null ? Arrays.asList(response) : List.of();
        } catch (RestClientException e) {
            log.error("File Analysis Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Analysis Service недоступен");
        }
    }

    /**
     * Получает отчет по ID сдачи.
     */
    public ReportResponse getReportBySubmission(Long submissionId) {
        try {
            return restTemplate.getForObject(
                    fileAnalysisUrl + "/api/analysis/submissions/{submissionId}/report",
                    ReportResponse.class,
                    submissionId
            );
        } catch (RestClientException e) {
            log.error("File Analysis Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Analysis Service недоступен");
        }
    }

    /**
     * Получает все отчеты по заданию.
     */
    public List<ReportResponse> getReportsByAssignment(Long assignmentId) {
        try {
            ReportResponse[] response = restTemplate.getForObject(
                    fileAnalysisUrl + "/api/analysis/assignments/{assignmentId}/reports",
                    ReportResponse[].class,
                    assignmentId
            );
            return response != null ? Arrays.asList(response) : List.of();
        } catch (RestClientException e) {
            log.error("File Analysis Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Analysis Service недоступен");
        }
    }

    /**
     * Получает URL облака слов.
     */
    public WordCloudResponse getWordCloud(Long submissionId) {
        try {
            return restTemplate.getForObject(
                    fileAnalysisUrl + "/api/analysis/submissions/{submissionId}/wordcloud",
                    WordCloudResponse.class,
                    submissionId
            );
        } catch (RestClientException e) {
            log.error("File Analysis Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Analysis Service недоступен");
        }
    }
}

