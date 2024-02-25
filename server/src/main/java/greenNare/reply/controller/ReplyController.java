package greenNare.reply.controller;

import greenNare.Response.MultiResponseDto;
import greenNare.Response.SingleResponseDto;
import greenNare.reply.dto.ReplyDto;
import greenNare.reply.entity.Reply;
import greenNare.reply.mapper.ReplyMapper;
import greenNare.reply.service.ReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nare")
@Validated
@Slf4j
public class ReplyController {
    private final ReplyService replyService;
    private final ReplyMapper mapper;

    public ReplyController(ReplyService replyService, ReplyMapper mapper) {
        this.replyService = replyService;
        this.mapper = mapper;
    }

    @GetMapping("/reply/{challengeId}")
    public ResponseEntity getReply(@PathVariable int challengeId,
                                   Pageable pageablePageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageablePageSize.getPageNumber(), pageablePageSize.getPageSize(), sort);


        List<ReplyDto.Response> replyList = replyService.getReplys(challengeId, pageable);
        Page<Reply> replyPage = replyService.getReplyPage(challengeId, pageable);

        return new ResponseEntity<>(new MultiResponseDto<>(replyList, replyPage), HttpStatus.OK);
    }

    @PostMapping("/reply/{challengeId}")
    public ResponseEntity createReply(@PathVariable int challengeId,
                                      @RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestBody ReplyDto.Post replyPostDto) {
        Reply reply = mapper.replyPostDtoToReply(replyPostDto);
        ReplyDto.Response response = replyService.createReply(reply, challengeId, token);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @PatchMapping("/reply/{replyId}")
    public ResponseEntity updateReply(@PathVariable int replyId,
                                      @RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestBody ReplyDto.Post replyPatchDto) {
        Reply reply =  mapper.replyPostDtoToReply(replyPatchDto);
        ReplyDto.Response response = replyService.updateReply(reply, replyId, token);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity deleteReply(@PathVariable int replyId,
                                      @RequestHeader(value = "Authorization", required = false) String token){
        replyService.deleteReply(replyId, token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
