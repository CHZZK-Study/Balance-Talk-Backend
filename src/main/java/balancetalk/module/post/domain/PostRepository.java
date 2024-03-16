package balancetalk.module.post.domain;

import java.util.List;
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
}
