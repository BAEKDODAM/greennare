package greenNare.place.entity;

import greenNare.audit.Auditable;
import greenNare.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Place extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int placeId;

    @ManyToOne
    @JoinColumn(name = "memberId")
    Member member;

    @Column
    private String placeName;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double longi;
}
