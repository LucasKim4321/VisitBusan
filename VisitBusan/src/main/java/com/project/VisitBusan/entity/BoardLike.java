package com.project.VisitBusan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardLike {

    @Id  // 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    private Board board;

    @Column(name="user_id")
    private String userId;

    @CreatedDate
    @Column(name="reg_date", updatable=false)
    private LocalDateTime regDate;

}
// member는 여러개의 좋아요를 누를 수 있고. member:like(1:N)
// board는 여러개의 좋아요를 받을 수 있다.  board:like(1:N)
// member:like:board(1:N:1)

