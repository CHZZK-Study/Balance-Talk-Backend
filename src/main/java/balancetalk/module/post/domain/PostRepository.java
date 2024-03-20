package balancetalk.module.post.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p "
            + "from Post p left join p.likes l "
            + "where p.deadline > current_timestamp "
            + "and function('month', p.createdAt) = function('month', current_timestamp) "
            + "group by p.id "
            + "order by count(l) desc, p.views desc")
    List<Post> findBestPosts(Pageable pageable);
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM post p " +
            "NATURAL JOIN post_tag pt " +
            "NATURAL JOIN tag t " +
            "WHERE p.post_id = pt.post_id AND pt.tag_id = t.tag_id AND t.name = :tagName", nativeQuery = true)
    Page<Post> findByPostTagsContaining(String tagName, Pageable pageable);
}
