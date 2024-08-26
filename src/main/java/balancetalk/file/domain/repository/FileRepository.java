package balancetalk.file.domain.repository;

import balancetalk.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {
    Optional<File> findByStoredName(String storedName);
}
