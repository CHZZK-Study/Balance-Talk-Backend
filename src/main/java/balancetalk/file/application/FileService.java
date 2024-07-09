package balancetalk.file.application;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileFormat;
import balancetalk.file.domain.FileRepository;
import balancetalk.file.domain.FileType;
import balancetalk.global.exception.BalanceTalkException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import static balancetalk.file.domain.FileType.TALK_PICK;
import static balancetalk.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String END_POINT = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/";
    private static final String UPLOAD_DIR = "talk-pick/";

    private final S3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Transactional
    public String uploadImage(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new BalanceTalkException(NOT_ATTACH_IMAGE);
        }

        // 이미지 고유 이름 생성
        String originalName = multipartFile.getOriginalFilename();
        String storedName = String.format("%s_%s", UUID.randomUUID(), originalName);

        // 이미지 파일 메타 데이터 추출
        long contentLength = multipartFile.getSize();
        FileFormat FileFormat = convertMimeTypeToFileFormat(multipartFile.getContentType());

        // S3에 이미지 저장 & DB에 메타 데이터 저장
        try (InputStream inputStream = multipartFile.getInputStream()) {
            putObjectToS3(UPLOAD_DIR + storedName, inputStream, contentLength);
        } catch (IOException e) {
            throw new BalanceTalkException(FILE_UPLOAD_FAILED);
        }

        try {
            fileRepository.save(
                    createFile(originalName, storedName, END_POINT + UPLOAD_DIR, TALK_PICK, FileFormat, contentLength));
        } catch (Exception e) {
            deleteObjectFromS3(UPLOAD_DIR + storedName);
            throw new BalanceTalkException(NOT_UPLOADED_IMAGE_FOR_DB_ERROR);
        }

        return getUrl(UPLOAD_DIR + storedName);
    }

    private FileFormat convertMimeTypeToFileFormat(String mimeType) {
        if (mimeType == null) {
            throw new IllegalArgumentException("MIME 타입은 NULL이 될 수 없습니다.");
        }

        return Arrays.stream(FileFormat.values())
                .filter(type -> type.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일 타입 : " + mimeType));
    }

    private void putObjectToS3(String key, InputStream inputStream, long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
    }

    private File createFile(String uploadName, String storedName, String path, FileType fileType, FileFormat FileFormat,
                            long contentLength) {

        return File.builder()
                .uploadName(uploadName)
                .storedName(storedName)
                .path(path)
                .fileType(fileType)
                .fileFormat(FileFormat)
                .size(contentLength)
                .build();
    }

    private void deleteObjectFromS3(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    private String getUrl(String key) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.utilities().getUrl(request).toString();
    }
}
