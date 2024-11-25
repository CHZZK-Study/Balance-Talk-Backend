package balancetalk.file.domain.repository;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileType;
import java.util.List;

public interface FileRepositoryCustom {

    List<String> findImgUrlsByResourceIdAndFileType(Long resourceId, FileType fileType);

    List<Long> findIdsByResourceIdAndFileType(Long resourceId, FileType fileType);

    List<File> findAllByResourceIdAndFileType(Long resourceId, FileType fileType);

    List<File> findAllByResourceIdsAndFileType(List<Long> resourceIds, FileType fileType);

    Long findIdByResourceIdAndFileType(Long resourceId, FileType fileType);
}
