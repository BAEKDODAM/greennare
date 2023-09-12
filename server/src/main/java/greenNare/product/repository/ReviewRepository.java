package greenNare.product.repository;

import greenNare.product.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    //쿼리 결과가 GetReviewsResultDto로 바뀌지않음 - 확인 후 수정
//    @Query(value = "SELECT r.*, m.NAME, m.POINT FROM REVIEW r JOIN MEMBER m ON r.MEMBER_ID = m.MEMBER_ID WHERE r.PRODUCT_ID = :productId", nativeQuery = true)
//    Page<GetReviewsResultDto> findByProductId(Pageable pageable, int productId);


    //필요한 데이터만 반환하도록 수정
    Page<Review> findByProductProductId(Pageable pageable, int productId);

    Optional<Review> findByMemberMemberIdAndProductProductId(int memberId, int productId);

    //DataLoder
    Page<Review> findByMemberMemberId(int memberId, PageRequest pageable);


}
