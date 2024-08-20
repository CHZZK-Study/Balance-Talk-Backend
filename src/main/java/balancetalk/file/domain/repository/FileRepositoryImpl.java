package balancetalk.file.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static balancetalk.file.domain.QFile.file;

@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public void updateResourceIdByStoredNames(long resourceId, List<String> storedNames) {
        queryFactory.update(file)
                .set(file.resourceId, resourceId)
                .where(file.storedName.in(storedNames))
                .execute();
    }
}
