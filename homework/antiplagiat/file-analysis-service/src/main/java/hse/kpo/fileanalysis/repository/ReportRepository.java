package hse.kpo.fileanalysis.repository;

import hse.kpo.fileanalysis.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByAssignmentId(Long assignmentId);

    Optional<Report> findBySubmissionId(Long submissionId);

    List<Report> findByIsPlagiarismTrue();
}

