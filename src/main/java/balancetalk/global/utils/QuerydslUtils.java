package balancetalk.global.utils;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static balancetalk.talkpick.domain.QTalkPick.talkPick;

public class QuerydslUtils {

    public static OrderSpecifier<?>[] getOrderSpecifiers(Path<?> path, Sort sort) {
        if (sort == null || sort.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.SORT_REQUIRED);
        }

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (Sort.Order order : sort) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();
            if (path.equals(talkPick)) {
                return orderSpecifiersForTalkPick(orderSpecifiers, direction, property);
            }
        }

        throw new BalanceTalkException(ErrorCode.FAIL_SORT);
    }

    private static OrderSpecifier<?>[] orderSpecifiersForTalkPick(List<OrderSpecifier<?>> orderSpecifiers, Order direction, String property) {
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

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    private static OrderSpecifier<?> getOrderSpecifier(Order direction, Path<?> path, String fieldName) {
        Path<Boolean> fieldPath = Expressions.path(Boolean.class, path, fieldName);
        return new OrderSpecifier<>(direction, fieldPath);
    }
}
