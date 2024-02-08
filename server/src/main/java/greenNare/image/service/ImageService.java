package greenNare.image.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import greenNare.challenge.entity.Challenge;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.image.entity.Image;
import greenNare.image.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Transactional
@Service
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

    public Image findImagesByChallengeId(long challengeId) {
        return imageRepository.findByChallengeChallengeId(challengeId);
    }

    public List<String> saveChallengeImages(Challenge challenge, List<MultipartFile> images) {
        return images.stream()
                .map(img -> {
                    UUID uuid = UUID.randomUUID();
                    String type = img.getContentType();
                    String name = uuid.toString() + SEPERATOR + img.getOriginalFilename();


                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentType(type);
                    try {
                        PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                                bucketName, name, img.getInputStream(), metadata
                        ).withCannedAcl(CannedAccessControlList.PublicRead));

                    } catch (IOException e) {
                        throw new BusinessLogicException(ExceptionCode.IMAGE_SAVE_FAILED); //커스텀 예외 던짐.
                    }

                    String imageSavePath = amazonS3.getUrl(bucketName, name).toString(); //데이터베이스에 저장할 이미지가 저장된 주소

                    Image image = new Image();
                    image.setImageUrl(imageSavePath);
                    image.setImageName(name);
                    image.setChallenge(challenge);

                    imageRepository.save(image);
                    return imageSavePath;
                })
                .collect(Collectors.toList());
    }

    public void deleteImageByPostId(long challengeId){
        Image images = imageRepository.findByChallengeChallengeId(challengeId);
        /*
        images.stream().forEach(image -> {
            String fileName = image.getImageName();
            try {
                amazonS3.deleteObject(bucketName, fileName);
            } catch (SdkClientException e) {
                try {
                    throw new IOException("Error deleting file from S3", e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });*/
        imageRepository.deleteByChallengeChallengeId(challengeId);
    }
}
