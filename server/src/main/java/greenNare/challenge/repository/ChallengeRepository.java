package greenNare.challenge.repository;

import greenNare.challenge.entity.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {
    Page<Challenge> findByMemberMemberId(int membereId, Pageable pageable);
    List<Challenge> findByMemberMemberId(int memberId);
}
