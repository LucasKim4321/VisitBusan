package com.project.VisitBusan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.VisitBusan.entity.Board;
import com.project.VisitBusan.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardLikeDTO {

    private Long id;
    @NotNull
    private long boardId;
    @NotEmpty
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;


}
