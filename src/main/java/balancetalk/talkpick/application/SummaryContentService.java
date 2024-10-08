package balancetalk.talkpick.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.talkpick.domain.Summary;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.global.exception.ErrorCode.SUMMARY_SIZE_IS_OVER;

@Service
@RequiredArgsConstructor
public class SummaryContentService {

    private final TalkPickReader talkPickReader;
    private final ChatClient chatClient;

    @Retryable(backoff = @Backoff(delay = 1000))
    @Transactional
    public void summaryContent(long talkPickId) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);

        Summary summary = chatClient.prompt()
                .system("- 당신의 역할은 사용자가 입력한 문장을 3줄로 요약하는 것입니다.\n" +
                        "- 각 문장을 firstLine, secondLine, thirdLine 키에 담아주세요.\n" +
                        "- 각 문장의 글자수는 120 이내로 해주세요.")
                .user(talkPick.getContent())
                .call()
                .entity(Summary.class);

        if (summary.isOverSize()) {
            throw new BalanceTalkException(SUMMARY_SIZE_IS_OVER);
        }
        talkPick.updateSummary(summary);
    }
}
