package com.project.VisitBusan.controller;

import com.project.VisitBusan.dto.BoardLikeDTO;
import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.service.BoardLikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


// REST방식 : 주로 XML, JSON형태의 문자열을 전송하고 이를 컨트롤러에서 처리하는 방식
@RestController
@RequestMapping("/boardLike")
@Log4j2
@RequiredArgsConstructor
public class BoardLikeController {

    private final BoardLikeService boardLikeService;  // 게시글 좋아요 서비스 객체

    // 게시글 좋아요 등록
    @PostMapping(value="/create", consumes = MediaType.APPLICATION_JSON_VALUE)  // 전송받은 data 종류
    public Map<String, String>  register(  // @Valid 제약조건 유효성 검사
                                           @Valid @RequestBody BoardLikeDTO boardLikeDTO, // boardLikeDTO랑 똑같은 이름의 클래스가 있으면 자동으로 값이 들어옴
                                           BindingResult bindingResult  // 유효성 검사 결과가 들어있음. 객체값 검증
                                         ) throws BindException {  //  에러가 존재하면 bindingResult에서 값을 받아서 리턴

        log.info("==> boardLikeDTO: "+boardLikeDTO);
        log.info("==> bindingResult: "+bindingResult.toString());
        log.info("==> BoardLikeDTO.get: "+boardLikeDTO.getId()+","+boardLikeDTO.getBoardId()+","+boardLikeDTO.getRegDate());

        if (bindingResult.hasErrors()) {  // 에러가 존재하면 bindingResult에서 값을 받아서 BindException으로 리턴
            log.info("==> test1 error: "+bindingResult);
            throw new BindException(bindingResult);
        }

        String userId = boardLikeService.register(boardLikeDTO);
        log.info("==> test4 userId: "+userId);


        //Board board = Board.builder().board_id(boardLikeDTO.getBoard_id()).build();

        // 1
//        Map<String, String> resultMap = Map.of("board_id", String.valueOf(boardLikeDTO.getBoard_id()), "replyText" , boardLikeDTO.getReplyText(), "replyer", boardLikeDTO.getReplyer());
//        resultMap.put("id",300L);  // 에러 발생
//        return ResponseEntity.ok(resultMap);  // ResponseEntity.ok()  200성공 코드 + 반환값
//        return new ResponseEntity(resultMap, HttpStatus.OK);  // 같음
//        return ResponseEntity.ok(boardLikeDTO);

        // 2
//        Map<String, Long> resultMap = Map.of("id", id);
//        return resultMap;

        // 3
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("userId", userId+"의 좋아요가 등록되었습니다~");
        return resultMap;

    }

    // 좋아요 삭제
    @DeleteMapping(value="/delete")
    public Map<String, String> remove(@Valid BoardLikeDTO boardLikeDTO, // boardLikeDTO랑 똑같은 이름의 클래스가 있으면 자동으로 값이 들어옴
                                    BindingResult bindingResult  // 유효성 검사 결과가 들어있음. 객체값 검증
                                    ) throws BindException {  //  에러가 존재하면 bindingResult에서 값을 받아서 리턴

        if (bindingResult.hasErrors()) {  // 에러가 존재하면 bindingResult에서 값을 받아서 BindException으로 리턴
            log.info("==> test1 error: "+bindingResult);
            throw new BindException(bindingResult);
        }

        log.info("==> test2 getBoardId: "+boardLikeDTO.getBoardId()+", getUserId: "+boardLikeDTO.getUserId());
        boardLikeService.remove(boardLikeDTO.getBoardId(), boardLikeDTO.getUserId());

        // 클라이언트에게 보낼 data 정보 및 메시지
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("userId", boardLikeDTO.getUserId()+"의 좋아요가 삭제되었습니다.");

        return resultMap;
    }

    // 게시글 좋아요 조회
    @GetMapping(value="/read")
    public BoardLikeDTO getBoardLikeDTO(@Valid BoardLikeDTO boardLikeDTO, // boardLikeDTO랑 똑같은 이름의 클래스가 있으면 자동으로 값이 들어옴
                                        BindingResult bindingResult  // 유효성 검사 결과가 들어있음. 객체값 검증
                                        ) throws BindException {  //  에러가 존재하면 bindingResult에서 값을 받아서 리턴

        if (bindingResult.hasErrors()) {  // 에러가 존재하면 bindingResult에서 값을 받아서 BindException으로 리턴
            log.info("==> test1 error: "+bindingResult);
            throw new BindException(bindingResult);
        }

        BoardLikeDTO result = boardLikeService.read(boardLikeDTO.getBoardId(), boardLikeDTO.getUserId());
        log.info("==> test3 result: "+result);
        return result;
    }

    @GetMapping("/count")
    public long countBoardLike(Long boardId) {

        return boardLikeService.count(boardId);
    }
}


/*
Springdoc 공식 가이드에서 설명하는 어노테이션의 변화는 다음과 같다.

@Api → @Tag
@ApiIgnore
  → @Parameter(hidden = true) or @Operation(hidden = true) or @Hidden
@ApiImplicitParam
  → @Parameter
@ApiImplicitParams
  → @Parameters
@ApiModel
  → @Schema
@ApiModelProperty(hidden = true)
  → @Schema(accessMode = READ_ONLY)
@ApiModelProperty
  → @Schema
@ApiOperation(value = "foo", notes = "bar")
  → @Operation(summary = "foo", description = "bar")
@ApiParam
  → @Parameter
@ApiResponse(code = 404, message = "foo")
  → @ApiResponse(responseCode = "404", description = "foo")

 */