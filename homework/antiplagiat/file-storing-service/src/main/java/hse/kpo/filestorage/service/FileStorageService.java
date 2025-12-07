package hse.kpo.filestorage.service;

import hse.kpo.filestorage.domain.Submission;
import hse.kpo.filestorage.dto.request.SubmissionRequest;
import hse.kpo.filestorage.dto.response.FileContentResponse;
import hse.kpo.filestorage.dto.response.SubmissionResponse;
import hse.kpo.filestorage.exception.FileStorageException;
import hse.kpo.filestorage.exception.SubmissionNotFoundException;
import hse.kpo.filestorage.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final SubmissionRepository submissionRepository;

    @Value("${file.storage.path:/app/uploads}")
    private String storagePath;

    /**
     * Сохраняет загруженный файл и создает запись о сдаче работы.
     */
    public SubmissionResponse uploadFile(MultipartFile file, SubmissionRequest request) {
        try {
            Path uploadDir = Paths.get(storagePath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            Submission submission = new Submission();
            submission.setStudentName(request.studentName());
            submission.setAssignmentId(request.assignmentId());
            submission.setFileName(file.getOriginalFilename());
            submission.setFilePath(filePath.toString());

            Submission saved = submissionRepository.save(submission);
            log.info("Файл сохранен: {} для студента {} по заданию {}",
                    saved.getFileName(), saved.getStudentName(), saved.getAssignmentId());

            return toResponse(saved);
        } catch (IOException e) {
            log.error("Ошибка сохранения файла: {}", e.getMessage());
            throw new FileStorageException("Не удалось сохранить файл: " + e.getMessage());
        }
    }

    /**
     * Возвращает информацию о сдаче по ID.
     */
    public SubmissionResponse getSubmission(Long id) {
        return submissionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new SubmissionNotFoundException("Работа с ID " + id + " не найдена"));
    }

    /**
     * Возвращает все сдачи по заданию.
     */
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdOrderBySubmittedAtAsc(assignmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Возвращает содержимое файла.
     */
    public FileContentResponse getFileContent(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new SubmissionNotFoundException("Работа с ID " + submissionId + " не найдена"));

        try {
            Path filePath = Paths.get(submission.getFilePath());
            String content = Files.readString(filePath, StandardCharsets.UTF_8);

            return new FileContentResponse(
                    submission.getId(),
                    submission.getStudentName(),
                    submission.getAssignmentId(),
                    submission.getFileName(),
                    content
            );
        } catch (IOException e) {
            log.error("Ошибка чтения файла: {}", e.getMessage());
            throw new FileStorageException("Не удалось прочитать файл: " + e.getMessage());
        }
    }

    /**
     * Возвращает все сдачи.
     */
    public List<SubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SubmissionResponse toResponse(Submission submission) {
        return new SubmissionResponse(
                submission.getId(),
                submission.getStudentName(),
                submission.getAssignmentId(),
                submission.getFileName(),
                submission.getSubmittedAt()
        );
    }
}

