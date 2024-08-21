package balancetalk.file.domain.repository;

import balancetalk.file.domain.FileType;

import java.util.List;

public interface FileRepositoryCustom {
    void updateResourceIdByStoredNames(long resourceId, List<String> storedNames);

    List<String> findImgUrlsByResourceIdAndFileType(Long talkPickId, FileType fileType);
}
