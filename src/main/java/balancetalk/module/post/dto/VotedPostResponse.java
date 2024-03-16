package balancetalk.module.post.dto;

import balancetalk.module.post.domain.Post;
import balancetalk.module.vote.domain.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class VotedPostResponse {
    private String balanceOptionTitle;
    private LocalDateTime createdAt;
    private String postTitle;

    public static VotedPostResponse fromVoteAndPost(Vote vote, Post post) {
        return VotedPostResponse.builder()
                .balanceOptionTitle(vote.getBalanceOption().getTitle())
                .createdAt(vote.getCreatedAt())
                .postTitle(post.getTitle())
                .build();
    }
}
