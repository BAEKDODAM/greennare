package greenNare.reply.entity;

import greenNare.audit.Auditable;
import greenNare.challenge.entity.Challenge;
import greenNare.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Reply extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int replyId;

    @ManyToOne
    @JoinColumn(name = "MemberId")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "challengeId")
    private Challenge challenge;

    @Column(nullable = false)
    private String content;


    public Reply(Member member, Challenge challenge, String content) {
        this.member = member;
        this.challenge = challenge;
        this.content = content;
    }
}
