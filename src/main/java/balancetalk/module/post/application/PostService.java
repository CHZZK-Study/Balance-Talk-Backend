package balancetalk.module.post.application;

import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.domain.PostTag;
import balancetalk.module.post.dto.PostRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Post save(final PostRequestDto postRequestDto) {
        Post postEntity = postRequestDto.toEntity();
        List<BalanceOption> options = postEntity.getOptions();
        for (BalanceOption option : options) {
            option.addPost(postEntity);
        }

        List<PostTag> postTags = postEntity.getPostTags();
        for (PostTag postTag : postTags) {
            postTag.addPost(postEntity);
        }
        return postRepository.save(postEntity);
    }
}
