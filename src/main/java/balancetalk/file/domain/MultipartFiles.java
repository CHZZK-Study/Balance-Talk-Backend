package balancetalk.file.domain;

import balancetalk.global.exception.BalanceTalkException;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static balancetalk.global.exception.ErrorCode.EXCEEDED_IMAGES_SIZE;
import static balancetalk.global.exception.ErrorCode.NOT_ATTACH_IMAGE;

@Getter
public record MultipartFiles(List<MultipartFile> multipartFiles, FileType fileType) {

    public MultipartFiles {
        if (multipartFiles == null || containsEmptyFile(multipartFiles)) {
            throw new BalanceTalkException(NOT_ATTACH_IMAGE);
        }
        if (multipartFiles.size() > fileType.getMaxCount()) {
            throw new BalanceTalkException(EXCEEDED_IMAGES_SIZE);
        }
    }

    private boolean containsEmptyFile(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream().anyMatch(MultipartFile::isEmpty);
    }

    @Override
    public List<MultipartFile> multipartFiles() {
        return List.copyOf(multipartFiles);
    }
}
