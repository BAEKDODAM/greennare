package greenNare.reply.dto;

import greenNare.reply.entity.Reply;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Builder
public class ReplyDto {
    @AllArgsConstructor
    @Getter
    @Builder
    public static class Response {
        private int replyId;
        private int memberId;
        private int challengeId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private String name;
        private int point;

        public void setName(String name) {
            this.name = name;
        }

        public void setPoint(int point) {
            this.point = point;
        }

        public static Response from(Reply reply) {
            return Response.builder()
                    .replyId(reply.getReplyId())
                    .memberId(reply.getMember().getMemberId())
                    .challengeId(reply.getChallenge().getChallengeId())
                    .content(reply.getContent())
                    .name(reply.getMember().getName())
                    .point(reply.getMember().getPoint())
                    .createdAt(reply.getCreatedAt())
                    .updatedAt(reply.getUpdatedAt())
                    .build();
        }

    }

    @Getter
    @Setter
    public static class Post {
        private String content;
    }
}
