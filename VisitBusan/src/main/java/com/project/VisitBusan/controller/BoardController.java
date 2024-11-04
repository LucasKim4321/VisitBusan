package com.project.VisitBusan.controller;


import com.project.VisitBusan.dto.*;
import com.project.VisitBusan.entity.Board;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.repository.BoardRepository;
import com.project.VisitBusan.repository.MemberRepository;
import com.project.VisitBusan.service.BoardService;
import com.project.VisitBusan.service.ProfileImageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {

    private final BoardService boardService;
    private final ProfileImageService profileImageService;
    private final MemberRepository memberRepository;


    // 카테고리
    // 관리자 게시판 :
    //  - 여행정보게시판(travelInfo) : 명소(place), 음식(food), 숙박(accommodation)
    //  - 여행추천게시판(travelRecommend) : 일정여행(scheduledTravel), 테마여행(themeTravel)
    //  - 여행가이드게시판(travelGuide) : 가이드(guide), 여행준비(preparation)
    //  - 축제게시판(festivalBoard) : 축제행사(festival), 공연전시(performance)
    // 유저 게시판 :
    //  - 유저게시판(userBoard) : 여행정보(information), 여행일정(schedule), 후기(review)

//    @GetMapping("/category")
//    public @ResponseBody ResponseEntity<Map<String, List<Board>>> getBoardsByCategory() {
//        Map<String, List<Board>> boardsByCategory = boardService.mainList2();
//        return ResponseEntity.ok(boardsByCategory);
//    }

    @GetMapping("/main/list")
    public @ResponseBody ResponseEntity<List<BoardListAllDTO>> mainList(PageRequestDTO pageRequestDTO){

        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);
//        log.info("=> "+responseDTO);

//        List<String> day = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
//        model.addAttribute("day", day);

        return ResponseEntity.ok(responseDTO.getDtoList());

    } // end get list

    // 게시글 목록 조회 및 검색
    @GetMapping("/{menu}/list")
    public String list(@PathVariable("menu") String menu,
                       PageRequestDTO pageRequestDTO,
                       Model model){
        // PageRequestDTO 객체 생성만 했을 경우 기본값 설정

        log.info("==> menu: "+menu);

        // 1-1. 게시글 댓글 개수 없는 List 조회
//        PageResponseDTO responseDTO = boardService.list(pageRequestDTO);

        // 1-2. 게시글 댓글 개수 있는 List 조회
//        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);
//        log.info("=> "+responseDTO);

        // 1-3. 게시글 댓글 개수 있는 List 조회
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);
//        log.info("=> "+responseDTO);

        model.addAttribute("responseDTO", responseDTO);

//        List<String> day = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
//        model.addAttribute("day", day);

        return "boards/"+menu+"/list";

    } // end get list

    // 게시글 조회
    @GetMapping("/{menu}/read")
    public String read(@PathVariable("menu") String menu,
                       Long id,
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


//        Member member = memberRepository.findByUserIdWithImg(boardDTO.getWriterId()).orElseThrow();
//        replyDTO.setReplierProfileImageName(member.getProfileImage().getFileName());
//        @NotEmpty
//        private String replierProfileImageName;

        /*
        반환값을 void로 할 경우
        return 생략하면 "board/read" 형태으로 자동 포워딩  (return "board/read";)
        */
//        return "boards/userBoard/read";

//        List<String> day = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
//        model.addAttribute("day", day);

        return "boards/"+menu+"/read";

    } // end get read

    // 게시글 등록
    @PreAuthorize("isAuthenticated() and (#menu=='userBoard' or hasRole('ROLE_ADMIN'))")
    @GetMapping("/{menu}/create")
    public String registerGet(@PathVariable("menu") String menu,
                              PageRequestDTO pageRequestDTO) {

        return "boards/"+menu+"/create";

    } // end get create

    @PreAuthorize("isAuthenticated() and (#menu=='userBoard' or hasRole('ROLE_ADMIN'))")
    @PostMapping("/{menu}/create")
    // BoardDTO는 메서드가 호출 받았을 때 넘겨받은 파라미터 값이 BoardDTO의 필드명과 일치하면 자동 매핑 (일치하는 값만 불러옴)
    public String registerPost(@PathVariable("menu") String menu,
                               @Valid BoardDTO boardDTO,  // @Valid 넘어온 데이터 BoardDTO의 에러 유무 체크
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
            return "redirect:/board/"+menu+"/create";
        }

        log.info("==> "+boardDTO);
        // 게시글 등록 서비스 호출(DB에 저장)
        Long id = boardService.register(boardDTO);

        redirectAttributes.addFlashAttribute("id",id);
        redirectAttributes.addFlashAttribute("result", "created");

        return "redirect:/board/"+menu+"/list?"+link;

    } // end post create

    @PreAuthorize("isAuthenticated() and #writerId==principal.username")
    @GetMapping("/{menu}/modify")  // 두개이상 사용시 {}안에 ,쓰고 하나 더 입력
    public String modifyget(@PathVariable("menu") String menu,
                          Long id, String writerId,
                          PageRequestDTO pageRequestDTO,
                          Model model) {

        // 게시글 조회 서비스 요청
        BoardDTO boardDTO = boardService.readOne(id);
        model.addAttribute("dto",boardDTO);

        return "boards/"+menu+"/modify";

    } // end get modify

    // 4. 게시글 수정
    @PreAuthorize("isAuthenticated() and #boardDTO.writerId==principal.username")
    @PostMapping("/{menu}/modify")
    // BoardDTO는 메서드가 호출 받았을 때 넘겨받은 파라미터 값이 BoardDTO의 필드명과 일치하면 자동 매핑 (일치하는 값만 불러옴)
    public String modifypost(@PathVariable("menu") String menu,
                             BoardDTO boardDTO,  // @Valid 넘어온 데이터 BoardDTO의 에러 유무 체크
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
            return "redirect:/board/"+menu+"/modify?"+link;
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

        return "redirect:/board/"+menu+"/read?"+link;
//        return "redirect:/board/read";

    } // end post modify

    // 5. 게시글 삭제
    @PreAuthorize("isAuthenticated() and #writerId==principal.username")
    @PostMapping("/{menu}/remove")
    // BoardDTO는 메서드가 호출 받았을 때 넘겨받은 파라미터 값이 BoardDTO의 필드명과 일치하면 자동 매핑 (일치하는 값만 불러옴)
    public String remove(@PathVariable("menu") String menu,
                         Long id, String writerId,
                         PageRequestDTO pageRequestDTO,
                         RedirectAttributes redirectAttributes){

        // 수정 페이지에서 넘겨받은 페이징 정보
        String link = pageRequestDTO.getLink();

        // 게시글 삭제 서비스 요청
        boardService.remove(id);

        // addFlashAttribute() => 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
        redirectAttributes.addFlashAttribute("id",id);
        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/"+menu+"/list?"+link;

    } // end remove

} // end class
