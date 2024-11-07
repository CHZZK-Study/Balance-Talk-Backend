package balancetalk.file.domain.repository;

import balancetalk.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {
    boolean existsByS3Url(String s3URL);
}
