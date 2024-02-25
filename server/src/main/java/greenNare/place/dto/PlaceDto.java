package greenNare.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PlaceDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        private String placeName;
        private double lat;
        private double longi;
    }

    @Getter
    @Builder
    public static class Response {
        private String placeName;
        private double lat;
        private double longi;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

}
