package greenNare.challenge.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import greenNare.auth.jwt.JwtTokenizer;
import greenNare.challenge.dto.ChallengeDto;
import greenNare.challenge.entity.Challenge;
import greenNare.challenge.repository.ChallengeRepository;
import greenNare.config.SecurityConfiguration;
import greenNare.exception.BusinessLogicException;
import greenNare.exception.ExceptionCode;
import greenNare.member.entity.Member;
import greenNare.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class ChallengeService {



    private final ChallengeRepository challengeRepository;
    private final MemberService memberService;
    private final SecurityConfiguration securityConfiguration;
    private final JwtTokenizer jwtTokenizer;
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;
    private final Storage storage;


    public static final String IMAGE_SAVE_URL = "/home/ssm-user/seb44_main_026/images/";
    public static final String IMAGE_DELETE_URL = "/home/ssm-user/seb44_main_026";
    public static final String SEPERATOR  = "/";
    public static final int DELETE_POINT  = 500;

    public ChallengeService(ChallengeRepository challengeRepository, MemberService memberService, SecurityConfiguration securityConfiguration, JwtTokenizer jwtTokenizer, Storage storage) {
        this.challengeRepository = challengeRepository;
        this.memberService = memberService;
        this.securityConfiguration = securityConfiguration;
        this.jwtTokenizer = jwtTokenizer;
        this.storage = storage;
    }

    public ChallengeDto.Response createChallenge(Challenge challenge, String token, MultipartFile file) throws NullPointerException, IOException {
        if(token.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        Member member = findMemberByToken(token);
        challenge.setMember(member);

        Challenge imageSaveChallenge = saveChallengeImage(challenge, file);

        memberService.deletePoint(member.getMemberId(), DELETE_POINT);

        Challenge saveChallenge = challengeRepository.save(imageSaveChallenge);

        ChallengeDto.Response response = ChallengeDto.Response.from(saveChallenge);
        return response;
    }

    public Challenge saveChallengeImage(Challenge challenge, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.info("patch 요청에 image 없음");
            return challenge;
        }
        String fileName = createFileName();
        String type = file.getContentType();
        saveFileToGcpStorage(fileName, type, file);
        String ImageSavedBucketUrl = createImageSavedBucketUrl(fileName);
        challenge.setImageUrl(ImageSavedBucketUrl);
        return challengeRepository.save(challenge);
    }
    public String createFileName(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    public String createImageSavedBucketUrl(String fileName){
        return bucketName+SEPERATOR+fileName;
    }

    public void saveFileToGcpStorage(String fileName, String type, MultipartFile file) throws IOException {
        storage.create(
                BlobInfo.newBuilder(bucketName, fileName)
                        .setContentType(type)
                        .build(),
                file.getInputStream()
        );
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
        log.info(String.valueOf("#### get challenge 시작 / challengeId {}"+  String.valueOf(challengeId)));

        Challenge challenge = findVerifideChallenge(challengeId);
        log.info("### challenge content : {}", challenge.getContent());
        ChallengeDto.Response response = ChallengeDto.Response.from(challenge);
        response.setCountReply(getReplyCountForChallenge(challengeId)); //(countReply(challengeId));

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

        deleteImg(findChallenge);
        Challenge imageSaveChallenge = saveChallengeImage(findChallenge, image);

        challengeRepository.save(imageSaveChallenge);
        ChallengeDto.Response response = ChallengeDto.Response.from(imageSaveChallenge);
        return response;
    }
    public void deleteImg(Challenge challenge){
        challenge.getImageName();
    }
    public void changeImg(Challenge challenge, MultipartFile file) {
        String imageName = challenge.getImageUrl();
        Storage storage = StorageOptions.newBuilder().setProjectId("greennare").build().getService();
        Blob blob = storage.get(bucketName, imageName);
        if (blob == null) {
            System.out.println("The image " + imageName + " wasn't found in " + bucketName);
            return;
        }
        Storage.BlobSourceOption precondition =
                Storage.BlobSourceOption.generationMatch(blob.getGeneration());

        storage.delete(bucketName,imageName, precondition);
        challenge.setImageUrl(null);
    }

    public void deleteChallenge(int challengeId, String token){
        log.info("##### delete challenge start");
        if(token.isBlank()){
            throw new BusinessLogicException(ExceptionCode.INVALID_TOKEN);
        }
        log.info("##### token empty 통과");
        Challenge findChallenge = findVerifideChallenge(challengeId);
        validateWriter(findChallenge.getMember(),token);

        File file = new File(IMAGE_DELETE_URL+findChallenge.getImageUrl());
        deleteImage(file);
        log.info("##### find challenge 통과");
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
    public void deleteImage(File file){
        if(file.exists()) file.delete();
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
                .orElseThrow(() -> new NoSuchElementException("Challenge not found with ID: " + challengeId));

        return challenge.getReply().size();
    }
}