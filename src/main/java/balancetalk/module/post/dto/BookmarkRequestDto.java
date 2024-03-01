package balancetalk.module.post.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Bookmark;
import balancetalk.module.post.domain.Post;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class BookmarkRequestDto {
    public Bookmark toEntity(Member member, Post post) {
        return Bookmark.builder()
                .member(member)
                .post(post)
                .build();
    }
}
