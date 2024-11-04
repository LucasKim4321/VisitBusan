package com.project.VisitBusan.repository;

import com.project.VisitBusan.entity.BoardLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    // 해당 게시글에 대한 좋아요 조회
    @Query("SELECT bl FROM BoardLike bl WHERE bl.board.id = :boardId AND bl.userId = :userId")
    Optional<BoardLike> findByUserId(Long boardId, String userId);

    // 해당 게시글에 대한 좋아요 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM BoardLike bl WHERE bl.board.id = :boardId AND bl.userId = :userId")
    void deleteByUserId(Long boardId, String userId);


    @Query("SELECT count(bl.id) FROM BoardLike bl WHERE bl.board.id = :b_id")
    long countBoardLike(@Param("b_id") Long board_id);

}
