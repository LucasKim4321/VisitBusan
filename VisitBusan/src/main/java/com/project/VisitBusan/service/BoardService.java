package com.project.VisitBusan.service;

import com.project.VisitBusan.dto.*;
import com.project.VisitBusan.entity.Board;
import com.project.VisitBusan.entity.FestivalInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface BoardService {

    // 게시글 등록 서비스 인터페이스
    long register(BoardDTO boardDTO);

    // 게시글 조회
    BoardDTO readOne (Long id);

    // 게시글 수정
    Board modify(BoardDTO boardDTO);

    // 게시글 삭제
    void remove(Long id);

    // 게시글 목록 : 페이징 처리를 한 게시글 목록
    PageResponseDTO<BoardDTO> list (PageRequestDTO pageRequestDTO);  // 반환값에 따라 실제 PageResponseDTO의 dtoList 타입이 달라짐

    // 댓글의 숫자를 처리하는 인터페이스 : 조회 결과를 List구조에 저장 및 페이징 처리
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

    // 게시글의 이미지와 댓글의 숫자 처리
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    // 게시글 조회수 처리
    void viewCount(BoardDTO boardDTO);

    // 게시글 댓글 카운트
    void baordLikeCount(Long board_id);

    // 메인 게시물 리스트
//    Map<String, List<Board>> mainList();
//    Map<String, List<Board>> mainList2();

    // DTO -> Entity : List<String> fileName -> Board에서 Set<boardImage> 타입으로 변환
    default Board dtoToEntity(BoardDTO boardDTO) {

        // getter DTO -> setter Entity -> DB table 저장
        Board board = Board.builder()
                .id(boardDTO.getId())
                .category(boardDTO.getCategory())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .writerId(boardDTO.getWriterId())
                .viewCount(boardDTO.getViewCount())
                .build();

        // 첨부파일이 있을 경우  // imageSet에 추가
        if (boardDTO.getFileNames() != null) {
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("==vb==");  // 첨부파일 이름 구성 : "UUID값" + "_" + "파일이름.확장자"
                board.addFile(arr[0], arr[1]);  // [0]UUID [1]파일이름
            });
        }

        return board;

    } // end dtoToEntity

    default FestivalInfo toFestivalInfo(BoardDTO boardDTO, Board board) {
        FestivalInfo festivalInfo = FestivalInfo.builder()
                .board(board)
                .contactNum(boardDTO.getContactNum())
                .place(boardDTO.getPlace())
                .host(boardDTO.getHost())
                .supervision(boardDTO.getSupervision())
                .homepage(boardDTO.getHomepage())
                .startDate(boardDTO.getStartDate())
                .endDate(boardDTO.getEndDate())
                .build();
        return festivalInfo;
    }

    // Entity -> DTO : 조회 기능
    default BoardDTO entityToDTO (Board board) {

        // getter Entity -> setter DTO
        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .writerId(board.getWriterId())
                .viewCount(board.getViewCount())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();

        List<String> fileNames = board.getBoardFileSet()
                .stream()
                .sorted()
                .map(boardImage -> boardImage.getUuid()+"==vb=="+boardImage.getFileName())
                .collect(Collectors.toList());

        boardDTO.setFileNames(fileNames);

        return boardDTO;

    } // end entityToDTO

    default BoardDTO entityToDTOAll(Board board, FestivalInfo festivalInfo) {

        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .writerId(board.getWriterId())
                .viewCount(board.getViewCount())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .contactNum(festivalInfo.getContactNum())
                .place(festivalInfo.getPlace())
                .host(festivalInfo.getHost())
                .supervision(festivalInfo.getSupervision())
                .homepage(festivalInfo.getHomepage())
                .startDate(festivalInfo.getStartDate())
                .endDate(festivalInfo.getEndDate())
                .build();

        List<String> fileNames = board.getBoardFileSet()
                .stream()
                .sorted()
                .map(boardImage -> boardImage.getUuid()+"==vb=="+boardImage.getFileName())
                .collect(Collectors.toList());

        boardDTO.setFileNames(fileNames);

        return boardDTO;

    }

} // end class
