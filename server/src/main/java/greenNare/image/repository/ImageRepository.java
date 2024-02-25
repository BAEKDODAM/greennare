package greenNare.image.repository;

import greenNare.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Optional<Image> findByChallengeChallengeId(int challengeId);
    void deleteByChallengeChallengeId(int challengeId);

    List<Image> findByProductProductId(int productId);
    void deleteByProductProductId(int productId);

    List<Image> findByReviewReviewId(int reviewId);
    void deleteByReviewReviewId(int reviewId);
}
