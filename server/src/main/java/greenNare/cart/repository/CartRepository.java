package greenNare.cart.repository;

import greenNare.cart.entity.Cart;
import greenNare.member.entity.Member;
import greenNare.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByMemberMemberIdAndProductProductId(int memberId, int productId);

    //  Cart 엔티티는 Product 엔티티와 일대다(One-to-Many) 관계를 갖고 있으므로, findProductByMemberMemberId 메서드에서 Page<Product>를 반환하는 것은 논리적으로 X
    //Page<Product> findProductByMemberMemberId(int memberId, PageRequest pageable);

    Member findMemberByMemberMemberId(int memberId);

    List<Cart> findByMemberMemberId(int memberId);

    Page<Cart> findByMemberMemberId(int memberId, PageRequest pageable);

}
