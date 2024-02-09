package greenNare.reply.service;

import greenNare.auth.jwt.JwtTokenizer;
import greenNare.challenge.entity.Challenge;
import greenNare.challenge.service.ChallengeService;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.member.entity.Member;
import greenNare.member.service.MemberService;
import greenNare.reply.dto.ReplyDto;
import greenNare.reply.entity.Reply;
import greenNare.reply.repository.ReplyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final MemberService memberService;
    private final ChallengeService challengeService;
    private final JwtTokenizer jwtTokenizer;
    public static final int ADD_POINT  = 100;

    public ReplyService(ReplyRepository replyRepository, MemberService memberService, ChallengeService challengeService, JwtTokenizer jwtTokenizer) {
        this.replyRepository = replyRepository;
        this.memberService = memberService;
        this.challengeService = challengeService;
        this.jwtTokenizer = jwtTokenizer;
    }

    public List<ReplyDto.Response> getReplys(int challengeId, Pageable pageable) {
        Page<Reply> replyList = replyRepository.findByChallengeChallengeId(challengeId, pageable);
        List<ReplyDto.Response> replyResponseList = getReplyResponseList(replyList);
        return replyResponseList;
    }
    public List<ReplyDto.Response> getReplyResponseList(Page<Reply> replyList){
        return replyList.stream()
                .map((reply)-> ReplyDto.Response.from(reply)).collect(Collectors.toList());
    }

    public ReplyDto.Response createReply(Reply reply, int challengeId, String token) {
        Member member = findMemberByToken(token);
        Challenge challenge = challengeService.findVerifideChallenge(challengeId);

        reply.setChallenge(challenge);
        reply.setMember(member);
        if(isOverlapReply(member, challengeId)) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_JOINED);
        }
        memberService.addPoint(member.getMemberId(), ADD_POINT);

        replyRepository.save(reply);

        return ReplyDto.Response.from(reply);
    }
    public boolean isOverlapReply(Member member, int challengeId){
        if (overlapUserReplyCnt(member, challengeId) > 1)
            return true;
        return false;
    }
    public int overlapUserReplyCnt(Member member, int challengeId) {
        List<Reply> replyList = replyRepository.findByChallengeChallengeId(challengeId);
        return (int) replyList.stream()
                .filter(r -> r.getMember().getMemberId() == member.getMemberId())
                .count();
    }

    public ReplyDto.Response updateReply (Reply reply, int replyId, String token) {
        Reply findReply = findVerifyReply(replyId);
        validateWriter(findReply, token);

        findReply.setContent(reply.getContent());

        replyRepository.save(findReply);

        return ReplyDto.Response.from(findReply);
    }
    public void deleteReply(int replyId, String token) {
        Reply reply = findVerifyReply(replyId);
        validateWriter(reply, token);
        replyRepository.delete(reply);
    }
    public Reply findVerifyReply(int replyId) {
        Optional<Reply> optionalReply = replyRepository.findById(replyId);
        Reply findReply = optionalReply.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.REPLY_NOT_FOUND));
        return findReply;
    }
    public void validateWriter(Reply reply, String token) {
        int memberId = jwtTokenizer.getMemberId(token);
        if (reply.getMember().getMemberId() != memberId) {
            throw new BusinessLogicException(ExceptionCode.REPLY_WRITER_NOT_MATCHED);
        }
    }

    public Page<Reply> getReplyPage(int challengeId, Pageable pageable) {
        Page<Reply> replyPage = replyRepository.findByChallengeChallengeId(challengeId, pageable);
        return replyPage;
    }

    public int countChallengeReply(int challengeId){
        return (int) replyRepository.findByChallengeChallengeId(challengeId).stream().count();
    }

    public List<Reply> findAllReply(int challengeId) {
        return replyRepository.findByChallengeChallengeId(challengeId);
    }

    public Member findMemberByToken(String token) {
        if(token.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        int memberId = jwtTokenizer.getMemberId(token);
        return memberService.findMemberById(memberId);
    }
}
