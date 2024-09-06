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

import static balancetalk.talkpick.domain.QTalkPick.talkPick;

@RequiredArgsConstructor
public class SearchTalkPickRepositoryImpl implements SearchTalkPickRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchTalkPickResponse> searchTalkPicks(String keyword, Pageable pageable) {
        long offset = pageable.getOffset();
        int totalSize = pageable.getPageSize();

        List<TalkPick> result = getTalkPicksByExactMatch(keyword, offset, totalSize);

        if (result.size() < totalSize && containsSpacing(keyword)) {
            List<TalkPick> talkPicks = getTalkPicksByBlankIgnoredExactMatch(keyword, offset, totalSize - result.size());
            addUniqueTalkPicksToResult(result, talkPicks);
        }

        if (result.size() < totalSize) {
            List<TalkPick> talkPicks = getTalkPicksByNaturalMode(keyword, offset, totalSize - result.size());
            addUniqueTalkPicksToResult(result, talkPicks);
        }

        List<SearchTalkPickResponse> content = result.stream()
                .map(SearchTalkPickResponse::new)
                .distinct()
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(talkPick.count())
                .from(talkPick);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private boolean containsSpacing(String keyword) {
        return keyword.contains(" ");
    }

    private List<TalkPick> getTalkPicksByExactMatch(String keyword, long offset, int size) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInBooleanMode(addQuotes(keyword)))
                .offset(offset)
                .limit(size)
                .fetch();
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

    private List<TalkPick> getTalkPicksByBlankIgnoredExactMatch(String keyword, long offset, int size) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInBooleanMode(addQuotes(ignoreSpacing(keyword))))
                .offset(offset)
                .limit(size)
                .fetch();
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

    private List<TalkPick> getTalkPicksByNaturalMode(String keyword, long offset, int size) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInNaturalMode(keyword))
                .offset(offset)
                .limit(size)
                .fetch();
    }

    private BooleanExpression matchInNaturalMode(String keyword) {
        return Expressions.booleanTemplate(
                "function('match_talk_pick_in_natural_mode', {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7})",
                talkPick.title, talkPick.summary.firstLine, talkPick.summary.secondLine, talkPick.summary.thirdLine,
                talkPick.content, talkPick.optionA, talkPick.optionB, keyword);
    }
}
