package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.dto.QTalkPickDto_TalkPickResponse;
import balancetalk.talkpick.dto.QTodayTalkPickDto_TodayTalkPickResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

import static balancetalk.talkpick.domain.QTalkPick.talkPick;
import static balancetalk.talkpick.dto.TalkPickDto.TalkPickResponse;
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

    @Override
    public Page<TalkPickResponse> findPagedTalkPicks(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable.getSort());

        List<TalkPickResponse> content = queryFactory
                .select(new QTalkPickDto_TalkPickResponse(
                        talkPick.id, talkPick.title, talkPick.member.nickname,
                        talkPick.createdAt, talkPick.views, talkPick.bookmarks
                ))
                .from(talkPick)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(talkPick.count())
                .from(talkPick);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                String property = order.getProperty();
                switch (property) {
                    case "views" -> {
                        orderSpecifiers.add(getOrderSpecifier(direction, talkPick, property));
                        orderSpecifiers.add(getOrderSpecifier(Order.DESC, talkPick, "createdAt"));
                    }
                    case "createdAt" -> {
                        orderSpecifiers.add(getOrderSpecifier(direction, talkPick, property));
                        orderSpecifiers.add(getOrderSpecifier(Order.DESC, talkPick, "views"));
                    }
                }
            }
        }
        return orderSpecifiers;
    }

    private OrderSpecifier<?> getOrderSpecifier(Order direction, Path<?> parent, String fieldName) {
        Path<Boolean> fieldPath = Expressions.path(Boolean.class, parent, fieldName);
        return new OrderSpecifier<>(direction, fieldPath);
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
