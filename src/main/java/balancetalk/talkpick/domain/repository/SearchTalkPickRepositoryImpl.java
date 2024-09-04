package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.dto.SearchTalkPickResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.StringTokenizer;

import static balancetalk.talkpick.domain.QTalkPick.talkPick;

@RequiredArgsConstructor
public class SearchTalkPickRepositoryImpl implements SearchTalkPickRepositoryCustom {

    private static final int SEARCH_LIMIT_SIZE = 4;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SearchTalkPickResponse> searchLimitedTalkPicks(String keyword) {
        List<TalkPick> result = getTalkPicksByExactMatch(keyword);

        if (isLessThanLimitSize(result) && containsSpacing(keyword)) {
            List<TalkPick> talkPicks = getTalkPicksByBlankIgnoredExactMatch(keyword);
            addUniqueTalkPicksToResult(result, talkPicks);
        }

        if (isLessThanLimitSize(result)) {
            List<TalkPick> talkPicks = getTalkPicksByNaturalMode(keyword);
            addUniqueTalkPicksToResult(result, talkPicks);
        }

        return result.stream()
                .map(SearchTalkPickResponse::new)
                .distinct()
                .toList();
    }

    private boolean isLessThanLimitSize(List<TalkPick> result) {
        return result.size() < SEARCH_LIMIT_SIZE;
    }

    private boolean containsSpacing(String keyword) {
        return keyword.contains(" ");
    }

    private List<TalkPick> getTalkPicksByExactMatch(String keyword) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInBooleanMode(addQuotes(keyword)))
                .limit(SEARCH_LIMIT_SIZE)
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

    private List<TalkPick> getTalkPicksByBlankIgnoredExactMatch(String keyword) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInBooleanMode(addQuotes(ignoreSpacing(keyword))))
                .limit(SEARCH_LIMIT_SIZE)
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

    private List<TalkPick> getTalkPicksByNaturalMode(String keyword) {
        return queryFactory
                .selectFrom(talkPick)
                .where(matchInNaturalMode(keyword))
                .limit(SEARCH_LIMIT_SIZE)
                .fetch();
    }

    private BooleanExpression matchInNaturalMode(String keyword) {
        return Expressions.booleanTemplate(
                "function('match_talk_pick_in_natural_mode', {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7})",
                talkPick.title, talkPick.summary.firstLine, talkPick.summary.secondLine, talkPick.summary.thirdLine,
                talkPick.content, talkPick.optionA, talkPick.optionB, keyword);
    }
}
