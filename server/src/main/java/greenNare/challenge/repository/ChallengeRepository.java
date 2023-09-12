package greenNare.challenge.repository;

import greenNare.challenge.dto.ChallengeDto;
import greenNare.challenge.entity.Challenge;
import greenNare.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {
    // inner join
    /*@Query("SELECT c.boardId, c.title, c.content, m.name, m.point "+
            "FROM Challenge c "+
            "JOIN c.member m")
     */
    /*
    @Query(value = "SELECT Challenge.*, Member.name "+
            "FROM Challenge c "+
            "JOIN Member m" +
            "ON c.memberId = m.memberId;", nativeQuery = true)
     */
    /*
    @Query(value = "SELECT * "+
            "FROM Challenge ;"
            , nativeQuery = true)
    Page<Object[]> getAllChallengeWithUsername(Pageable pageable);

     */
/*
    @Query(value = "SELECT c.title, c.content, c.challengeId, c.createdAt, m.name, m.point " +
            "FROM challenge c " +
            "JOIN member m ON c.memberId = m.memberId;", nativeQuery = true)
    ChallengeDto.Response findByMemberId(int memberId);
*/

    Page<Challenge> findByMemberMemberId(int membereId, Pageable pageable);
    List<Challenge> findByMemberMemberId(int memberId);
}
