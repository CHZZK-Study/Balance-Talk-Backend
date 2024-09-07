package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.dto.SearchTalkPickResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.StringTokenizer;

import static balancetalk.global.utils.QuerydslUtils.getOrderSpecifiers;
import static balancetalk.talkpick.domain.QTalkPick.talkPick;

@RequiredArgsConstructor
public class SearchTalkPickRepositoryImpl implements SearchTalkPickRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchTalkPickResponse> searchTalkPicks(String keyword, Pageable pageable) {
        int totalSize = pageable.getPageSize();

        // 1. 키워드와 완전 일치하는 단어가 포함된 톡픽 조회
        List<TalkPick> result = findByExactMatch(keyword, pageable, totalSize);

        // 2. 공백 제거한 키워드와 완전 일치하는 단어가 포함된 톡픽 조회
        if (hasMoreTalkPicks(totalSize, result) && containsSpacing(keyword)) {
            List<TalkPick> talkPicks = findByExactMatch(ignoreSpacing(keyword), pageable, totalSize - result.size());
            addUniqueTalkPicksToResult(result, talkPicks);
        }

        // 3. 자연어 모드에 매칭되는 톡픽 조회
        if (hasMoreTalkPicks(totalSize, result)) {
            List<TalkPick> talkPicks = findByNaturalMode(keyword, pageable, totalSize - result.size());
            addUniqueTalkPicksToResult(result, talkPicks);
        }

        // 페이징 응답으로 변환
        List<SearchTalkPickResponse> content = toResponses(result);
        JPAQuery<Long> countQuery = countQueryForTalkPicks();

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private List<TalkPick> findByExactMatch(String keyword, Pageable pageable, int limit) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInBooleanMode(addQuotes(keyword)))
                .orderBy(getOrderSpecifiers(talkPick, pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(limit)
                .fetch();
    }

    private boolean hasMoreTalkPicks(int totalSize, List<TalkPick> result) {
        return result.size() < totalSize;
    }

    private boolean containsSpacing(String keyword) {
        return keyword.contains(" ");
    }

    private BooleanExpression matchInBooleanMode(String keyword) {
        return Expressions.booleanTemplate(
                "function('match_talk_pick_in_boolean_mode', {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7})",
                talkPick.title, talkPick.summary.firstLine, talkPick.summary.secondLine, talkPick.summary.thirdLine,
                talkPick.content, talkPick.optionA, talkPick.optionB, keyword);
    }

    private String addQuotes(String keyword) {
        return "'\"%s\"'".formatted(keyword);
    }

    private String ignoreSpacing(String keyword) {
        StringTokenizer st = new StringTokenizer(keyword);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
        }
        return sb.toString();
    }

    private void addUniqueTalkPicksToResult(List<TalkPick> result, List<TalkPick> talkPicks) {
        for (TalkPick talkPick : talkPicks) {
            if (!result.contains(talkPick)) {
                result.add(talkPick);
            }
        }
    }

    private List<TalkPick> findByNaturalMode(String keyword, Pageable pageable, int limit) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInNaturalMode(keyword))
                .orderBy(getOrderSpecifiers(talkPick, pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression matchInNaturalMode(String keyword) {
        return Expressions.booleanTemplate(
                "function('match_talk_pick_in_natural_mode', {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7})",
                talkPick.title, talkPick.summary.firstLine, talkPick.summary.secondLine, talkPick.summary.thirdLine,
                talkPick.content, talkPick.optionA, talkPick.optionB, keyword);
    }

    private List<SearchTalkPickResponse> toResponses(List<TalkPick> result) {
        return result.stream()
                .map(SearchTalkPickResponse::new)
                .distinct()
                .toList();
    }

    private JPAQuery<Long> countQueryForTalkPicks() {
        return queryFactory
                .select(talkPick.count())
                .from(talkPick);
    }
}
