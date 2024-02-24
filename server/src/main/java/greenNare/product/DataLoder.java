package greenNare.product;

import greenNare.image.repository.ImageRepository;
import greenNare.member.repository.MemberRepository;

import greenNare.product.entity.Product;
import greenNare.product.repository.ProductRepository;
import greenNare.product.repository.ReviewRepository;
import greenNare.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataLoder implements CommandLineRunner {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    private final ImageRepository imageRepository;

    public DataLoder(ProductService productService,
                     ProductRepository productRepository,
                     ReviewRepository reviewRepository,
                     MemberRepository memberRepository,
                     ImageRepository imageRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.imageRepository = imageRepository;
    }


    @Override
    public void run(String... args) {
//        Product product1 = new Product("칫솔1", "칫솔입니다", 100,10, "a.com", "bathroom1");
//        Product product2 = new Product("칫솔2", "칫솔입니다", 200,20, "b.com", "kitchen");
//        Product product3 = new Product("칫솔3", "칫솔입니다", 300,30, "c.com", "bathroom2");
//
//        productRepository.save(product1);
//        productRepository.save(product2);
//        productRepository.save(product3);
//
        makeTestProduct();
    }

    public void makeTestProduct() {

        productRepository.save(new Product(
                "친환경 직장인 도시락 세트",
                "친환경 직장인 도시락 세트를 소개합니다! 바쁜 일상 속에서도 지구를 위하는 멋진 당신, 그런 자상한 마음을 가진 분들을 위해 모았습니다. 하루 한 끼, 나와 지구의 건강을 생각하는 도시락 문화를 만들어보세요.",
                25000,
                250,
                "https://onlyeco.co.kr/web/product/big/202307/269148e8cb9682e7ac01f5209a1d8cfc.jpg",
                "living"
        ));
        //imageRepository.save(new Image("/images/947cdd39dd19b4c417e8e3ae9364eec4_친환경 직장인 도시락 세트.jpg", productService.getProduct(1)));

        productRepository.save(new Product(
                "업사이클링 문구 선물세트",
                "업사이클링 문구 선물세트를 소개합니다! 학생들도, 직장인들도 매일 사용하는 문구류, 지구를  아프게 하는 플라스틱 대신 버려진 자원으로 만든 업사이클링 문구들로 친환경 일상을 실천해 보세요.",
                22000,
                220,
                "https://onlyeco.co.kr/web/product/big/202307/9665b57cf45c85dfc339dece2a25b73e.jpg",
                "stationery"
        ));

        //imageRepository.save(new Image( "/images/de7710a88708842998d0410ff08de383업사이클링 문구 선물세트.jpg", productService.getProduct(2)));

        productRepository.save(new Product(
                "[아트앤허그] 검수리 플로깅키트",
                "가치있는 쓰레기줍기 활동을 위한 키트",
                10500,
                105,
                "https://onlyeco.co.kr/web/product/big/202307/9665b57cf45c85dfc339dece2a25b73e.jpg",
                "hygiene"
        ));

        //imageRepository.save(new Image( "/images/ed2f458d7a90ad8995defdaebfd240df[아트앤허그] 검수리 플로깅키트.jpg", productService.getProduct(3)));

        productRepository.save(new Product(
                "[블루워시] 고체 섬유유연제 (무향)",
                "친환경 고체 섬유 유연제! 잔여물 걱정없는 잘 녹는 섬유유연제입니다",
                20900,
                209,
                "https://onlyeco.co.kr/web/product/big/202307/9665b57cf45c85dfc339dece2a25b73e.jpg",
                "bathroom"
        ));

        //imageRepository.save(new Image( "https://onlyeco.co.kr/web/product/big/202307/9665b57cf45c85dfc339dece2a25b73e.jpg", productService.getProduct(4)));





//        for (int i=1; i<=30; i++) {
//            productRepository.save(new Product("칫솔"+i, "칫솔입니다", i*100, i, "/images/image.jpg", "bathroom"));
//            productRepository.save(new Product("오이"+i, "오이입니다", i*100, i, "/images/image.jpg", "kitchen"));
//            productRepository.save(new Product("집"+i, "집입니다", i*100, i, "/images/image.jpg", "living"));
//            productRepository.save(new Product("연필"+i, "연필입니다", i*100, i, "/images/image.jpg", "stationery"));
//            productRepository.save(new Product("손수건"+i, "손수건입니다", i*100, i, "/images/image.jpg", "hygiene"));
//        }
//        for(int i=1; i<=30; i++) {
//            memberRepository.save(new Member("email"+i, "name"+i,"password"+i,"/images/image.jpg",i));
//        }
//        for (int i=1; i<=30; i++) {
//            Member member = memberRepository.findBymemberId(i);
////            reviewRepository.save(Review.builder()
////                    .context("리뷰 * "+i)
////                    .product(productRepository.findByProductId(i))
////                    .member(member)
////                    .build());
//            reviewRepository.save(new Review(member, productService.getProduct(i),"리뷰 * "+i ));
//
//        }
//        for (int i=1; i<=30; i++) {
//            imageRepository.save(new Image("/images/image.jpg", productService.getProduct(i)));
//        }
//
//        for (int i=1; i<=30; i++) {
//            for(int j=0; j<3; j++){
//                imageRepository.save(new Image("/images/image.jpg", reviewRepository.findById(i)));
//            }
//        }

    }

}

