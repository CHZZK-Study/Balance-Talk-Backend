package balancetalk.file.domain.repository;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static balancetalk.file.domain.QFile.file;

@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public void updateResourceIdAndTypeByStoredNames(Long resourceId, FileType fileType, List<String> storedNames) {
        if (storedNames == null) {
            return;
        }

        queryFactory.update(file)
                .set(List.of(file.resourceId, file.fileType), List.of(resourceId, fileType))
                .where(file.storedName.in(storedNames))
                .execute();
    }

    @Override
    public List<String> findImgUrlsByResourceIdAndFileType(Long resourceId, FileType fileType) {
        List<File> images = queryFactory.selectFrom(file)
                .where(file.fileType.eq(fileType), file.resourceId.eq(resourceId))
                .fetch();

        return images.stream()
                .map(File::getS3Url)
                .toList();
    }

    @Override
    public List<String> findStoredNamesByResourceIdAndFileType(Long resourceId, FileType fileType) {
        return queryFactory.select(file.storedName)
                .from(file)
                .where(file.fileType.eq(fileType), file.resourceId.eq(resourceId))
                .fetch();
    }

    @Override
    public List<Long> findIdsByResourceIdAndFileType(Long resourceId, FileType fileType) {
        return queryFactory.select(file.id)
                .from(file)
                .where(file.fileType.eq(fileType), file.resourceId.eq(resourceId))
                .fetch();
    }
}
