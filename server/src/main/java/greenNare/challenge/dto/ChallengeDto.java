package greenNare.challenge.dto;

import greenNare.challenge.entity.Challenge;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChallengeDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Post {
        @NotBlank
        private String title;
        @NotBlank
        private String content;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Patch {
        @NotBlank
        private String title;
        @NotBlank
        private String content;
    }

    @AllArgsConstructor
    @Getter
    @Builder
    @Setter
    public static class Response {
        private int challengeId;
        private int memberId;
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String imageUrl;

        private String name;
        private int point;
        private int countReply;

        public void setName(String name) {
            this.name = name;
        }
        public void setPoint(int point) {
            this.point = point;
        }
        public void setCountReply(int countReply) {this.countReply = countReply;}

        public static Response from(Challenge challenge, String imageSaveUrl) {
            return Response.builder()
                    .challengeId(challenge.getChallengeId())
                    .memberId(challenge.getMember().getMemberId())
                    .title(challenge.getTitle())
                    .content(challenge.getContent())
                    .createdAt(challenge.getCreatedAt())
                    .updatedAt(challenge.getUpdatedAt())
                    .name(challenge.getMember().getName())
                    .point(challenge.getMember().getPoint())
                    .imageUrl(imageSaveUrl)
                    .build();
        }
    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static class PageResponse {
        private int challengeId;
        private int memberId;
        private String title;
        private String name;
        private int point;
        private int countReply;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public void setName(String name) {
            this.name = name;
        }
        public void setPoint(int point) {
            this.point = point;
        }
        public void setCountReply(int countReply) {this.countReply = countReply;}

        public static PageResponse from(Challenge challenge, int countReply) {
            return PageResponse.builder()
                    .challengeId(challenge.getChallengeId())
                    .memberId(challenge.getMember().getMemberId())
                    .name(challenge.getMember().getName())
                    .point(challenge.getMember().getPoint())
                    .title(challenge.getTitle())
                    .createdAt(challenge.getCreatedAt())
                    .updatedAt(challenge.getUpdatedAt())
                    .countReply(countReply)
                    .build();
        }

    }
}
