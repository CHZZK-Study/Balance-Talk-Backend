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
    TALK_PICK_VOTE_1000("작성한 톡픽에 투표한 사람이 1000명! ‘후끈한 핫플 (캐릭터) 배찌를 얻었어요!"),
    TALK_PICK_COMMENT("MY 톡픽이 댓글 %d개를 달성했어요!"),
    TALK_PICK_COMMENT_100("MY 톡픽에 댓글이 100개! ‘와글와글 (캐릭터) 배찌를 얻었어요!"),
    TALK_PICK_COMMENT_1000("MY 톡픽에 댓글이 1000개! ‘북적북적 (캐릭터) 배찌를 얻었어요!"),
    TALK_PICK_RATIO_2_1("%s '%s'이 압도적으로 우세해요! '마이웨이 (캐릭터) 배찌를 얻었어요!'"),
    TALK_PICK_RATIO_3_1("%s '%s'이 압승중! '이게 나야 (캐릭터) 배찌를 얻었어요!'"),
    GAME_VOTE("MY 밸런스게임에 벌써 %d명이 투표했어요!"),
    GAME_VOTE_100("MY 밸런스게임에 투표한 사람이 100명! ‘물이 좋은 (캐릭터) 배찌를 얻었어요!"),
    GAME_VOTE_1000("MY 밸런스게임에 투표한 사람이 1000명! ‘파도에 올라탄 (캐릭터) 배찌를 얻었어요!"),
    GAME_BOOKMARK("MY 밸런스게임이 저장 %d개를 달성했어요!"),
    GAME_BOOKMARK_100("MY 밸런스게임을 100명이나 저장! ‘트렌드 리더 (캐릭터) 배찌를 얻었어요!"),
    GAME_BOOKMARK_1000("MY 밸런스게임을 1000명이나 저장! ‘이정도면 문화대통령 (캐릭터) 배찌를 얻었어요!");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }
}
