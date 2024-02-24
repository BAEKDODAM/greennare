package greenNare.challenge.entity;

import greenNare.audit.Auditable;
import greenNare.image.entity.Image;
import greenNare.member.entity.Member;
import greenNare.reply.entity.Reply;
import lombok.*;
import java.util.List;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Challenge extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int challengeId;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;
    //@Column(nullable = false)
    //private int memberId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
/*
    @Column
    private String imageUrl;

    private String imageName;
*/
    @OneToMany(mappedBy = "challenge")
    private List<Reply> reply;
    @OneToMany(mappedBy = "challenge")
    private List<Image> images;

    public Challenge(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Challenge(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }
}
