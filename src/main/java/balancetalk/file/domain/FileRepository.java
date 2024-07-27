package balancetalk.file.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByStoredName(String storedName);

    List<File> findAllByStoredNameIn(List<String> storedNames);
}
