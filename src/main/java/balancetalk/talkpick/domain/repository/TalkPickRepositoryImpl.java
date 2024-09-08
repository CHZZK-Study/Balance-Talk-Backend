package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.dto.QTalkPickDto_TalkPickResponse;
import balancetalk.talkpick.dto.QTodayTalkPickDto_TodayTalkPickResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static balancetalk.global.utils.QuerydslUtils.getOrderSpecifiers;
import static balancetalk.talkpick.domain.QTalkPick.talkPick;
import static balancetalk.talkpick.dto.TalkPickDto.TalkPickResponse;
import static balancetalk.talkpick.dto.TodayTalkPickDto.TodayTalkPickResponse;
import static balancetalk.vote.domain.QTalkPickVote.talkPickVote;

@RequiredArgsConstructor
public class TalkPickRepositoryImpl implements TalkPickRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public TodayTalkPickResponse findTodayTalkPick() {
        return queryFactory
                .select(new QTodayTalkPickDto_TodayTalkPickResponse(
                        talkPick.id, talkPick.title, talkPick.optionA, talkPick.optionB
                ))
                .from(talkPick)
                .leftJoin(talkPick.votes, talkPickVote)
                .groupBy(talkPick.id)
                .orderBy(talkPick.views.desc(), talkPickVote.count().desc(), talkPick.createdAt.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Page<TalkPickResponse> findPagedTalkPicks(Pageable pageable) {
        List<TalkPickResponse> content = queryFactory
                .select(new QTalkPickDto_TalkPickResponse(
                        talkPick.id, talkPick.title, talkPick.member.nickname,
                        talkPick.createdAt, talkPick.views, talkPick.bookmarks
                ))
                .from(talkPick)
                .orderBy(getOrderSpecifiers(talkPick, pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(talkPick.count())
                .from(talkPick);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<TalkPickResponse> findBestTalkPicks() {
        return queryFactory
                .select(new QTalkPickDto_TalkPickResponse(
                        talkPick.id, talkPick.title, talkPick.member.nickname,
                        talkPick.createdAt, talkPick.views, talkPick.bookmarks
                ))
                .from(talkPick)
                .orderBy(talkPick.views.desc(), talkPick.createdAt.desc())
                .limit(3)
                .fetch();
    }
}
