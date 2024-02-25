package greenNare.image.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import greenNare.challenge.entity.Challenge;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.image.entity.Image;
import greenNare.image.repository.ImageRepository;
import greenNare.product.entity.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class ImageService {
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public static final String SEPERATOR  = "_";
    private final ImageRepository imageRepository;

    public ImageService(AmazonS3 amazonS3, ImageRepository imageRepository) {
        this.amazonS3 = amazonS3;
        this.imageRepository = imageRepository;
    }

    public String saveChallengeImage(Challenge challenge, MultipartFile image){
        String imageName = createImageName(image.getOriginalFilename());
        String imageSavePath = saveImageToS3(image, imageName);

        Image saveImage = new Image(imageSavePath, imageName, challenge);
        imageRepository.save(saveImage);
        return imageSavePath;
    }
    public List<String> saveReviewImages(Review review, List<MultipartFile> images){
        return images.stream().map(img -> {
                    String name = createImageName(img.getOriginalFilename());
                    String imageSavePath = saveImageToS3(img, name);
                    Image image = new Image(imageSavePath, name, review);

                    imageRepository.save(image);
                    return imageSavePath;
                })
                .collect(Collectors.toList());
    }
    public String createImageName(String imageName){
        UUID uuid = UUID.randomUUID();
        return uuid.toString() + SEPERATOR + imageName;
    }
    public String saveImageToS3(MultipartFile image, String name){
        String type = image.getContentType();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(type);
        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName, name, image.getInputStream(), metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_SAVE_FAILED);
        }

        String imageSavePath = amazonS3.getUrl(bucketName, name).toString();
        return imageSavePath;
    }

    public void deleteImageByChallengId(int challengeId){
        imageRepository.findByChallengeChallengeId(challengeId).ifPresent(image -> {
            deleteImage(image.getImageName());
            imageRepository.deleteByChallengeChallengeId(challengeId);
        });
    }

    public void deleteImagesByProductId(int productId){
        List<Image> images = imageRepository.findByProductProductId(productId);

        images.stream().forEach(image -> {
            String fileName = image.getImageName();
            deleteImage(fileName);
        });
        imageRepository.deleteByProductProductId(productId);
    }

    public void deleteImagesByReviewId(int reviewId){
        List<Image> images = imageRepository.findByReviewReviewId(reviewId);
        images.stream().forEach(image -> {
            String fileName = image.getImageName();
            deleteImage(fileName);
        });
        imageRepository.deleteByReviewReviewId(reviewId);
    }

    public void deleteImage(String fileName){
        try {
            amazonS3.deleteObject(bucketName, fileName);
        } catch (SdkClientException e) {
            try {
                throw new IOException("Error deleting file from S3", e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public List<Image> findImageByReviewId(int reviewId){
        return imageRepository.findByReviewReviewId(reviewId);
    }
    public List<Image> findImageByProductId(int productId){
        return imageRepository.findByProductProductId(productId);
    }
    public Optional<Image> findImageByChallengeId(int challengeId){
        log.info("findImageByChallengeId");
        return imageRepository.findByChallengeChallengeId(challengeId);
    }

}
