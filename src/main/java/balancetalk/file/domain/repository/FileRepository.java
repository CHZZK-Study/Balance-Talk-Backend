package balancetalk.file.domain.repository;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {

    void deleteByResourceIdAndFileType(Long tempTalkPickId, FileType fileType);

    void deleteByResourceIdInAndFileType(List<Long> ids, FileType fileType);

    @Query("SELECT f.imgUrl from File f where f.id = :id")
    String findImgUrlById(Long id);
}
