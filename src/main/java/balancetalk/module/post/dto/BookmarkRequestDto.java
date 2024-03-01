package balancetalk.module.post.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Bookmark;
import balancetalk.module.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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
