package greenNare.challenge.service;

import greenNare.auth.jwt.JwtTokenizer;
import greenNare.challenge.dto.ChallengeDto;
import greenNare.challenge.entity.Challenge;
import greenNare.challenge.repository.ChallengeRepository;
import greenNare.config.SecurityConfiguration;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.image.entity.Image;
import greenNare.image.service.ImageService;
import greenNare.member.entity.Member;
import greenNare.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final MemberService memberService;
    private final SecurityConfiguration securityConfiguration;
    private final JwtTokenizer jwtTokenizer;
    private final ImageService imageService;

    public static final int DELETE_POINT  = 500;

    public ChallengeService(ChallengeRepository challengeRepository, MemberService memberService, SecurityConfiguration securityConfiguration, JwtTokenizer jwtTokenizer, ImageService imageService) {
        this.challengeRepository = challengeRepository;
        this.memberService = memberService;
        this.securityConfiguration = securityConfiguration;
        this.jwtTokenizer = jwtTokenizer;
        this.imageService = imageService;
    }

    public ChallengeDto.Response createChallenge(Challenge challenge, String token, MultipartFile file) throws NullPointerException, IOException {
        if(token.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        Member member = findMemberByToken(token);
        challenge.setMember(member);
        memberService.deletePoint(member.getMemberId(), DELETE_POINT);
        Challenge saveChallenge = challengeRepository.save(challenge);
        String imageSaveUrl = imageService.saveChallengeImage(saveChallenge, file);
        ChallengeDto.Response response = ChallengeDto.Response.from(saveChallenge, imageSaveUrl);
        return response;
    }

    public List<ChallengeDto.PageResponse> getChallenges(Pageable pageable) {
        log.info("getChallenges start");

        Page<Challenge> challengePage = challengeRepository.findAll(pageable);

        List<ChallengeDto.PageResponse> challengeDtoPageResponeList = challengePageToChallengeDtoPageResponseList(challengePage);
        return challengeDtoPageResponeList;
    }
    public List<ChallengeDto.PageResponse> challengePageToChallengeDtoPageResponseList(Page<Challenge> challengePage){
        return challengePage.stream()
                .map(challenge -> {
                            int countReply = getReplyCountForChallenge(challenge.getChallengeId());
                            ChallengeDto.PageResponse response =  ChallengeDto.PageResponse.from(challenge, countReply);
                            return response;
                        }
                ).collect(Collectors.toList());
    }
    public Page<Challenge> getChallengesPage(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        Page<Challenge> challengePage = challengeRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return challengePage;
    }

    public ChallengeDto.Response getChallenge(int challengeId) {
        Challenge challenge = findVerifideChallenge(challengeId);
        Image findImage = imageService.findImageByChallengeId(challengeId);
        ChallengeDto.Response response = ChallengeDto.Response.from(challenge, findImage.getImageUrl());
        response.setCountReply(getReplyCountForChallenge(challengeId));

        return response;
    }
    public Page<Challenge> getMyChallengePage(Pageable pageable, String token){
        int memberId = jwtTokenizer.getMemberId(token);
        Page<Challenge> challengePage = challengeRepository.findByMemberMemberId(memberId, pageable);
        return challengePage;
    }

    public ChallengeDto.Response updateChallenge(Challenge postChallenge, int challengeId, MultipartFile image, String token) throws IOException {
        Challenge findChallenge = findVerifideChallenge(challengeId);

        validateWriter(findChallenge.getMember(), token);

        Optional.ofNullable(postChallenge.getTitle())
                .ifPresent(title -> findChallenge.setTitle(title));
        Optional.ofNullable(postChallenge.getContent())
                .ifPresent(content -> findChallenge.setContent(content));

        imageService.deleteImageByChallengId(challengeId);
        String imageSaveUrl = imageService.saveChallengeImage(findChallenge, image);

        ChallengeDto.Response response = ChallengeDto.Response.from(findChallenge, imageSaveUrl);
        return response;
    }

    public void deleteChallenge(int challengeId, String token){
        log.info("delete challenge start");
        if(token.isBlank()){
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        log.info("token empty 통과");
        Challenge findChallenge = findVerifideChallenge(challengeId);
        validateWriter(findChallenge.getMember(),token);
        imageService.deleteImageByChallengId(challengeId);

        log.info("find challenge 통과");
        challengeRepository.delete(findChallenge);
    }

    public Challenge findVerifideChallenge(int challengeId) {
        log.info("findVerifiedChallenge challengeId : {}", challengeId);
        Optional<Challenge> optionalChallenge =
                challengeRepository.findById(challengeId);
        Challenge findChallenge = optionalChallenge.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.CHALLENGE_NOT_FOUND));
        log.info("성공");
        return findChallenge;
    }

    public Member findMemberByToken(String token) {
        if(token.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        int memberId = jwtTokenizer.getMemberId(token);
        log.info("token에서 추출한 memberId : {}", memberId);
        return memberService.findMemberById(memberId);
    }

    public void validateWriter(Member member, String token) {
        if(token.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        if (findMemberByToken(token).getMemberId() != member.getMemberId()) {
            log.info("작성자와 접근자(수정) 불일치");
            throw new BusinessLogicException(ExceptionCode.UNMATCHED_WRITER);
        }
        log.info("validateWriter OK");
    }

    public int getReplyCountForChallenge(int challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHALLENGE_NOT_FOUND));

        return challenge.getReply().size();
    }
}