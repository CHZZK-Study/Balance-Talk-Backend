package balancetalk.file.application;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileProcessor;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.MultipartFiles;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.file.dto.UploadFileResponse;
import balancetalk.global.exception.BalanceTalkException;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static balancetalk.global.exception.ErrorCode.NOT_FOUND_FILE;
import static balancetalk.global.exception.ErrorCode.NOT_UPLOADED_IMAGE_FOR_DB_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Operations s3Operations;
    private final FileProcessor fileProcessor;
    private final FileRepository fileRepository;

    @Value(value = "${picko.aws.s3.endpoint}")
    private String s3EndPoint;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public UploadFileResponse uploadImages(MultipartFiles multipartFiles) { // TODO 데이터 일관성 관련 개선 필요
        FileType fileType = multipartFiles.fileType();

        List<String> s3Keys = new ArrayList<>();
        List<String> imgUrls = new ArrayList<>();
        List<String> storedNames = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles.multipartFiles()) {
            try {
                File file = fileProcessor.process(multipartFile, s3EndPoint + fileType.getUploadDir(), fileType);
                String s3Key = fileType.getUploadDir() + file.getStoredName();

                // S3에 이미지 파일 업로드
                S3Resource uploaded = s3Operations.upload(bucket, s3Key, multipartFile.getInputStream());
                imgUrls.add(uploaded.getURL().toString());

                // DB에 메타 데이터 저장
                fileRepository.save(file);

                // 이미지 URL 및 고유 이름 저장
                s3Keys.add(s3Key);
                storedNames.add(file.getStoredName());
            } catch (Exception e) { // TODO 예외 처리 구체화
                log.error(e.getMessage());

                // DB에 메타 데이터 저장 중 예외가 발생하면 S3에 업로드된 이미지 제거 후 커스텀 예외로 변환
                for (String key : s3Keys) {
                    s3Operations.deleteObject(bucket, key);
                }

                throw new BalanceTalkException(NOT_UPLOADED_IMAGE_FOR_DB_ERROR);
            }
        }

        return new UploadFileResponse(imgUrls, storedNames);
    }

    @Transactional
    public void deleteImageByStoredName(String storedName) { // TODO 데이터 일관성 관련 개선 필요
        File file = fileRepository.findByStoredName(storedName)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE));
        fileRepository.delete(file);
        s3Operations.deleteObject(bucket, file.getS3Key());
    }
}
