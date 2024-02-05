package balancetalk.module.post.application;

import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public void save(final PostRequestDto postRequestDto) {
        Post postEntity = postRequestDto.toEntity();
        postRepository.save(postEntity);
    }
}
