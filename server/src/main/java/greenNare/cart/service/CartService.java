package greenNare.cart.service;

import greenNare.cart.entity.Cart;
import greenNare.cart.repository.CartRepository;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.member.service.MemberService;
import greenNare.product.dto.GetProductWithImageDto;
import greenNare.product.entity.Image;
import greenNare.product.entity.Product;
import greenNare.product.repository.ImageRepository;
import greenNare.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private CartRepository cartRepository;
    private ProductService productService;
    private MemberService memberService;

    private ImageRepository imageRepository;

    public CartService(CartRepository cartRepository,
                       ProductService productService,
                       MemberService memberService,
                       ImageRepository imageRepository) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.memberService = memberService;
        this.imageRepository = imageRepository;
    }


//    public void changeLike(/*String token*, */int productId) {
//        if(findExistLike(productId) == false) createLike(productId);
//        else deleteLike(productId);
//    }
    public void createLike(int memberId, int productId) {
        if(findExistLike(memberId, productId) == true) throw new BusinessLogicException(ExceptionCode.PRODUCT_EXIST);
        cartRepository.save(new Cart(memberService.findMember(memberId),productService.getProduct(productId)));
//        cartRepository.save(new Cart(cartRepository.findMemberByMemberMemberId(memberId), productService.getProduct(productId)));
    }

    public void deleteLike(int memberId, int productId) {
        if(findExistLike(memberId, productId) != true) throw new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
        cartRepository.delete(findExistLikeProduct(memberId, productId));
    }

    public boolean findExistLike(int memberId, int productId) {
        Optional<Cart> optionalLike = cartRepository.findByMemberMemberIdAndProductProductId(memberId, productId);
        boolean like = optionalLike.isPresent();

        return like;
    }

    public Cart findExistLikeProduct(int memberId, int productId) {
        Optional<Cart> optionalCart = cartRepository.findByMemberMemberIdAndProductProductId(memberId, productId);
        Cart findCart = optionalCart.orElseThrow(() -> new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));

        return findCart;
    }

    public Page<Cart> getLikeProducts(int memberId, PageRequest pageable) {
        Page<Cart> myCartProductsId = cartRepository.findByMemberMemberId(memberId, pageable);

        return myCartProductsId;
    }

    public List<Integer> getLikeProductId(int memberId){
        List<Integer> likeProductId = cartRepository.findByMemberMemberId(memberId).stream()
                .map(cartProduct -> {
                    int productId = cartProduct.getProduct().getProductId();
                    return productId;
                })
                .collect(Collectors.toList());

        return likeProductId;
    }

    public List<GetProductWithImageDto> getCartProuctsWithImage(Page<Cart> cart) {
        List<Product> myCartProducts = cart.getContent().stream()
                .map(cartProdcut -> {
                    int productId = cartProdcut.getProduct().getProductId();
                    return productService.getProduct(productId);

                })
                .collect(Collectors.toList());

        List<GetProductWithImageDto> getProductWithImageDtos = myCartProducts.stream()
                .map(product -> {
                    List<Image> images = imageRepository.findImagesUriByProductProductId(product.getProductId());
                    List<String> imageLinks = images.stream()
                            .map(image -> image.getImageUri())
                            .collect(Collectors.toList());
//                    Image image = imageRepository.findImageUriByProductProductId(product.getProductId());
//                    String imageLink = image.getImageUri();

                    GetProductWithImageDto resultDto = new GetProductWithImageDto(
                            product.getProductId(),
                            product.getProductName(),
                            product.getDetail(),
                            product.getPrice(),
                            product.getCategory(),
                            product.getPoint(),
                            product.getStoreLink(),
                            imageLinks,
                            false
//                            imageLink
                    );

                    return resultDto;
                })
                .collect(Collectors.toList());

        return getProductWithImageDtos;

    }

}
