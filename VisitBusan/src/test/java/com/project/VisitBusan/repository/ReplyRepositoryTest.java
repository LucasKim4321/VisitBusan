//package com.project.VisitBusan.repository;
//
//import com.project.VisitBusan.entity.Board;
//import com.project.VisitBusan.entity.Reply;
//import jakarta.transaction.Transactional;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import java.util.Optional;
//import java.util.stream.IntStream;
//
//@SpringBootTest
//@Log4j2
//public class ReplyRepositoryTest {
//
//    @Autowired
//    private ReplyRepository replyRepository;
//
//    @Autowired
//    private BoardRepository boardRepository;
//
//    @Test@DisplayName("Reply 객체 생성")
//    public void testInsertReply() {
//
//        // 1.
//        Long id = 20L;
//
////            Optional<Board> result = boardRepository.findById(id);
////            Board board = result.orElseThrow();
//
//        Board board = boardRepository.findById(id).orElseThrow();
//        log.info("=> findById(n):"+board);
//
//        for (int i=20; i<=40; i++) {
//
//            Reply reply = Reply.builder()
//                    .board(board)   //=> .board_bno(board.getBno())
//                    .replyText("댓글...")
//                    .replierId("id1")
//                    .replier("replyer1")
//                    .build();
//
//            // db
//            Reply savedReply = replyRepository.save(reply);
//            log.info("=> savedReply: "+savedReply);
//        }//end for
//
//
//        // 2. 1에서 100사이 게시글에 대해 무작위로 게시글을 선정하여 댓글 100개 생성하고,
//        //    무작위로 선정되 게시글에 대해 댓글 달기
////        IntStream.rangeClosed(1, 100).forEach(i -> {
////
////            // 게시글 번호 무작위 선정(1에서 100사이)
////            long id = (long) (Math.random()*40)+1;
////            Board board = Board.builder().id(id).build();
////
////            // 특정 게시글에 대한 댓글 생성(특정 게시글과 댓글과 연관관계 설정후 생성)
////            Reply reply = Reply.builder()
////                    // board_bno필드만 생성하여 board의 pk필드 bno값을 설정하고 join상태 설정
////                    .board(board)   //=> .board_bno(board.getBno())
////                    .replyText("댓글..."+i)
////                    .replierId("id"+i)
////                    .replier("replyer"+i)
////                    .build();
////
////            // db처리
////            replyRepository.save(reply);
////
////        });
//
//
//    }
//
//    @Test
//    @DisplayName("특정 게시글 댓글조회")
//    @Transactional
//    public void testBoardReplies(){
//        Long id = 1L;
//
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
//        Page<Reply> result = replyRepository.listOfBoard(id, pageable);
//
//        log.info("=> result: "+result);
//        result.getContent().forEach(reply -> {
//            log.info(reply);
//        });
//    }
//}
