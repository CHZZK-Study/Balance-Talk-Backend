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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static balancetalk.global.exception.ErrorCode.NOT_ATTACH_IMAGE;
import static balancetalk.global.exception.ErrorCode.NOT_UPLOADED_IMAGE_FOR_DB_ERROR;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String END_POINT = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/";
    private static final String UPLOAD_DIR = "talk-pick/";

    private final FileRepository fileRepository;
    private final FileProcessor fileProcessor;
    private final S3ImageUploader s3ImageUploader;
    private final S3ImageRemover s3ImageRemover;
    private final S3ImageUrlGetter s3ImageUrlGetter;

    /**
     * 1. 이미지 파일에서 메타 데이터를 추출(정제)한다.
     * 2. 이미지의 고유 이름과 inputstream을 가져와서 S3 버킷에 업로드한다.
     * 3. DB에 이미지의 메타 데이터를 저장한다.
     * 4. 업로드한 이미지의 URL을 반환한다.
     */
    @Transactional
    public String uploadImage(MultipartFile multipartFile, Long resourceId, FileType fileType) {
        if (multipartFile.isEmpty()) {
            throw new BalanceTalkException(NOT_ATTACH_IMAGE);
        }

        File file = fileProcessor.process(multipartFile, END_POINT + UPLOAD_DIR, resourceId, fileType);

        // S3에 이미지 파일 업로드
        s3ImageUploader.uploadImageToBucket(multipartFile, UPLOAD_DIR, file.getStoredName());

        // DB에 메타 데이터 저장
        try {
            fileRepository.save(file);
        } catch (Exception e) {
            s3ImageRemover.removeImageFromBucket(UPLOAD_DIR + file.getStoredName());
            throw new BalanceTalkException(NOT_UPLOADED_IMAGE_FOR_DB_ERROR);
        }

        // 이미지 URL 반환
        return s3ImageUrlGetter.getUrl(UPLOAD_DIR + file.getStoredName());
    }
}
