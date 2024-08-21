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
    public void updateResourceIdByStoredNames(Long resourceId, List<String> storedNames) {
        if (storedNames == null) {
            return;
        }

        queryFactory.update(file)
                .set(file.resourceId, resourceId)
                .where(file.storedName.in(storedNames))
                .execute();
    }

    @Override
    public List<String> findImgUrlsByResourceIdAndFileType(Long resourceId, FileType fileType) {
        List<File> images = queryFactory.selectFrom(file)
                .where(file.fileType.eq(fileType), file.resourceId.eq(resourceId))
                .fetch();

        return images.stream()
                .map(image -> "%s%s".formatted(image.getPath(), image.getStoredName()))
                .toList();
    }

    @Override
    public List<String> findStoredNamesByResourceIdAndFileType(Long resourceId, FileType fileType) {
        return queryFactory.select(file.storedName)
                .from(file)
                .where(file.fileType.eq(fileType), file.resourceId.eq(resourceId))
                .fetch();
    }
}
