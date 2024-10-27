package com.project.VisitBusan.repository.search;

import com.project.VisitBusan.dto.BoardFileDTO;
import com.project.VisitBusan.dto.BoardListAllDTO;
import com.project.VisitBusan.dto.BoardListReplyCountDTO;
import com.project.VisitBusan.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
// 구현 클래스 : 반드시 '인터페이스이름+impl" 표현해야 한다.
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    // 인자가 있을 경우
    /*
    public BoardSearchImpl(Class<?> domainClass) {
    super(domainClass);
    */

    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search(Pageable pageable) {

        // Entity -> QDomain
        QBoard board = QBoard.board;
        // JPQLQuery<Board> => jsp : PreparseStatement 역할
        // 1. where 추가
        JPQLQuery<Board> query = from(board);  // select ... from board
        query.where(board.title.contains("1"));  // where title like ...



        // 2. 조건문을 작성하는 클래스
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // where title like '%11%' or content like '%12%'
        booleanBuilder.or(board.title.contains("5"));
        booleanBuilder.or(board.content.contains("8"));
        log.info("==> booleanBuilder: "+booleanBuilder);

        JPQLQuery<Board> query2 = from(board).where(booleanBuilder);
        log.info("==> 1. query2: "+query2);
        query2.where(board.id.gt(7L));
        log.info("==> 2. query2: "+query2);





        // 1. 쿼리문 수행하여 결과 값을 List구조로 반환
        List<Board> list = query.fetch();  // 데이터 모두 반환
        long count = query.fetchCount();  // 검색된 모든 값 카운트

        log.info("==> list: "+list);
        log .info("_______________________");
//        list.stream().forEach(b->log.info(b));
        log.info("==> total: "+count);


        // 3. paging 추가
        // Pageable pageable = PageRequest.of(pageNumber:1, pageSize:5, Sort.by("id"))
        this.getQuerydsl().applyPagination(pageable, query2);  // 인자값으로 넘어온 pageable데이터로 query2 페이징 설정


        // 2. 쿼리문 수행하여 결과 값을 List구조로 반환
        List<Board> list2 = query2.fetch();  // 3.paging추가 이후 // 마지막 페이지 반환 // 마지막 페이지가 비어있으면 안보임(설정한 페이지가 더 많을 경우)
        long count2 = query2.fetchCount();  // 검색된 모든 값 카운트

        log.info("==> list2: "+list2);
        log .info("_______________________");
//        list.stream().forEach(b->log.info(b));
        log.info("==> total2: "+count2);


        // 페이지 처리의 최종 결과: Page<T>타입
        // JPA에서 제공하는 PageImpl클래스는 3개의 파라미터로 Page<T>를 생성
        return new PageImpl<>(list2, pageable, count2);

    }

    @Override
    public Page<Board> searchAll(String category, String[] types, String keyword, Pageable pageable) {    // pageable은 항상 마지막에 와야함

        QBoard board = QBoard.board;

        JPQLQuery<Board> query3 = from(board);
        if(category != null) {

            // 조건문을 작성하는 클래스
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            if ((types != null && types.length>0) && keyword != null) {  // 검색 키워드가 있으면

                // 타입
                for (String type : types) {
                    switch (type) {
                        case "t":
                            booleanBuilder.or(board.title.contains(keyword)); break;
                        case "c":
                            booleanBuilder.or(board.content.contains(keyword)); break;
                        case "w":
                            booleanBuilder.or(board.writer.contains(keyword)); break;
                    }
                } // end for


            } // end if type

            // 카테고리 조건 추가
            booleanBuilder.and(board.category.contains(category));

            query3.where(booleanBuilder);

        } // end if category

        // paging 추가
        this.getQuerydsl().applyPagination(pageable, query3);

        // query 실행
        List<Board> list3 = query3.fetch();
        long count3 = query3.fetchCount();
        // 또는 long count = query3.fetch().size();

        return new PageImpl<>(list3, pageable, count3);
    }

    // 특정 게시글에 대한 댓글 개수 계산하는 인터페이스 구현
    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String category, String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QBoardLike boardLike = QBoardLike.boardLike;

        // 1. query 작성
        JPQLQuery<Board> query = from(board);
        // left(reply)를 기준으로 일치하는 보드 값을 추가로 표시.  reply는 다 나옴.
        query.leftJoin(reply).on(reply.board.eq(board));  // reply.board.eq(board) : reply.board_id == board.id
        query.leftJoin(boardLike).on(boardLike.board.eq(board));  // boardLike.board.eq(board) : boardLike.board_id == board.id

        // 그룹핑
        query.groupBy(board);  // group by board.id

        // 조건문 추가
        if(category != null) {

            // 조건문을 작성하는 클래스
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            if ((types != null && types.length > 0) && keyword != null) {  // 검색 키워드가 있으면

                for (String type : types) {
                    switch (type) {
                        case "t":
                            booleanBuilder.or(board.title.contains(keyword));
                            break;
                        case "c":
                            booleanBuilder.or(board.content.contains(keyword));
                            break;
                        case "w":
                            booleanBuilder.or(board.writer.contains(keyword));
                            break;
                    }
                } // end for

            } // end if type

            // 카테고리 조건 추가
            booleanBuilder.and(board.category.contains(category));

            query.where(booleanBuilder);

        } // end if category

        // id > 0 : board테이블에 자료가 존재하면
        query.where(board.id.gt(0L));  // gt(값): greater than 값

        // Projection.bean(): JPA에서 프로젝션: JPQL의 결과를 바로 DTO로 처리하는 기능
        JPQLQuery<BoardListReplyCountDTO> dtoQuery =
                query.select(Projections.bean(
                        BoardListReplyCountDTO.class,  // DTO 타입 선언

                        board.id,
                        board.category,
                        board.title,
                        board.writer,
                        board.writerId,
                        board.regDate,
                        board.viewCount,

                        // reply테이블에서 그룹핑 기준으로 reply개수 계산
                        reply.count().as("replyCount"),
                        boardLike.count().as("boardLikeCount")

                ));

        // 2. paging 추가
        this.getQuerydsl().applyPagination(pageable, query);

        // 3. query 실행
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();
        long count = dtoQuery.fetchCount();



        return new PageImpl<>(dtoList, pageable, count);
    }

    // 게시물 조건 검색 조회 구현
    @Override
    public Page<BoardListAllDTO> searchWithAll(String category, String[] types, String keyword, LocalDate bStartDate, LocalDate bEndDate, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QBoardLike boardLike = QBoardLike.boardLike;
        QFestivalInfo festivalInfo = QFestivalInfo.festivalInfo;
        log.info("=> test board"+board);
        log.info("=> test board.id"+board.id);

        // 1. 쿼리문 작성 (댓글 기준으로 게시글 연결)
        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));  // left join => p댓글 기준으로 게시글 조인
        boardJPQLQuery.leftJoin(boardLike).on(boardLike.board.eq(board));
        boardJPQLQuery.leftJoin(festivalInfo).on(festivalInfo.board.eq(board));

        // 5. 조건문 추가 : where 문 작성

        //  BooleanBuilder: 조건문을 작성하는 클래스
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if ( (types != null && types.length > 0) && keyword != null){// 검색 키워드가 있으면

            for (String type : types){
                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));break;
                    case "writerId":
                        booleanBuilder.or(board.writerId.contains(keyword));break;
                }
            } // end for

        } // end if type

        // 카테고리 조건 추가
        if (category != null) {
            booleanBuilder.and(board.category.contains(category));
        }

        if (bStartDate != null) {
            booleanBuilder.and(festivalInfo.endDate.goe(bStartDate).or(festivalInfo.endDate.isNull())); // 축제 종료일이 검색 시작일 이후 이거나 종료 날짜가 null인 경우
        }
        if ( bEndDate != null ) {
            booleanBuilder.and(festivalInfo.startDate.loe(bEndDate).or(festivalInfo.startDate.isNull())); // 축제 시작일이 검색 종료일 이전 이거나 시작 날짜가 null인 경우
        }
        // end BooleanBuilder

        boardJPQLQuery.where(booleanBuilder);

        // 1.1 게시글번호를 기준으로 그룹핑 처리
        boardJPQLQuery.groupBy(board);

        // 1.2 페이징 설정
        getQuerydsl().applyPagination(pageable, boardJPQLQuery);  // paging 설정

        /** 테스트
         // 2. 쿼리문 실행 : 결과값을 반환
         List<Board> boardList = boardJPQLQuery.fetch();

         // 3. 쿼리문 결과 콘솔에 출력
         boardList.forEach(board1 -> {
         log.info("==> board.getId(): "+board1.getId());
         for (BoardImage boardImage : board1.getImageSet()) {
         log.info("==> boardImage.getFileName(): "+boardImage.getFileName());
         }
         });
         */

        // 4. 쿼리문 작성 : 댓글 개수 파악 => select 항목은 그룹핑된(board id) 게시글정보, 게시글 번호 기준으로 카운트
        // 게시글 번호를 그룹핑하여 board 엔티티와 reply 엔티티 개수 계산
        // Tuple은 Map이랑 비슷
        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct(), boardLike.countDistinct(), festivalInfo);

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {
//            Board board1 = (Board) tuple.get(board);  // 둘 다 됨
            Board board1 = tuple.get(0, Board.class);

//            long replyCount = (long) tuple.get(reply.countDistinct());  // 둘 다 됨
            long replyCount = tuple.get(1, long.class);  // 필드명 없는 관계로 컬럼의 위치 및 타입설정
            long boardLikeCount = tuple.get(2, long.class);  // 필드명 없는 관계로 컬럼의 위치 및 타입설정
            FestivalInfo festivalInfoData = tuple.get(3, FestivalInfo.class);  // 필드명 없는 관계로 컬럼의 위치 및 타입설정

            // boardListAllDTO 객체 생성하여 관련 entity 정보 저장
            BoardListAllDTO boardListAllDTO = BoardListAllDTO.builder()
                    // 1. board Entity -> board DTO
                    .id(board1.getId())
                    .category(board1.getCategory())
                    .title(board1.getTitle())
                    .content(board1.getContent())
                    .writer(board1.getWriter())
                    .writerId(board1.getWriterId())
                    .regDate(board1.getRegDate())
                    .viewCount(board1.getViewCount())
                    .replyCount(replyCount)  // 2. reply count -> replyCount DTO
                    .boardLikeCount(boardLikeCount)  // boardLike count -> boardLikeCount DTO
                    .build();

            if(festivalInfoData != null) {
                boardListAllDTO.setStartDate(festivalInfoData.getStartDate());
                boardListAllDTO.setEndDate(festivalInfoData.getEndDate());
            }


            // 3. BoardImage -> BoardImageDTO
            List<BoardFileDTO> fileDTOS = board1.getBoardFileSet().stream().sorted()
                    .map(boardFile -> BoardFileDTO.builder()
                            .uuid(boardFile.getUuid())
                            .fileName(boardFile.getFileName())
                            .ord(boardFile.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            // 4. boardImageDTO -> boardListAllDTO
            boardListAllDTO.setBoardFiles(fileDTOS);

            return boardListAllDTO;

        }).collect(Collectors.toList());
        // end dtoList = tupleList.stream().map()

        long totalCount = boardJPQLQuery.fetchCount();  // 게시글 전체 개수

        return new PageImpl<>(dtoList, pageable, totalCount);

    } // end searchWithAll()

} // end class

/** 페이징(Paging)은 데이터베이스나 리스트에서 대량의 데이터를 한 번에 처리하지 않고, 여러 페이지로 나누어 처리하는 방법입니다. 이렇게 하면 한 번에 다루는 데이터의 양을 줄여서 성능을 개선하고, 사용자가 데이터를 쉽게 탐색할 수 있게 됩니다. 페이징을 구현할 때 일반적으로 사용하는 요소들은 다음과 같습니다:

 페이지 번호(Page Number):

 사용자가 보고자 하는 페이지의 번호입니다. 일반적으로 1부터 시작합니다.
 페이지 크기(Page Size):

 한 페이지에 포함될 데이터의 개수입니다. 예를 들어, 페이지 크기가 10이면 한 페이지에 10개의 데이터가 표시됩니다.
 총 데이터 개수(Total Count):

 전체 데이터의 총 개수입니다. 이는 페이징의 총 페이지 수를 계산하는 데 사용됩니다.
 총 페이지 수(Total Pages):

 총 데이터 개수와 페이지 크기를 사용하여 계산됩니다. 예를 들어, 총 데이터가 50개이고 페이지 크기가 10이면 총 페이지 수는 5가 됩니다.
 오프셋(Offset):

 데이터베이스 쿼리에서 특정 페이지의 데이터를 가져오기 위해 사용하는 시작 지점입니다. 예를 들어, 페이지 크기가 10이고 페이지 번호가 2인 경우, 오프셋은 10이 됩니다(즉, 11번째 데이터부터 가져옴).
 페이징의 예
 페이지 번호: 1

 오프셋: 0
 가져올 데이터 개수: 10
 페이지 번호: 2

 오프셋: 10
 가져올 데이터 개수: 10
 Spring Data JPA에서 페이징 사용 예시
 Spring Data JPA에서는 Pageable 인터페이스를 사용하여 페이징을 쉽게 구현할 수 있습니다. 예를 들어, 다음과 같은 코드를 통해 페이징을 구현할 수 있습니다:

 java
 코드 복사
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Pageable;

 public void getPagedData() {
 int page = 0; // 페이지 번호 (0부터 시작)
 int size = 10; // 페이지 크기

 Pageable pageable = PageRequest.of(page, size);
 Page<MyEntity> resultPage = myRepository.findAll(pageable);

 System.out.println("Total pages: " + resultPage.getTotalPages());
 System.out.println("Total elements: " + resultPage.getTotalElements());
 System.out.println("Current page number: " + resultPage.getNumber());
 System.out.println("Current page size: " + resultPage.getSize());
 resultPage.getContent().forEach(System.out::println); // 현재 페이지의 데이터
 }
 위 예시에서 PageRequest.of(page, size)를 통해 Pageable 객체를 생성하고, findAll(pageable) 메서드를 사용하여 특정 페이지의 데이터를 가져옵니다. Page 객체는 페이징 관련 정보를 제공하며, getContent() 메서드를 통해 현재 페이지의 데이터를 리스트 형태로 가져올 수 있습니다.

 페이징의 장점
 성능 향상: 한 번에 처리하는 데이터 양을 줄여서 메모리 사용량을 줄이고, 쿼리 성능을 향상시킵니다.
 사용자 경험 개선: 사용자가 데이터를 쉽게 탐색하고, 필요한 데이터를 빠르게 찾을 수 있도록 도와줍니다.
 관리 용이성: 대량의 데이터를 관리하고 처리하는 데 효율적입니다.
 페이징을 적절히 사용하면 대량의 데이터를 효과적으로 처리하고, 애플리케이션의 성능과 사용성을 크게 개선할 수 있습니다.*/

/** JPA에서 제공하는 PageImpl클래스는 3개의 파라미터로 Page<T>를 생성
 public PageImpl(List<T> content, Pageable pageable, long total) {

 super(content, pageable);

 this.total = pageable.toOptional().filter(it -> !content.isEmpty())//
 .filter(it -> it.getOffset() + it.getPageSize() > total)//
 .map(it -> it.getOffset() + content.size())//
 .orElse(total);
 }
 public String toString() {

 String contentType = "UNKNOWN";
 List<T> content = getContent();

 if (!content.isEmpty() && content.get(0) != null) {
 contentType = content.get(0).getClass().getName();
 }

 return String.format("Page %s of %d containing %s instances", getNumber() + 1, getTotalPages(), contentType);
 }*/

/** 페이징 기능 applyPagination
 Applies the given {@link Pageable} to the given {@link JPQLQuery}.
 @param pageable must not be {@literal null}.
 @param query must not be {@literal null}.
 @return the Querydsl {@link JPQLQuery}.

 public <T> JPQLQuery<T> applyPagination(Pageable pageable, JPQLQuery<T> query) {

 Assert.notNull(pageable, "Pageable must not be null");
 Assert.notNull(query, "JPQLQuery must not be null");

 if (pageable.isUnpaged()) {
 return query;
 }

 query.offset(pageable.getOffset());
 query.limit(pageable.getPageSize());

 return applySorting(pageable.getSort(), query);
 }*/
