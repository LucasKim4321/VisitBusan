//package com.project.VisitBusan.service;
//
//import com.project.VisitBusan.dto.PageRequestDTO;
//import com.project.VisitBusan.dto.PageResponseDTO;
//import com.project.VisitBusan.dto.ReplyDTO;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@Log4j2
//public class ReplyServiceTest {
//
//    @Autowired
//    private ReplyService replyService;
//
//    @Test
//    @DisplayName("Reply등록")
//    public void testRegisterReply() {
//
//        ReplyDTO replyDTO = ReplyDTO.builder()
//                .replyText("ReplyDTO text2")
//                .replier("replyer2")
//                .replierId("replyer2")
//                .board_id(21L)
////        .board(board)
//                .build();
//
//        log.info(replyService.register(replyDTO));
//    }
//    @Test
//    @DisplayName("특정 게시글에 대한 댓글 조회")
//    public void testReplyListOBoard() {
//    Long board_id = 21L;
//
//    PageRequestDTO pageRequestDTO = new PageRequestDTO();
//
//    PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOBoard(board_id, pageRequestDTO);
//    responseDTO.getDtoList().stream().forEach(reply -> log.info("=> "+reply));
//    }
//}
//
