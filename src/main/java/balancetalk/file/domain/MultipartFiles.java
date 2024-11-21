package balancetalk.file.domain;

import static balancetalk.global.exception.ErrorCode.EXCEEDED_IMAGES_SIZE;
import static balancetalk.global.exception.ErrorCode.NOT_ATTACH_IMAGE;
import static balancetalk.global.exception.ErrorCode.NOT_SUPPORTED_MIME_TYPE;

import balancetalk.global.exception.BalanceTalkException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public record MultipartFiles(List<MultipartFile> multipartFiles, FileType fileType) {

    public MultipartFiles {
        if (multipartFiles == null || containsEmptyFile(multipartFiles)) {
            throw new BalanceTalkException(NOT_ATTACH_IMAGE);
        }
        if (multipartFiles.size() > fileType.getMaxCount()) {
            throw new BalanceTalkException(EXCEEDED_IMAGES_SIZE);
        }
        if (containsNotImage(multipartFiles)) {
            throw new BalanceTalkException(NOT_SUPPORTED_MIME_TYPE);
        }
    }

    private boolean containsEmptyFile(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream().anyMatch(MultipartFile::isEmpty);
    }

    private boolean containsNotImage(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
                .anyMatch(multipartFile ->
                        !Objects.requireNonNull(multipartFile.getContentType()).startsWith("image"));
    }

    @Override
    public List<MultipartFile> multipartFiles() {
        return List.copyOf(multipartFiles);
    }
}
