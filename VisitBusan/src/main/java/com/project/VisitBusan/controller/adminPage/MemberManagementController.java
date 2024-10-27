package com.project.VisitBusan.controller.adminPage;
import com.project.VisitBusan.dto.MemberDTO;
import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.exception.DuplicateEmailException;
import com.project.VisitBusan.exception.DuplicateUserIdException;
import com.project.VisitBusan.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/member")
public class MemberManagementController {

    private final MemberService memberService;

    //회원 정보 조회
    @GetMapping("/list")
    public String memberPage(PageRequestDTO pageRequestDTO,
                             Model model) {

//        List<MemberDTO> memberDTOList = memberService.findAll();
//        log.info("=> memberDTOList: "+memberDTOList);
//        model.addAttribute("memberDTOList", memberDTOList);

        PageResponseDTO<MemberDTO> responseDTO = memberService.findAll(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);

        return "adminPage/member/list";
    }

    @GetMapping("/read/{userId}")
    public @ResponseBody ResponseEntity<MemberDTO> readMember(@PathVariable String userId) {
        MemberDTO member = memberService.findMember(userId);

        if (member == null) {
            return ResponseEntity.notFound().build();
        }

        log.info("===> userId : " + userId);

        return ResponseEntity.ok(member);
//        return (ResponseEntity<MemberDTO>) ResponseEntity.notFound();
    }

    @PostMapping(value = "/create")
    public @ResponseBody ResponseEntity<?> memberRegister(@Valid @RequestBody MemberDTO memberDTO,
                                 Model model) {

        log.info("=> memberDTO: " + memberDTO);

        try {
            // dto -> entity -> email중복 체크 ->  save
            memberService.saveMember(memberDTO);
        } catch (Exception e) { // 중복된 이메일 등록시 예외발생되는 Exception 처리
            model.addAttribute("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
        }
        return ResponseEntity.ok("Deleted");
    }

    // 중복 체크 처리
    @ResponseBody
    @PostMapping("/check-existing")
    public ResponseEntity<Map<String, Object>> checkExisting(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String userId = requestData.get("userId");
        String email = requestData.get("email");

        try {
            // ID와 이메일을 가진 임시 Member 객체 생성
            Member tempMember = new Member();
            tempMember.setUserId(userId);
            tempMember.setEmail(email);

            // validateDuplicateMember 메서드를 호출하여 중복 검사
            memberService.validateDuplicateMember(userId, email);

            // 중복이 없는 경우
            response.put("exists", false);
        } catch (DuplicateUserIdException e) {
            // ID가 중복된 경우
            response.put("exists", true);
            response.put("type", "userId");
        } catch (DuplicateEmailException e) {
            // 이메일이 중복된 경우
            response.put("exists", true);
            response.put("type", "email");
        } catch (Exception e) {
            // 다른 예외가 발생한 경우
            response.put("exists", false);
            response.put("error", "서버에서 오류가 발생했습니다.");
        }

        //결과 반환
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/modify") //데이터 전송
    public @ResponseBody ResponseEntity<?> updateMember(@Valid @RequestBody MemberDTO memberDTO, //valid 유효성 검사
//                                       public String updateMember(@Valid @RequestBody MemberDTO memberDTO, //valid 유효성 검사
                                          BindingResult bindingResult, //유효성 검사 후 데이터 담는 객체
                                          RedirectAttributes redirectAttributes) { //일회성 데이터 전달
        System.out.println("수정 요청 받음: " + memberDTO);

        // 유효성 검사 오류 처리
       /* if (bindingResult.hasErrors()) {
            // 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호를 입력해주세요.");
            return ResponseEntity.ok("SU");
        }*/

        //회원정보 수정
        memberService.modify(memberDTO);
        Member updatedMember = memberService.modify(memberDTO);
        System.out.println("수정된 회원 정보: " + updatedMember);

        redirectAttributes.addFlashAttribute("message", "회원정보가 수정되었습니다.");

        return ResponseEntity.ok("Modified");
    }

    //4. 회원정보 삭제
//    @PreAuthorize("isAuthenticated") //로그인 인증 완료
    @PostMapping(value = "/delete")
    public @ResponseBody ResponseEntity<?> removeMember(@RequestParam("userId") String userId, RedirectAttributes redirectAttributes) {

        try {
            memberService.remove(userId);
            redirectAttributes.addFlashAttribute("result","deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 삭제를 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
        }
        return ResponseEntity.ok("Deleted");
    }

}
