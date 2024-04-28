package balancetalk.module.post.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByMemberId(Long id, Pageable pageable);

    @Query("select p "
            + "from Post p left join p.likes l "
            + "where p.deadline > current_timestamp "
            + "and function('month', p.createdAt) = function('month', current_timestamp) "
            + "group by p.id "
            + "order by count(l) desc, p.views desc")
    List<Post> findBestPosts(Pageable pageable);

    List<Post> findByTitleContaining(String keyword);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN p.postTags pt " +
            "JOIN pt.tag t " +
            "WHERE t.name = :tagName")
    List<Post> findByPostTagsContaining(String tagName);

    @Query("select p from Post p where p.deadline > function('now')")
    Page<Post> findAllOnlyOpened(Pageable pageable);
}
