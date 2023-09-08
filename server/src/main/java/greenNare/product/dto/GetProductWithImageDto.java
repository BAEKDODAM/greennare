package greenNare.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductWithImageDto {
    private int productId;
    private String productName;

    private String detail;
    private int price;
    private String category;
    private int point;
    private String storeLink;
    List<String> imageLinks;
    private boolean inMyCart;
    //String imageLink;




}
