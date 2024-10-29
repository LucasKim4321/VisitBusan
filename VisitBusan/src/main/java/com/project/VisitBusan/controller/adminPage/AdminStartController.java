package com.project.VisitBusan.controller.adminPage;

import com.project.VisitBusan.dto.BoardListReplyCountDTO;
import com.project.VisitBusan.dto.MemberDTO;
import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.service.BoardService;
import com.project.VisitBusan.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@PostAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
public class AdminStartController {

    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping("/admin")
    public String mainPage(PageRequestDTO pageRequestDTO,
                           Model model) {

        pageRequestDTO.setSize(5);

        // 게시글 조회

        // 1-1. 게시글 댓글 개수 없는 List 조회
//        PageResponseDTO responseDTO = boardService.list(pageRequestDTO);

        // 1-2. 게시글 조회수,댓글수,좋아요수 List 조회
        PageResponseDTO<BoardListReplyCountDTO> boardResponseDTO = boardService.listWithReplyCount(pageRequestDTO);

        // 1-3. 게시글 파일포함 List 조회
//        PageResponseDTO<BoardAllDataDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

//        log.info("=> "+responseDTO);

        model.addAttribute("boardResponseDTO", boardResponseDTO);


        // 회원 조회

//        List<MemberDTO> memberDTOList = memberService.findAll();
//        model.addAttribute("memberDTOList", memberDTOList);
        PageResponseDTO<MemberDTO> memberResponseDTO = memberService.findAll(pageRequestDTO);
        model.addAttribute("memberResponseDTO", memberResponseDTO);

        return "adminPage/main";
    }

}
