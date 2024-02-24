package greenNare.image.repository;

import greenNare.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Image findByChallengeChallengeId(long challengeId);
    void deleteByChallengeChallengeId(long challengeId);

    List<Image> findByProductProductId(int productId);
    void deleteByProductProductId(long productId);

    List<Image> findByReviewReviewId(int reviewId);
    void deleteByReviewReviewId(long reviewId);
}
