package balancetalk.global.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationMessage {
    FIRST_COMMENT_REPLY("MY 댓글에 답글이 달렸어요!"),
    COMMENT_REPLY("내가 작성한 댓글에 %d개의 답글이 달렸어요!"),
    COMMENT_REPLY_50("MY 댓글에 답글이 50개! ‘난리파티 (캐릭터) 배찌를 얻었어요!"),
    COMMENT_REPLY_100("MY 댓글에 답글이 100개! ‘쿵쓰쿵쓰 쿵쿵따 (캐릭터) 배찌를 얻었어요!"),
    COMMENT_LIKE("작성한 댓글이 하트 %d개를 달성했어요!"),
    COMMENT_LIKE_100("작성한 댓글에 하트가 100개! ‘공감왕 (캐릭터) 배찌를 얻었어요!"),
    COMMENT_LIKE_1000("작성한 댓글에 하트가 1000개! ‘공감대왕 (캐릭터) 배찌를 얻었어요!"),
    TALK_PICK_BOOKMARK("작성한 톡픽이 저장 %d개를 달성했어요!"),
    TALK_PICK_BOOKMARK_100("작성한 톡픽을 100명이나 저장! ‘맛깔난 글솜씨 (캐릭터) 배찌를 얻었어요!"),
    TALK_PICK_BOOKMARK_1000("작성한 톡픽을 1000명이나 저장! ‘ 킹왕짱 미다스의 손 (캐릭터) 배찌를 얻었어요!"),
    TALK_PICK_VOTE("작성한 톡픽에 벌써 %d명이 투표했어요!"),
    TALK_PICK_VOTE_100("작성한 톡픽에 투표한 사람이 100명! ‘따끈한 핫플 (캐릭터) 배찌를 얻었어요!"),
    TALK_PICK_VOTE_1000("작성한 톡픽에 투표한 사람이 1000명! ‘후끈한 핫플 (캐릭터) 배찌를 얻었어요!");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }
}
