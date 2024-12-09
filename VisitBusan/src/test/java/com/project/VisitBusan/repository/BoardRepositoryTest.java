//package com.project.VisitBusan.repository;
//
//import com.project.VisitBusan.entity.Board;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.IntStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//@Log4j2
////@TestPropertySource(locations="classpath:application-test.properties")  // 환경설정파일 따로 지정
//class BoardRepositoryTest {
//
//    @Autowired
//    private BoardRepository boardRepository;
////    @Autowired
////    private ReplyRepository replyRepository;
//
//    @Test
//    @DisplayName("보드 생성 테스트")
//    void createBoardTest() {
//
//        // 1. 더미 생성
////        IntStream.rangeClosed(1,3).forEach(i-> {
////            Board board = Board.builder()
////                    .category("festival")
////                    .title("title"+i)
////                    .content("content"+i)
////                    .writer("user"+(i%10))
////                    .writerId("user"+(i%10))
////                    .build();
////            Board result = boardRepository.save(board);
////            log.info("==> board id: "+result.getId());
////        });
//
//        // 2. 이미지 포함 더미 생성
//        IntStream.rangeClosed(1,20).forEach(i-> {
//            Board board = Board.builder()
//                    .category("information")
//                    .title("title"+i)
//                    .content("content"+i)
//                    .writer("user"+(i%10))
//                    .writerId("user"+(i%10))
//                    .build();
//
//            // 새로운 첨부파일 추가
//            IntStream.rangeClosed(1,3).forEach(j-> {
//                // 부모객체 내에서 하위객체 생성
//                // board객체에서 BoardImage 객체를 생성
//                board.addFile(UUID.randomUUID().toString(), "file"+i+"_3#"+j+".jpg");
//            });
//
//            Board result = boardRepository.save(board);
//            log.info("==> Id: "+result.getId());
//
//        });
//
//    } // end test
//
//
//    @Test
//    @DisplayName("보드 목록 테스트")
//    void listBoardTest() {
////        this.createBoardTest(); // h2로 테스트 할 때 필요
//
//        List<Board> list = boardRepository.findAll();
//
//        list.stream().forEach(board-> {
//            log.info("==> board: "+board);
//        });
//
//    } // end test
//
//    @Test
//    @DisplayName("보드 카테고리별 목록 테스트")
//    void listBoardCategoryTest() {
////        this.createBoardTest(); // h2로 테스트 할 때 필요
//
//        // paging 정보  pageNumber: 현재 페이지를 설정(실제 만들어진 페이지를 초과하면 각종 오류발생)
//        Pageable pageable = PageRequest.of(0,5, Sort.by("id"));
//        log.info("==> pageable: "+pageable);
//
//        // 카테고리
//        // 관리자 게시판 :
//        //  - 여행정보게시판(travelInfo) : 명소(place), 음식(food), 숙박(accommodation)
//        //  - 여행추천게시판(travelRecommend) : 일정여행(scheduledTravel), 테마여행(themeTravel)
//        //  - 여행가이드게시판(travelGuide) : 가이드(guide), 여행준비(preparation)
//        //  - 축제게시판(festivalBoard) : 축제행사(festival), 공연전시(performance)
//        // 유저 게시판 :
//        //  - 유저게시판(userBoard) : 여행정보(information), 여행일정(schedule), 후기(review)
//        String category = "festival";
//
//        // 키워드, 타입
//        String[] types= {};
//        String keyword = "";
//
//        Page<Board> result = boardRepository.searchAll(category, types, keyword, pageable);
//        log.info("==> result: "+result);
//        log.info("==> result.getContent(): "+result.getContent());  // 현재 페이지의 내용물(content)가 나옴
//        result.getContent().forEach(board->log.info("==> result list: "+board));
//
//        log.info("==> paging info");
//        log.info("==> 총페이지: "+result.getTotalPages());
//        log.info("==> 페이지 사이즈: "+result.getSize());
//        log.info("==> 현재페이지: "+result.getNumber());
//        log.info("==> 이전페이지: "+result.hasPrevious());
//        log.info("==> 다음페이지: "+result.hasNext());
//
//        // 비교판단 Assert  junit라이브러리
//        // AssertThat(result3.hasPrevious()).isEqualTo(0);
//
//    } // end test
//
//    @Test
//    @DisplayName("보드 수정 테스트")
//    void modifyBoardTest() {
//        this.createBoardTest(); // h2로 테스트 할 때 필요
//
//        Long id = 3L;
//
//        Board board = boardRepository.findById(id).orElseThrow();
//
//        board.change("information","updatedTitle","updatedContent");
//
//        Board savedBoard = boardRepository.save(board);
//
////        log.info("==> update: "+savedBoard);
//        this.listBoardTest();
//
//
//    } // end test
//
//    @Test
//    @DisplayName("보드 삭제 테스트")
//    void deleteBoardTest() {
////        this.createBoardTest(); // h2로 테스트 할 때 필요
//
//        Long id = 503L;
//
//        Board board = boardRepository.findById(id).orElseThrow();
//
//        boardRepository.delete(board);
//
//        this.listBoardTest();
//
//    } // end test
//
//    @Test
//    @DisplayName("보드 검색 테스트")
//    void searchBoardTest() {
////        this.createBoardTest(); // h2로 테스트 할 때 필요
//
//        // data 생성 후 삭제 할 글번호 읽어와서 삭제 작업
//
//        // paging 정보  pageNumber: 현재 페이지를 설정(실제 만들어진 페이지를 초과하면 각종 오류발생)
//        Pageable pageable = PageRequest.of(0,5, Sort.by("id"));
//        log.info("==> pageable: "+pageable);
//
//        // 카테고리
//        // 관리자 게시판 :
//        //  - 여행정보게시판(travelInfo) : 명소(place), 음식(food), 숙박(accommodation)
//        //  - 여행추천게시판(travelRecommend) : 일정여행(scheduledTravel), 테마여행(themeTravel)
//        //  - 여행가이드게시판(travelGuide) : 가이드(guide), 여행준비(preparation)
//        //  - 축제게시판(festivalBoard) : 축제행사(festival), 공연전시(performance)
//        // 유저 게시판 :
//        //  - 유저게시판(userBoard) : 여행정보(information), 여행일정(schedule), 후기(review)
//        String category = "guide";
//
//        // 키워드, 타입
//        String[] types= {"t","c","w"};
//        String keyword = "1";
//
//        Page<Board> result = boardRepository.searchAll(category, types, keyword, pageable);
//        log.info("==> result: "+result);
//        log.info("==> result.getContent(): "+result.getContent());  // 현재 페이지의 내용물(content)가 나옴
//        result.getContent().forEach(board->log.info("==> result list: "+board));
//
//        log.info("==> paging info");
//        log.info("==> 총페이지: "+result.getTotalPages());
//        log.info("==> 페이지 사이즈: "+result.getSize());
//        log.info("==> 현재페이지: "+result.getNumber());
//        log.info("==> 이전페이지: "+result.hasPrevious());
//        log.info("==> 다음페이지: "+result.hasNext());
//
//        // 비교판단 Assert  junit라이브러리
//        // AssertThat(result3.hasPrevious()).isEqualTo(0);
//
//    } // end test
//
//
//    @Test
//    void findByIdWithFiles() {
//    }
//}