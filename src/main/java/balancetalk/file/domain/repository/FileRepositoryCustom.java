package balancetalk.file.domain.repository;

import balancetalk.file.domain.FileType;

import java.util.List;

public interface FileRepositoryCustom {
    void updateResourceIdAndTypeByStoredNames(Long resourceId, FileType fileType, List<String> storedNames);

    List<String> findImgUrlsByResourceIdAndFileType(Long resourceId, FileType fileType);

    List<String> findStoredNamesByResourceIdAndFileType(Long resourceId, FileType fileType);
}
