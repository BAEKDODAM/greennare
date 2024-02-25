package greenNare.challenge.controller;

import greenNare.Response.MultiResponseDto;
import greenNare.Response.SingleResponseDto;
import greenNare.challenge.dto.ChallengeDto;
import greenNare.challenge.entity.Challenge;
import greenNare.challenge.mapper.ChallengeMapper;
import greenNare.challenge.service.ChallengeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/nare")
@Validated
@Slf4j
public class ChallengeController {
    private final ChallengeService challengeService;
    private final ChallengeMapper mapper;

    public ChallengeController(ChallengeService challengeService, ChallengeMapper mapper) {
        this.challengeService = challengeService;
        this.mapper = mapper;
    }

    @GetMapping("/challenge") // 챌린지 전체 조회
    public ResponseEntity getChallenges(final Pageable pageablePageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageablePageSize.getPageNumber(), pageablePageSize.getPageSize(), sort);
        Page<Challenge> challengePage = challengeService.getChallengesPage(pageable);
        List<ChallengeDto.PageResponse> response = challengeService.getChallenges(pageable);
        log.info("getChallenges 완료");
        return new ResponseEntity<>(new MultiResponseDto<>(response, challengePage), HttpStatus.OK);
    }

    @GetMapping("/{challengeId}") // 챌린지 상세 조회
    public ResponseEntity getChallenge(@PathVariable("challengeId") int challengeId) {
        ChallengeDto.Response response = challengeService.getChallenge(challengeId);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    @GetMapping("/myChallenge") // 내가 작성한 챌린지 조회
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
