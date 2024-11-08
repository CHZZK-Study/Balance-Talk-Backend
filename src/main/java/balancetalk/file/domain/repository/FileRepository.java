package balancetalk.file.domain.repository;

import balancetalk.file.domain.File;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {

    List<File> findAllByS3Url(String s3URL);

    Optional<File> findByS3Url(String s3Url);
}
