package greenNare.challenge.controller;

import greenNare.Response.MultiResponseDto;
import greenNare.Response.SingleResponseDto;
import greenNare.challenge.dto.ChallengeDto;
import greenNare.challenge.entity.Challenge;
import greenNare.challenge.mapper.ChallengeMapper;
import greenNare.challenge.service.ChallengeService;
import greenNare.place.entity.Place;
import greenNare.utils.UriCreator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/nare")
@Validated
@Slf4j
//@EnableWebMvc
public class ChallengeController {
    private final static String CHALLENGE_DEFAULT_URL = "/nare";
    private final ChallengeService challengeService;
    private final ChallengeMapper mapper;

    public ChallengeController(ChallengeService challengeService, ChallengeMapper mapper) {
        this.challengeService = challengeService;
        this.mapper = mapper;
    }

    @GetMapping("/challenge") // 챌린지 전체 조회
    public ResponseEntity getChallenges(final Pageable pageablePageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // challengeId를 내림차순으로 정렬하는 Sort 객체 생성
        Pageable pageable = PageRequest.of(pageablePageSize.getPageNumber(), pageablePageSize.getPageSize(), sort);
        Page<Challenge> challengePage = challengeService.getChallengesPage(pageable);
        List<ChallengeDto.PageResponse> response = challengeService.getChallenges(pageable);
        log.info("!!!! getChallenges 완료");
        return new ResponseEntity<>(new MultiResponseDto<>(response, challengePage), HttpStatus.OK);
    }

    @GetMapping("/{challengeId}") // 챌린지 상세 조회
    public ResponseEntity getChallenge(@PathVariable("challengeId") int challengeId) {
        ChallengeDto.Response response = challengeService.getChallenge(challengeId);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    @GetMapping("/myChallenge")
    public ResponseEntity getMyChallenge(@RequestHeader(value = "Authorization") String token,
                                         Pageable pageable){
        Page<Challenge> challengePage = challengeService.getMyChallengePage(pageable, token);
        List<Challenge> challengeList = challengePage.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(challengeList, challengePage), HttpStatus.OK);
    }

    @PostMapping("/challenge") // 챌린지 생성
    public ResponseEntity postChallenge(@Valid @RequestPart(required = false) ChallengeDto.Post requestBody,
                                        @RequestPart(required = false) MultipartFile image,
                                        @RequestHeader(value = "Authorization", required = false) String token) throws Exception, IOException {
        ChallengeDto.Response createdChallenge = challengeService.createChallenge(mapper.challengePostDtoToChallenge(requestBody), token, image);

        return new ResponseEntity<>(new SingleResponseDto<>(createdChallenge), HttpStatus.CREATED);
    }

    @PatchMapping("/{challengeId}") // 챌린지 수정
    public ResponseEntity patchChallenge2(@PathVariable("challengeId") int challengeId,
                                         @Valid @RequestPart(required = false) ChallengeDto.Patch requestBody,
                                         @RequestPart(required = false) MultipartFile image,
                                         @RequestHeader(value = "Authorization", required = false) String token) throws IOException {
        Challenge patch = mapper.challengePatchDtoToChallenge(requestBody);

        ChallengeDto.Response response = challengeService.updateChallenge(patch, challengeId, image, token);

        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    @PostMapping("/update/{challengeId}") // 챌린지 수정
    public ResponseEntity patchChallenge(@PathVariable("challengeId") int challengeId,
                                         @Valid @RequestPart(required = false) ChallengeDto.Patch requestBody,
                                         @RequestPart(required = false) MultipartFile image,
                                         @RequestHeader(value = "Authorization", required = false) String token) throws IOException {
        Challenge patch = mapper.challengePatchDtoToChallenge(requestBody);

        ChallengeDto.Response response = challengeService.updateChallenge(patch, challengeId, image, token);

        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    @DeleteMapping("/{challengeId}") // 챌린지 삭제
    public ResponseEntity deleteChallenge(@PathVariable("challengeId") int challengeId,
                                          @RequestHeader(value = "Authorization", required = false) String token){
        challengeService.deleteChallenge(challengeId, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
