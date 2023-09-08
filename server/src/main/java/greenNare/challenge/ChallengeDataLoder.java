/*
package greenNare.challenge;


import greenNare.auth.jwt.JwtTokenizer;
import greenNare.auth.utils.CustomAuthorityUtils;
import greenNare.challenge.entity.Challenge;
import greenNare.challenge.repository.ChallengeRepository;
import greenNare.config.SecurityConfiguration;
import greenNare.member.entity.Member;
import greenNare.member.repository.MemberRepository;
import greenNare.product.entity.Product;
import greenNare.product.entity.Review;
import greenNare.product.repository.ProductRepository;
import greenNare.product.repository.ReviewRepository;
import greenNare.reply.entity.Reply;
import greenNare.reply.repository.ReplyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ChallengeDataLoder implements CommandLineRunner {
    private final ChallengeRepository challengeRepository;
    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;

    public ChallengeDataLoder(ChallengeRepository challengeRepository, ReplyRepository replyRepository, MemberRepository memberRepository) {
        this.challengeRepository = challengeRepository;
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) {
        makeChallenge();
    }
    public void makeChallenge(){

        JwtTokenizer jwtTokenizer = new JwtTokenizer();
        CustomAuthorityUtils authorityUtils = new CustomAuthorityUtils();
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(jwtTokenizer, authorityUtils);
        String password = securityConfiguration.passwordEncoder().encode("greennare12");

        Member member = new Member("guest@email.com", "name", password, 10000);
        memberRepository.save(member); // Member 엔티티를 먼저 저장

        Challenge challenge = new Challenge("title", "content", member);
        challengeRepository.save(challenge); // Challenge 엔티티 저장

        for (int i = 1; i <= 50; i++) {
            challengeRepository.save(new Challenge("title " + i, "content " + i, member));
        }

        for (int j = 1; j < 40; j++) {
            replyRepository.save(new Reply(member, challenge, j + "번 댓글입니다"));
        }
    }
}
 */
