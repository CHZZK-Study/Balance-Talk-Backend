package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.dto.QTodayTalkPickDto_TodayTalkPickResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static balancetalk.talkpick.domain.QTalkPick.talkPick;
import static balancetalk.talkpick.dto.TodayTalkPickDto.TodayTalkPickResponse;
import static balancetalk.vote.domain.QVote.vote;

@RequiredArgsConstructor
public class TalkPickRepositoryImpl implements TalkPickRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public TodayTalkPickResponse findTodayTalkPick() {
        return queryFactory
                .select(new QTodayTalkPickDto_TodayTalkPickResponse(
                        talkPick.id, talkPick.title, talkPick.optionA, talkPick.optionB
                ))
                .from(vote)
                .join(vote.talkPick, talkPick)
                .where(talkPick.id.eq(vote.talkPick.id))
                .groupBy(talkPick.id)
                .orderBy(talkPick.views.desc(), vote.count().desc(), talkPick.createdAt.desc())
                .limit(1)
                .fetchOne();
    }
}
