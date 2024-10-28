package com.project.VisitBusan.service;

import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.dto.ReplyDTO;
import com.project.VisitBusan.entity.Board;
import com.project.VisitBusan.entity.Reply;

public interface ReplyService {

    // 1. 댓글 등록
    Long register(ReplyDTO replyDTO);
    // 2. 댓글 조회
    ReplyDTO read(Long id);
    // 3. 댓글 수정
    void modify(ReplyDTO replyDTO);
    // 4. 댓글 삭제
    void remove(Long id);

    // 5. 댓글 목록
    PageResponseDTO<ReplyDTO> getListOBoard(Long board_id, PageRequestDTO pageRequestDTO);

    // ReplyDTO -> Entity : Board entity 객체 처리
    default Reply dtoToEntity(ReplyDTO replyDTO) {
        // DTO에 있는 게시글 bno -> board객체를 생성
        Board board = Board.builder().id(replyDTO.getBoard_id()).build();

        Reply reply = Reply.builder()
                .id(replyDTO.getId())
                .replyText(replyDTO.getReplyText())
                .replier(replyDTO.getReplier())
                .replierId(replyDTO.getReplierId())
                .board(board)
                .build();

        return reply;
    }
    // Entity -> ReplyDTO: Board 객체가 필요하지 않으므로 게시물 번호만 처리
    default ReplyDTO entityToDTO(Reply reply){
        ReplyDTO dto = ReplyDTO.builder()
                .id(reply.getId())
                .replyText(reply.getReplyText())
                .replier(reply.getReplier())
                .id(reply.getId())

                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();

        return dto;
    }



}
