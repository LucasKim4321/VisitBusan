package com.project.VisitBusan.service;

import com.project.VisitBusan.dto.BoardLikeDTO;
import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.entity.BoardLike;
import com.project.VisitBusan.repository.BoardRepository;
import com.project.VisitBusan.repository.BoardLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
public class BoardLikeServiceImpl implements BoardLikeService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final ModelMapper modelMapper;


    // 게시글 좋아요 등록
    @Override
    public String register(BoardLikeDTO boardLikeDTO) {

        /*
        // 게시글 번호 => board Entity 읽기 //  BoardLikeDTO에서 boardId를 쓰기위한 임시방편
        Board board = boardRepository.findById(boardLikeDTO.getBoardId()).orElseThrow();
        boardLikeDTO.setBoard(board);

        // 1.1 dto -> entity
        BoardLike boardLike = modelMapper.map(boardLikeDTO, BoardLike.class);
        Long id = boardLikeRepository.save(boardLike).getId();
        */

        // dto -> entity
        BoardLike boardLike = dtoToEntity(boardLikeDTO);
        log.info("==> test2 boardLike: "+boardLike);
        // entity -> DB에 반영
        String userId = boardLikeRepository.save(boardLike).getUserId();
        log.info("==> test3 userId: "+userId);

        return userId;
    }

    // 게시글 좋아요 조회
    @Override
    public BoardLikeDTO read(Long boardId, String userId) {
        Optional<BoardLike> boardLikeOptional = boardLikeRepository.findByUserId(boardId, userId);
        try {
            BoardLike boardLike = boardLikeOptional.orElseThrow(() -> new RuntimeException("BoardLike not found"));
//            return modelMapper.map(boardLike, BoardLikeDTO.class);
            return entityToDTO(boardLike);  // entity -> dto 전환 후 반환

        } catch (RuntimeException e) {  // 예외 처리
            return null;
        }
    }

    // 게시글 좋아요 삭제
    @Override
    public void remove(Long boardId, String userId) {
        log.info("boardLike remove userId:"+userId);
        boardLikeRepository.deleteByUserId(boardId, userId);

    }

    //  게시글 좋아요 카운트
    @Override
    public long count(Long boardId) {

        return boardLikeRepository.countBoardLike(boardId);
    }

}
