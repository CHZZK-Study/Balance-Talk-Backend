package balancetalk.file.domain;

import balancetalk.file.domain.repository.FileRepository;
import io.awspring.cloud.s3.S3Operations;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final S3Client s3Client;
    private final S3Operations s3Operations;
    private final FileRepository fileRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${picko.aws.s3.endpoint}")
    private String s3EndPoint;

    @Async
    @Retryable(backoff = @Backoff(delay = 1000))
    public void relocateFile(File file, Long resourceId, FileType fileType) {
        String newDirectoryPath = relocateWithinS3(file, resourceId, fileType);
        saveOrMapToResource(file, newDirectoryPath, resourceId, fileType);
    }

    @Async
    @Retryable(backoff = @Backoff(delay = 1000))
    public void relocateFiles(List<File> files, Long resourceId, FileType fileType) {
        for (File file : files) {
            String newDirectoryPath = relocateWithinS3(file, resourceId, fileType);
            saveOrMapToResource(file, newDirectoryPath, resourceId, fileType);
        }
    }

    private String relocateWithinS3(File file, Long resourceId, FileType fileType) {
        String sourceKey = file.getS3Key();
        String destinationKey = getDestinationKey(file, resourceId, fileType);
        CopyObjectRequest copyObjectRequest = getCopyObjectRequest(sourceKey, destinationKey);
        s3Client.copyObject(copyObjectRequest);
        if (file.isUnmapped()) {
            s3Operations.deleteObject(bucket, sourceKey);
        }
        return String.format("%s%d/", fileType.getUploadDir(), resourceId);
    }

    private String getDestinationKey(File file, Long resourceId, FileType fileType) {
        return String.format("%s%d/%s", fileType.getUploadDir(), resourceId, file.getStoredName());
    }

    private CopyObjectRequest getCopyObjectRequest(String sourceKey, String destinationKey) {
        return CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destinationKey)
                .build();
    }

    private void saveOrMapToResource(File file, String directoryPath, Long resourceId, FileType fileType) {
        if (file.isUnmapped()) {
            file.updateDirectoryPathAndImgUrl(directoryPath, s3EndPoint);
            file.updateResourceId(resourceId);
            file.updateFileType(fileType);
            return;
        }
        fileRepository.save(createNewFile(file, resourceId, fileType, directoryPath));
    }

    private File createNewFile(File file, Long resourceId, FileType fileType, String directoryPath) {
        return File.builder()
                .resourceId(resourceId)
                .fileType(fileType)
                .uploadName(file.getUploadName())
                .storedName(String.format("%s_%s", UUID.randomUUID(), file.getUploadName()))
                .mimeType(file.getMimeType())
                .size(file.getSize())
                .directoryPath(directoryPath)
                .imgUrl(getImgUrl(file, directoryPath))
                .build();
    }

    private String getImgUrl(File file, String directoryPath) {
        return String.format("%s%s%s", s3EndPoint, directoryPath, file.getStoredName());
    }

    @Async
    @Retryable(backoff = @Backoff(delay = 1000))
    public void deleteFiles(List<File> files) {
        for (File file : files) {
            s3Operations.deleteObject(bucket, file.getS3Key());
        }
        fileRepository.deleteAll(files);
    }

    @Async
    @Retryable(backoff = @Backoff(delay = 1000))
    public void deleteFile(File file) {
        s3Operations.deleteObject(bucket, file.getS3Key());
        fileRepository.delete(file);
    }
}
