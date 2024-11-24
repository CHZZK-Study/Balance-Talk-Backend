package balancetalk.file.domain.repository;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {

    void deleteByResourceIdAndFileType(Long tempTalkPickId, FileType fileType);
}
