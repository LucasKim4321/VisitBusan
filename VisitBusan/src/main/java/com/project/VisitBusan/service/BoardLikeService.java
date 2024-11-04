package com.project.VisitBusan.service;

import com.project.VisitBusan.dto.BoardLikeDTO;
import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.entity.Board;
import com.project.VisitBusan.entity.BoardLike;

public interface BoardLikeService {

    // 1. 게시글 좋아요 등록
    String register (BoardLikeDTO boardLikeDTO);

    // 2. 게시글 좋아요 조회
    BoardLikeDTO read (Long boardId, String userId);

    // 4. 게시글 좋아요 삭제
    void remove (Long boardId, String userId);

    // 게시글 좋아요 카운트
    long count (Long boardId);

    // 원래는 상속자가 본체를 만들어야 하지만 업데이트 되면서 default 사용하면 interface에 본체를 쓰는게 가능
    // BoardLikeDTO -> Entity : Board entity 객체 처리
    default BoardLike dtoToEntity(BoardLikeDTO boardLikeDTO) {
        // DTO에 있는 게시글 board_id -> board 객체를 생성
        Board board = Board.builder().id(boardLikeDTO.getBoardId()).build();

        BoardLike boardLike = BoardLike.builder()
                .id(boardLikeDTO.getId())
                .board(board)
                .userId(boardLikeDTO.getUserId())
                .build();

        return boardLike;
    }

    // Entity -> BoardLikeDTO : Board  객체 **
    default BoardLikeDTO entityToDTO (BoardLike boardLike) {

        BoardLikeDTO boardLikeDTO = BoardLikeDTO.builder()
                .id(boardLike.getId())
                .userId(boardLike.getUserId())
                .boardId(boardLike.getBoard().getId())
                .regDate(boardLike.getRegDate())
                .build();

        return boardLikeDTO;
    }

}
