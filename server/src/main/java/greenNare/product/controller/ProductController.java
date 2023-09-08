package greenNare.product.controller;


import greenNare.Response.MultiResponseDto;
import greenNare.Response.SingleResponseDto;
import greenNare.auth.jwt.JwtTokenizer;
import greenNare.cart.service.CartService;
import greenNare.product.dto.GetProductWithImageDto;
import greenNare.product.entity.Product;
import greenNare.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/green")
public class ProductController {
    private ProductService productService;

    private JwtTokenizer jwtTokenizer;
    private CartService cartService;

    public ProductController(ProductService productService, JwtTokenizer jwtTokenizer, CartService cartService) {
        this.productService = productService;
        this.jwtTokenizer = jwtTokenizer;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public void response(){
        System.out.println("h");
    }

    @GetMapping
    public ResponseEntity getProducts(@RequestParam("page") int page,
                                      @RequestParam("size") int size,
                                      @RequestParam("category") String category,
                                      @RequestHeader(value = "Authorization", required = false) String token){

        Page<Product> getProducts = productService.getProducts(page, size, category);

        if(token == null) {
            List<GetProductWithImageDto> responseProducts = productService.getProductsWithImage(getProducts);
            MultiResponseDto response = new MultiResponseDto(responseProducts, getProducts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }



        //토큰있으면 카트리스트도 같이 전송
        else{
            List<Integer> cartProductId = cartService.getLikeProductId(jwtTokenizer.getMemberId(token));
            List<GetProductWithImageDto> responseProductsWithCart = productService.getProductsWithImage(getProducts, cartProductId);
            MultiResponseDto response = new MultiResponseDto(responseProductsWithCart, getProducts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

//        MultiResponseDto response = new MultiResponseDto(responseProducts, getProducts);
//        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity getProduct(@PathVariable("productId") int productId) {
        GetProductWithImageDto productDetails = productService.getProductWithImage(productId);

        SingleResponseDto response = new SingleResponseDto(productDetails);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity findProduct(@RequestParam("productName") String productName,
                                      @RequestParam("page") int page,
                                      @RequestParam("size") int size) {

        List<Product> Products = productService.findProducts(productName);
        SingleResponseDto response = new SingleResponseDto(Products);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
