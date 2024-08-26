package balancetalk.file.application;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileProcessor;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.MultipartFiles;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.file.domain.s3.S3ImageRemover;
import balancetalk.file.domain.s3.S3ImageUploader;
import balancetalk.file.domain.s3.S3ImageUrlGetter;
import balancetalk.file.dto.UploadFileResponse;
import balancetalk.global.exception.BalanceTalkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;
import java.util.List;

import static balancetalk.global.exception.ErrorCode.NOT_FOUND_FILE;
import static balancetalk.global.exception.ErrorCode.NOT_UPLOADED_IMAGE_FOR_DB_ERROR;

@Slf4j
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
    public UploadFileResponse uploadImages(MultipartFiles multipartFiles) { // TODO 데이터 일관성 관련 개선 필요
        FileType fileType = multipartFiles.fileType();

        List<String> s3Keys = new ArrayList<>();
        List<String> storedNames = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles.multipartFiles()) {
            try {
                File file = fileProcessor.process(multipartFile, s3EndPoint + fileType.getUploadDir(), fileType);
                String s3Key = fileType.getUploadDir() + file.getStoredName();

                // S3에 이미지 파일 업로드
                s3ImageUploader.uploadImageToBucket(s3Client, bucket, s3Key, multipartFile);

                // DB에 메타 데이터 저장
                fileRepository.save(file);

                // 이미지 URL 및 고유 이름 저장
                s3Keys.add(s3Key);
                storedNames.add(file.getStoredName());
            } catch (Exception e) {
                // DB에 메타 데이터 저장 중 예외가 발생하면 S3에 업로드된 이미지 제거 후 커스텀 예외로 변환
                for (String key : s3Keys) {
                    s3ImageRemover.removeImageFromBucket(s3Client, bucket, key);
                }

                throw new BalanceTalkException(NOT_UPLOADED_IMAGE_FOR_DB_ERROR);
            }
        }

        List<String> imgUrls = s3Keys.stream()
                .map(key -> s3ImageUrlGetter.getImageUrl(s3Client, bucket, key))
                .toList();

        return new UploadFileResponse(imgUrls, storedNames);
    }

    @Transactional
    public void deleteImageByStoredName(String storedName) { // TODO 데이터 일관성 관련 개선 필요
        File file = fileRepository.findByStoredName(storedName)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE));
        fileRepository.delete(file);
        s3ImageRemover.removeImageFromBucket(s3Client, bucket, file.getS3Key());
    }
}
