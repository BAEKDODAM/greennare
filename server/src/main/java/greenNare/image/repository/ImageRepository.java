package greenNare.image.repository;

import greenNare.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ImageRepository extends JpaRepository<Image, Integer> {
    Image findByChallengeChallengeId(long challengeId);
    void deleteByChallengeChallengeId(long challengeId);
}
