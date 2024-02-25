package greenNare.reply.mapper;

import greenNare.reply.dto.ReplyDto;
import greenNare.reply.entity.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReplyMapper {
    Reply replyPostDtoToReply(ReplyDto.Post replyPostDto);
}

