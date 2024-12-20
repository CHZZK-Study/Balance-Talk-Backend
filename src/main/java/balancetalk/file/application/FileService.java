package balancetalk.file.application;

import static balancetalk.global.exception.ErrorCode.FAIL_UPLOAD_FILE;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_FILE;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileProcessor;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.MultipartFiles;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.file.dto.UploadFileResponse;
import balancetalk.global.exception.BalanceTalkException;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private static final String TEMP_DIRECTORY_PATH = "temp/";

    private final S3Operations s3Operations;
    private final FileProcessor fileProcessor;
    private final FileRepository fileRepository;

    @Value(value = "${picko.aws.s3.endpoint}")
    private String s3EndPoint;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public UploadFileResponse uploadImages(MultipartFiles multipartFiles) {
        FileType fileType = multipartFiles.fileType();

        List<String> s3Keys = new ArrayList<>();
        List<String> imgUrls = new ArrayList<>();
        List<Long> fileIds = new ArrayList<>();

        try {
            for (MultipartFile multipartFile : multipartFiles.multipartFiles()) {
                File file = fileProcessor.process(multipartFile, fileType);
                String directoryPath = String.format("%s%s", fileType.getUploadDir(), TEMP_DIRECTORY_PATH);
                file.updateDirectoryPathAndImgUrl(directoryPath, s3EndPoint);
                String s3Key = file.getS3Key();
                s3Keys.add(s3Key);

                // DB 저장
                fileIds.add(saveFileMetadata(file));

                // S3 업로드
                imgUrls.add(uploadToS3(multipartFile, s3Key));
            }

            return new UploadFileResponse(imgUrls, fileIds);
        } catch (Exception e) {
            log.error("error = ", e);
            deleteFailedUploads(s3Keys);
            throw new BalanceTalkException(FAIL_UPLOAD_FILE);
        }
    }

    private Long saveFileMetadata(File file) {
        fileRepository.save(file);
        return file.getId();
    }

    private String uploadToS3(MultipartFile multipartFile, String s3Key) throws IOException {
        S3Resource uploaded = s3Operations.upload(bucket, s3Key, multipartFile.getInputStream());
        return uploaded.getURL().toString();
    }

    private void deleteFailedUploads(List<String> s3Keys) {
        for (String key : s3Keys) {
            s3Operations.deleteObject(bucket, key);
        }
    }

    @Transactional
    public void deleteImageById(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE));
        fileRepository.delete(file);
        s3Operations.deleteObject(bucket, file.getS3Key());
    }
}
