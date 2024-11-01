package balancetalk.file.domain;

import io.awspring.cloud.s3.S3Operations;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final S3Client s3Client;
    private final S3Operations s3Operations;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${picko.aws.s3.endpoint}")
    private String s3EndPoint;

    public void relocateFiles(List<File> files, Long resourceId, FileType fileType) {
        for (File file : files) {
            String s3Key = relocateWithinS3(file, resourceId, fileType);
            updateFile(file, s3Key, resourceId, fileType);
        }
    }

    private String relocateWithinS3(File file, Long resourceId, FileType fileType) {
        String sourceKey = file.getS3Key();
        String destinationKey = getDestinationKey(file, resourceId, fileType);
        CopyObjectRequest copyObjectRequest = getCopyObjectRequest(sourceKey, destinationKey);
        s3Client.copyObject(copyObjectRequest);
        if (sourceKey.contains("temp")) {
            s3Operations.deleteObject(bucket, sourceKey);
        }
        return destinationKey;
    }

    private String getDestinationKey(File file, Long resourceId, FileType fileType) {
        return "%s%d/%s".formatted(fileType.getUploadDir(), resourceId, file.getStoredName());
    }

    private CopyObjectRequest getCopyObjectRequest(String sourceKey, String destinationKey) {
        return CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destinationKey)
                .build();
    }

    private void updateFile(File file, String s3Key, Long resourceId, FileType fileType) {
        file.updateS3KeyAndUrl(s3Key, s3EndPoint + s3Key);
        file.updateResourceId(resourceId);
        file.updateFileType(fileType);
    }
}
