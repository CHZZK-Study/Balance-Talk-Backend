package balancetalk.file.domain;

import static balancetalk.global.exception.ErrorCode.EXCEEDED_IMAGES_SIZE;
import static balancetalk.global.exception.ErrorCode.MISSING_MIME_TYPE;
import static balancetalk.global.exception.ErrorCode.NOT_ATTACH_IMAGE;
import static balancetalk.global.exception.ErrorCode.NOT_SUPPORTED_MIME_TYPE;

import balancetalk.global.exception.BalanceTalkException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

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
                .anyMatch(this::isNotImage);
    }

    private boolean isNotImage(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        if (contentType == null) {
            throw new BalanceTalkException(MISSING_MIME_TYPE);
        }
        return !contentType.startsWith("image");
    }

    @Override
    public List<MultipartFile> multipartFiles() {
        return List.copyOf(multipartFiles);
    }
}
