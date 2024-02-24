package greenNare.image.controller;

import greenNare.image.service.ImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// image 저장 test용 컨트롤러
@RestController
@RequestMapping("/imagetest")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public void postImage(@RequestPart MultipartFile image) {
        String imageSaveUrl = imageService.saveImageToS3(image, "testImg");
        System.out.println(imageSaveUrl);
    }

    @DeleteMapping
    public void deleteImage(){
        imageService.deleteImage("testImg");
    }

}