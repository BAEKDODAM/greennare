package greenNare.product.service;


import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.image.entity.Image;
import greenNare.image.service.ImageService;
import greenNare.product.dto.GetProductWithImageDto;
import greenNare.product.entity.Product;
import greenNare.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {
    private ProductRepository productRepository;
    private ImageService imageService;

    public ProductService(ProductRepository productRepository, ImageService imageService) {
        this.productRepository = productRepository;
        this.imageService = imageService;
    }

    public Page<Product> getProducts(int page, int size, String category) {
        //PageRequest pageRequest = PageRequest.of(1, 2);
        PageRequest pageRequest = PageRequest.of(page, size);
        if(category.equals("all")) {
            Page<Product> products = productRepository.findAll(pageRequest);
            return products;
        }
        else {
            Page<Product> products = productRepository.findByCategory(pageRequest, category);
            return products;
        }


    }

    public List<GetProductWithImageDto> getProductsWithImage(Page<Product> products) {
        //List<Product> productList = products.getContent();
        List<GetProductWithImageDto> getProductWithImageDtos = products.getContent().stream()
                .map(product -> {
                    List<Image> images = imageService.findImageByProductId(product.getProductId());
                    //List<Image> images = imageRepository.findImagesUriByProductProductId(product.getProductId());
                    List<String> imageLinks = images.stream()
                            .map(image -> image.getImageUrl())
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


    public List<GetProductWithImageDto> getProductsWithImage(Page<Product> products, List<Integer> cartProductId) {
        //List<Product> productList = products.getContent();
        List<GetProductWithImageDto> getProductWithImageDtos = products.getContent().stream()
                .map(product -> {
                    List<Image> images = imageService.findImageByProductId(product.getProductId());//imageRepository.findImagesUriByProductProductId(product.getProductId());
                    List<String> imageLinks = images.stream()
                            .map(image -> image.getImageUrl())
                            .collect(Collectors.toList());
//                    Image image = imageRepository.findImageUriByProductProductId(product.getProductId());
//                    String imageLink = image.getImageUri();
                    //카트에있는 상품번호면 boolean existInMyCart == true
                    log.info("finding cartproducrt");

                    GetProductWithImageDto resultDto = new GetProductWithImageDto(
                            product.getProductId(),
                            product.getProductName(),
                            product.getDetail(),
                            product.getPrice(),
                            product.getCategory(),
                            product.getPoint(),
                            product.getStoreLink(),
                            imageLinks,
                            existInMyCart(cartProductId, product.getProductId())
//                            imageLink
                    );

                    return resultDto;
                })
                .collect(Collectors.toList());

        return getProductWithImageDtos;

    }


    public Product getProduct(int productId) {
        Product productDetails = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        return productDetails;
    }

    public GetProductWithImageDto getProductWithImage(int productId) {
        Product productDetails = getProduct(productId);
        List<Image> images = imageService.findImageByProductId(productId);
        List<String> imageLinks = images.stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
//        String imageLink = imageRepository.findImageUriByProductProductId(productId).getImageUri();
        GetProductWithImageDto resultDto = new GetProductWithImageDto(
                productDetails.getProductId(),
                productDetails.getProductName(),
                productDetails.getDetail(),
                productDetails.getPrice(),
                productDetails.getCategory(),
                productDetails.getPoint(),
                productDetails.getStoreLink(),
                imageLinks,
                false

        );
        return resultDto;
    }

    public GetProductWithImageDto getProductWithImage(int productId, List<Integer> cartProductId) {
        Product productDetails = getProduct(productId); //productRepository.findById(productId);
        List<Image> images = imageService.findImageByProductId(productId);//imageRepository.findImagesUriByProductProductId(productDetails.getProductId());
        List<String> imageLinks = images.stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
//        String imageLink = imageRepository.findImageUriByProductProductId(productId).getImageUri();

        //카트에있는 상품번호면 boolean existInMyCart == true
        GetProductWithImageDto resultDto = new GetProductWithImageDto(
                productDetails.getProductId(),
                productDetails.getProductName(),
                productDetails.getDetail(),
                productDetails.getPrice(),
                productDetails.getCategory(),
                productDetails.getPoint(),
                productDetails.getStoreLink(),
                imageLinks,
                existInMyCart(cartProductId, productId)


        );
        return resultDto;
    }

    public List<Product> findProducts(String productName) {

        List<Product> findProducts = productRepository.findByProductName(productName);
        System.out.println(productName + findProducts);

        //String일 경우 조회결과없음 -> int타입으로 price와비교해서 조회확인(ok)
        //List<Product> findProducts = productRepository.findByPrice(productName);

        return findProducts;
    }

    public boolean existInMyCart(List<Integer> cartProductsId, int productId) {
        log.info(cartProductsId.toString() + " " + productId);

        for(Integer cartProductId : cartProductsId) {
            log.info("cartProductId: " + cartProductId);
            if (cartProductId == productId){
                log.info(productId + "exist in the cart_return true");
                return true;
            }
        }

//        for(int i=0; i<cartProductId.size(); i++) {
//            log.info("cartProductId: " + cartProductId.get(i));
//            if(cartProductId.get(i) == productId){
//                log.info(productId + "exist in the cart_return true");
//                return true;
//            }
//        }

        return false;
    }
}
