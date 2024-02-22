package greenNare.member.service;

import greenNare.cart.entity.Cart;
import greenNare.cart.repository.CartRepository;
import greenNare.cart.service.CartService;
import greenNare.config.SecurityConfiguration;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.member.entity.Member;
import greenNare.member.repository.MemberRepository;
import greenNare.product.dto.GetProductWithImageDto;
import greenNare.product.entity.Product;
import greenNare.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageReader;
import java.util.Optional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private MemberRepository memberRepository;
    private SecurityConfiguration securityConfiguration;

//    private CartService cartService;
//
//    private ImageRepository imageRepository;
//
//    private ProductService productService;


    public MemberService(MemberRepository memberRepository,
                         SecurityConfiguration securityConfiguration/*,
                         CartService cartService,
                         ProductService productService,
                         ImageRepository imageRepository*/) {
        this.memberRepository = memberRepository;
        this.securityConfiguration = securityConfiguration;
//        this.cartService = cartService;
//        this.productService = productService;
//        this.imageRepository = imageRepository;

    }
    public Member loginMember(String email, String password) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member = optionalMember.orElse(null);

        if (member != null && securityConfiguration.passwordEncoder().matches(password, member.getPassword())) {
            return member;
        }

        return null;
    }

    public Member createMember(Member member) {
        member.setPassword(securityConfiguration.passwordEncoder().encode(member.getPassword()));

        verifyExistsEmail(member.getEmail());

        return memberRepository.save(member);
    }

    public Member updateMember(Member member) {

        Member findMember = findVerifiedMember(member.getMemberId());


        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));

        return memberRepository.save(findMember);
    }


    public Member findMember(int memberId) {
        return findVerifiedMember(memberId);
    }

    public List<Member> findMember() {

        return (List<Member>) memberRepository.findAll();
    }


    public void deleteMember(int memberId) {
        Member findMember = findVerifiedMember(memberId);

        memberRepository.delete(findMember);
    }


    public Member findVerifiedMember(int memberId) {
        Optional<Member> optionalMember =
                memberRepository.findById(memberId);
        Member findMember =
                optionalMember.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }


    private void verifyExistsEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_EXIST));

    }
    public Member findMemberById(int memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public void addPoint(int memberId, int point) {
        Member member = findMemberById(memberId);
        int changePoint = member.getPoint() + point;
        member.setPoint(changePoint);
        memberRepository.save(member);
    }
    public void deletePoint(int memberId, int point) {
        Member member = findMemberById(memberId);
        int changePoint = member.getPoint() - point;
        if (changePoint < 0) {
            throw new BusinessLogicException(ExceptionCode.POINT_LAKE);
        }
        member.setPoint(changePoint);
        memberRepository.save(member);
    }

//    public Page<Product> getLikeProduts(int memberId, PageRequest pageable) {
//        Page<Product> likeProducts = cartService.findMyLikeProducts(memberId,pageable);//cartService.findMyLikeProducts(memberId, pageable);
//
//        return likeProducts;
//    }
//
//    public List<GetProductWithImageDto> getLikeProductsWithImage(Page<Product> cartProducts) {
////        List<Product> myLikeProducts = cartProducts.getContent()
////                .stream()
////                .map( likeProduct -> {
////                            Product productDetail = productService.getProduct(likeProduct.getProduct().getProductId());
////                            return productDetail;
////
////                        }
////
////                ).collect(Collectors.toList());
//
//        List<GetProductWithImageDto> getProductWithImageDtos = cartProducts.stream()
//                .map(product -> {
//                    List<Image> images = imageRepository.findImagesUriByProductProductId(product.getProductId());
//                    List<String> imageLinks = images.stream()
//                            .map(image -> image.getImageUri())
//                            .collect(Collectors.toList());
////                    Image image = imageRepository.findImageUriByProductProductId(product.getProductId());
////                    String imageLink = image.getImageUri();
//
//                    GetProductWithImageDto resultDto = new GetProductWithImageDto(
//                            product.getProductId(),
//                            product.getProductName(),
//                            product.getPrice(),
//                            product.getCategory(),
//                            product.getPoint(),
//                            imageLinks
////                            imageLink
//                    );
//
//                    return resultDto;
//                })
//                .collect(Collectors.toList());
//
//
//        return getProductWithImageDtos;
//
//    }
}

