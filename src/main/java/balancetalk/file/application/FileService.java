package balancetalk.file.application;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileProcessor;
import balancetalk.file.domain.FileRepository;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.s3.S3ImageRemover;
import balancetalk.file.domain.s3.S3ImageUploader;
import balancetalk.file.domain.s3.S3ImageUrlGetter;
import balancetalk.global.exception.BalanceTalkException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import static balancetalk.global.exception.ErrorCode.NOT_ATTACH_IMAGE;
import static balancetalk.global.exception.ErrorCode.NOT_UPLOADED_IMAGE_FOR_DB_ERROR;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    private final FileProcessor fileProcessor;
    private final S3ImageUploader s3ImageUploader;
    private final S3ImageRemover s3ImageRemover;
    private final S3ImageUrlGetter s3ImageUrlGetter;
    private final FileRepository fileRepository;

    @Value(value = "${aws.s3.end-point}")
    private String s3EndPoint;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Transactional
    public String uploadImage(MultipartFile multipartFile, Long resourceId, FileType fileType) {
        if (multipartFile.isEmpty()) {
            throw new BalanceTalkException(NOT_ATTACH_IMAGE);
        }

        File file = fileProcessor.process(multipartFile, s3EndPoint + fileType.getUploadDir(), resourceId, fileType);

        String s3Key = fileType.getUploadDir() + file.getStoredName();

        // S3에 이미지 파일 업로드
        s3ImageUploader.uploadImageToBucket(s3Client, bucket, s3Key, multipartFile);

        // DB에 메타 데이터 저장
        try {
            fileRepository.save(file);
        } catch (Exception e) {
            // DB에 메타 데이터 저장 중 예외가 발생하면 S3에 업로드된 이미지 제거
            s3ImageRemover.removeImageFromBucket(s3Client, bucket, s3Key);
            throw new BalanceTalkException(NOT_UPLOADED_IMAGE_FOR_DB_ERROR);
        }

        // 이미지 URL 반환
        return s3ImageUrlGetter.getImageUrl(s3Client, bucket, s3Key);
    }
}
