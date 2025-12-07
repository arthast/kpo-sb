package hse.kpo.gateway.client;

import hse.kpo.gateway.dto.response.FileContentResponse;
import hse.kpo.gateway.dto.response.SubmissionResponse;
import hse.kpo.gateway.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageClient {

    private final RestTemplate restTemplate;

    @Value("${services.file-storage.url}")
    private String fileStorageUrl;

    /**
     * Загружает файл в File Storage Service.
     */
    public SubmissionResponse uploadFile(MultipartFile file, String studentName, Long assignmentId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            body.add("studentName", studentName);
            body.add("assignmentId", assignmentId);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<SubmissionResponse> response = restTemplate.postForEntity(
                    fileStorageUrl + "/api/files/upload",
                    requestEntity,
                    SubmissionResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            log.error("File Storage Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Storage Service недоступен");
        } catch (IOException e) {
            log.error("Ошибка чтения файла: {}", e.getMessage());
            throw new RuntimeException("Ошибка чтения файла");
        }
    }

    /**
     * Получает содержимое файла.
     */
    public FileContentResponse getFileContent(Long submissionId) {
        try {
            return restTemplate.getForObject(
                    fileStorageUrl + "/api/files/submissions/{id}/content",
                    FileContentResponse.class,
                    submissionId
            );
        } catch (RestClientException e) {
            log.error("File Storage Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Storage Service недоступен");
        }
    }

    /**
     * Получает информацию о сдаче.
     */
    public SubmissionResponse getSubmission(Long submissionId) {
        try {
            return restTemplate.getForObject(
                    fileStorageUrl + "/api/files/submissions/{id}",
                    SubmissionResponse.class,
                    submissionId
            );
        } catch (RestClientException e) {
            log.error("File Storage Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Storage Service недоступен");
        }
    }

    /**
     * Получает все сдачи.
     */
    public List<SubmissionResponse> getAllSubmissions() {
        try {
            SubmissionResponse[] response = restTemplate.getForObject(
                    fileStorageUrl + "/api/files/submissions",
                    SubmissionResponse[].class
            );
            return response != null ? Arrays.asList(response) : List.of();
        } catch (RestClientException e) {
            log.error("File Storage Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Storage Service недоступен");
        }
    }

    /**
     * Получает сдачи по заданию.
     */
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        try {
            SubmissionResponse[] response = restTemplate.getForObject(
                    fileStorageUrl + "/api/files/assignments/{assignmentId}/submissions",
                    SubmissionResponse[].class,
                    assignmentId
            );
            return response != null ? Arrays.asList(response) : List.of();
        } catch (RestClientException e) {
            log.error("File Storage Service недоступен: {}", e.getMessage());
            throw new ServiceUnavailableException("File Storage Service недоступен");
        }
    }
}

