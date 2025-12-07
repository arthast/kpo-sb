package hse.kpo.filestorage.controller;

import hse.kpo.filestorage.dto.request.SubmissionRequest;
import hse.kpo.filestorage.dto.response.FileContentResponse;
import hse.kpo.filestorage.dto.response.SubmissionResponse;
import hse.kpo.filestorage.service.FileStorageService;
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
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File Storage", description = "API для хранения и получения файлов")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить файл работы")
    public ResponseEntity<SubmissionResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("studentName") String studentName,
            @RequestParam("assignmentId") Long assignmentId) {

        SubmissionRequest request = new SubmissionRequest(studentName, assignmentId);
        SubmissionResponse response = fileStorageService.uploadFile(file, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/submissions/{id}")
    @Operation(summary = "Получить информацию о сдаче по ID")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable Long id) {
        return ResponseEntity.ok(fileStorageService.getSubmission(id));
    }

    @GetMapping("/submissions")
    @Operation(summary = "Получить все сдачи")
    public ResponseEntity<List<SubmissionResponse>> getAllSubmissions() {
        return ResponseEntity.ok(fileStorageService.getAllSubmissions());
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @Operation(summary = "Получить все сдачи по заданию")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(fileStorageService.getSubmissionsByAssignment(assignmentId));
    }

    @GetMapping("/submissions/{id}/content")
    @Operation(summary = "Получить содержимое файла")
    public ResponseEntity<FileContentResponse> getFileContent(@PathVariable Long id) {
        return ResponseEntity.ok(fileStorageService.getFileContent(id));
    }
}

