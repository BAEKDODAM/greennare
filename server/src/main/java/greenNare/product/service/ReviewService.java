package greenNare.product.service;

import greenNare.auth.jwt.JwtTokenizer;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.member.repository.MemberRepository;
import greenNare.member.service.MemberService;
import greenNare.product.dto.GetReviewWithImageDto;
import greenNare.product.entity.Image;
import greenNare.product.entity.Review;
import greenNare.product.repository.ImageRepository;
import greenNare.product.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;

    private ImageRepository imageRepository;    //imageService를 만들어야할지

    private ProductService productService;

    private MemberRepository memberRepository;

    private MemberService memberService;

    public static final String IMAGE_SAVE_URL = "/home/ssm-user/seb44_main_026/images/";

    public static final String SEPERATOR  = "_";


    public ReviewService(ReviewRepository reviewRepository,
                         ProductService productService,
                         MemberRepository memberRepository,
                         ImageRepository imageRepository,
                         MemberService memberService) {

        this.reviewRepository = reviewRepository;
        this.productService = productService;
        this.memberRepository = memberRepository;
        this.imageRepository = imageRepository;
        this.memberService = memberService;
    }


    public Page<Review> getReviews(int productId, PageRequest pageable) {
        //PageRequest pageable = PageRequest.of(page,size);
        Page<Review> reviews = reviewRepository.findByProductProductId(pageable, productId);

        return reviews;
    }


    public List<GetReviewWithImageDto> getReviewImage(Page<Review> reviews) {
        List<GetReviewWithImageDto> getReviewWithImageDtos = reviews.getContent().stream()
                .map(review -> {
                    List<Image> images = imageRepository.findImagesUriByReviewReviewId(review.getReviewId());
                    List<String> imageLinks = images.stream()
                            .map(image -> image.getImageUri())
                            .collect(Collectors.toList());

                    GetReviewWithImageDto resultDto = new GetReviewWithImageDto(
                            review.getMember().getMemberId(),
                            review.getReviewId(),
                            review.getContext(),
                            review.getCreatedAt(),
                            review.getUpdatedAt(),
                            review.getProduct().getProductId(),
                            imageLinks,
                            review.getMember().getName(),
                            review.getMember().getPoint()
                    );
//                    resultDto.setReviewId(review.getReviewId());
//                    resultDto.setContext(review.getContext());
//                    resultDto.setCreatedAt(review.getCreatedAt());
//                    resultDto.setUpdateId(review.getUpdatedAt());
//                    resultDto.setProductId(review.getProduct().getProductId());
//                    resultDto.setImageLinks(images);
//                    resultDto.setName(review.getMember().getName());
//                    resultDto.setPoint(review.getMember().getPoint());
                    return resultDto;
                })
                .collect(Collectors.toList());

        return getReviewWithImageDtos;
    }


    //create결과 리턴?
    public void createReview(Review review, int memberId, int productId) {
        //
        verifyExistsReview(memberId, productId);

        review.setMember(memberRepository.findBymemberId(memberId));
        review.setProduct(productService.getProduct(productId));
        reviewRepository.save(review);

        System.out.println("createReview " + review);

//        List<String> imageLinks =

        //updatePoint(response-변경된 포인트 전송)
        int point = (int)Math.floor(review.getProduct().getPrice() * 0.01);
        memberService.addPoint(memberId, point);
    }


    public void updateReview(Review review,  int memberId, int productId) {
        //
        Review findReview = findReview(memberId, productId);

        findReview.setContext(review.getContext());
        findReview.setUpdatedAt(LocalDateTime.now());

        reviewRepository.save(findReview);
        System.out.println("updateReview " + review);

    }


    public List<String> saveImage(List<MultipartFile> images) throws IOException{
        List<String> imageLinks = images.stream()
                .map( image -> {
                    UUID uuid = UUID.randomUUID();
                    String imageName = uuid + SEPERATOR + image.getOriginalFilename();

                    File imagefile = new File(IMAGE_SAVE_URL, imageName);
                    try {
                        image.transferTo(imagefile);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return "/images/" + imageName;
                }

        ).collect(Collectors.toList());

        return imageLinks;

    }


    public String getImage() {
        String imageLink = "";
        return imageLink;
    }


    public void deleteReview(int memberId, int productId) {
        Review findReview = findReview(memberId, productId);

        reviewRepository.delete(findReview);
    }


    public void verifyExistsReview(int memberId, int productId) {
        boolean exist = reviewRepository.findByMemberMemberIdAndProductProductId(memberId, productId).isPresent();
        if(exist) throw new BusinessLogicException(ExceptionCode.REVIEW_EXIST);

    }


    public Review findReview(int memberId, int productId) {
        Optional<Review> optionalReview = reviewRepository.findByMemberMemberIdAndProductProductId(memberId, productId);
        Review findReview = optionalReview
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
        return findReview;
    }

}
