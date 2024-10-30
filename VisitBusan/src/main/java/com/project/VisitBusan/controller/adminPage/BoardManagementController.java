package com.project.VisitBusan.controller.adminPage;


import com.project.VisitBusan.dto.*;
import com.project.VisitBusan.entity.Board;
import com.project.VisitBusan.service.BoardService;
import com.project.VisitBusan.service.ProfileImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/board")
public class BoardManagementController {

    private final BoardService boardService;
    private final ProfileImageService profileImageService;

    // 게시글 목록 조회 및 검색
    @GetMapping("/list")
    public String boardPage(PageRequestDTO pageRequestDTO,
                            Model model) {

        // 1-1. 게시글 댓글 개수 없는 List 조회
//        PageResponseDTO responseDTO = boardService.list(pageRequestDTO);

        // 1-2. 게시글 조회수,댓글수,좋아요수 List 조회
        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);

        // 1-3. 게시글 파일포함 List 조회
//        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

//        log.info("=> "+responseDTO);

        model.addAttribute("responseDTO", responseDTO);

        return "adminPage/board/list";

    }

    // 게시글 조회
    @GetMapping("/read")
    public String read(Long id,
                       PageRequestDTO pageRequestDTO,
                       Model model) {

        if(id != null) {
            log.info("==> id: "+id);
            log.info("==> pageRequestDTO: "+pageRequestDTO);

            // 게시글 조회 서비스 요청
            BoardDTO boardDTO = boardService.readOne(id);
            model.addAttribute("dto",boardDTO);
            log.info("==> after service boardDTO: "+boardDTO);

            boardService.viewCount(boardDTO);

            //프로필 이미지 조회
            ProfileImageDTO profileImageDTO = new ProfileImageDTO();
            profileImageService.findImage(profileImageDTO, boardDTO.getWriterId());
            model.addAttribute("writerProfileImage", profileImageDTO);
        }

        List<String> day = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
        // 모델에 데이터를 담아 타임리프 템플릿에 전달합니다.
        model.addAttribute("day", day);

        return "adminPage/board/read";

    } // end get read

    
    // 게시글 등록
    @GetMapping("/create")
    public String registerGet(PageRequestDTO pageRequestDTO) {

        return "adminPage/board/create";

    } // end get create

    @PostMapping("/create")
    // BoardDTO는 메서드가 호출 받았을 때 넘겨받은 파라미터 값이 BoardDTO의 필드명과 일치하면 자동 매핑 (일치하는 값만 불러옴)
    public String registerPost(@Valid BoardDTO boardDTO,  // @Valid 넘어온 데이터 BoardDTO의 에러 유무 체크
                               BindingResult bindingResult,  // 감지한 에러 데이터
                               PageRequestDTO pageRequestDTO,
                               RedirectAttributes redirectAttributes) {

        log.info("==> boardDTO: "+boardDTO);

        // 수정 페이지에서 넘겨받은 페이징 정보
        String link = pageRequestDTO.getLink();

        // 클라이언트로 부터 전송받은 boardDTO를 @valid에서 문제가 발생했을 경우
        if(bindingResult.hasErrors()) {
            log.info("==> has errors...");

            // 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());  // 한번 에러값을 보낸 후 없어짐

            // Get방식으로 board/register를 재요청
            return "redirect:/admin/board/create";
        }

        log.info("==> "+boardDTO);
        // 게시글 등록 서비스 호출(DB에 저장)
        Long id = boardService.register(boardDTO);

        redirectAttributes.addFlashAttribute("id",id);
        redirectAttributes.addFlashAttribute("result", "created");

        return "redirect:/admin/board/list?"+link;

    } // end post create


    // 게시글 수정
    @GetMapping("/modify")  // 두개이상 사용시 {}안에 ,쓰고 하나 더 입력
    public String modifyget(Long id,
                            PageRequestDTO pageRequestDTO,
                            Model model) {

        // 게시글 조회 서비스 요청
        BoardDTO boardDTO = boardService.readOne(id);
        model.addAttribute("dto",boardDTO);

        return "adminPage/board/modify";

    } // end get modify
    
    @PostMapping("/modify")
    // BoardDTO는 메서드가 호출 받았을 때 넘겨받은 파라미터 값이 BoardDTO의 필드명과 일치하면 자동 매핑 (일치하는 값만 불러옴)
    public String modifypost(BoardDTO boardDTO,  // @Valid 넘어온 데이터 BoardDTO의 에러 유무 체크
                             BindingResult bindingResult,  // 감지한 에러 데이터
                             PageRequestDTO pageRequestDTO,
                             RedirectAttributes redirectAttributes) {

        log.info("==> boardDTO: "+boardDTO);

        // 수정 페이지에서 넘겨받은 페이징 정보
        String link = pageRequestDTO.getLink();

        // 클라이언트로 부터 전송받은 boardDTO를 @valid에서 문제가 발생했을 경우
        if(bindingResult.hasErrors()) {
            log.info("==> has errors...");

            // addFlashAttribute() => 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());  // 한번 에러값을 보낸 후 없어짐
            redirectAttributes.addFlashAttribute("id", boardDTO.getId());

            // Get방식으로 board/modify+페이징정보 재요청
            return "redirect:/admin/board/modify?"+link;
        }

        // 수정 서비스 요청
        Board board = boardService.modify(boardDTO);
        log.info("==> board: "+board);
        log.info("==> board.getBno: "+board.getId());

        // addFlashAttribute() => 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addFlashAttribute("id", board.getId());

        // redirect방식에서 파라미터 추가 기능
        redirectAttributes.addAttribute("id", board.getId());

        return "redirect:/admin/board/read?"+link;
//        return "redirect:/board/read";

    } // end post modify


    // 게시글 삭제
    @PostMapping("/remove")
    // BoardDTO는 메서드가 호출 받았을 때 넘겨받은 파라미터 값이 BoardDTO의 필드명과 일치하면 자동 매핑 (일치하는 값만 불러옴)
    public String remove(Long id,
                         PageRequestDTO pageRequestDTO,
                         RedirectAttributes redirectAttributes){

        // 수정 페이지에서 넘겨받은 페이징 정보
        String link = pageRequestDTO.getLink();

        // 게시글 삭제 서비스 요청
        boardService.remove(id);

        // addFlashAttribute() => 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
        redirectAttributes.addFlashAttribute("id",id);
        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/admin/board/list?"+link;

    } // end remove

} // end class
