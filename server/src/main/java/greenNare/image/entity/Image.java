package greenNare.image.entity;

import greenNare.challenge.entity.Challenge;
import greenNare.product.entity.Product;
import greenNare.product.entity.Review;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;
    private String imageName;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "reviewId")
    Review review;

    @ManyToOne
    @JoinColumn(name = "productId")
    Product product;
    @ManyToOne
    @JoinColumn(name = "challengeId")
    Challenge challenge;

    public Image(String imageUrl, String imageName, Challenge challenge) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.challenge = challenge;
    }
    public Image(String imageUrl, String imageName, Review review) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.review = review;
    }
    public Image(String imageUrl, String imageName, Product product) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.product = product;
    }
}
