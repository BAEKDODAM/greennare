package greenNare.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_EXIST(409, "이미 등록된 회원입니다"),
    PRODUCT_EXIST(409, "이미 등록된 상품입니다"),
    PRODUCT_NOT_FOUND(404, "등록되지 않은 상품입니다"),
    REVIEW_EXIST(409, "이미 등록된 리뷰가 존재합니다."),
    REVIEW_NOT_FOUND(404, "존재하지 않는 리뷰입니다"),
    PLACE_EXIST(409, "이미 등록된 장소입니다."),
    UNMATCHED_WRITER(403, "접근 권한이 없습니다."),
    CHALLENGE_NOT_FOUND(404,"존재하지 않는 챌린지 입니다."),
    INVALID_TOKEN(404, "유효하지 않은 토큰입니다."),
    PLACE_NOT_FOUND(404, "존재하지 않는 장소 입니다"),
    REPLY_NOT_FOUND(404, "존재하지 않는 댓글입니다"),
    REPLY_WRITER_NOT_MATCHED(403, "댓글 접근 권한이 없습니다."),
    MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),
    POINT_LAKE(403, "포인트가 부족합니다"),
    ALREADY_JOINED(403,"이미 참여한 챌린지 입니다."),
    IMAGE_NOT_FOUND(404,"이미지가 존재하지 않습니다"),
    IMAGE_SAVE_FAILED(403, "이미지를 삭제하지 못했습니다.");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
