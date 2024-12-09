package com.project.VisitBusan.controller;

import com.project.VisitBusan.dto.MemberDTO;
import com.project.VisitBusan.dto.ProfileImageDTO;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.exception.DuplicateEmailException;
import com.project.VisitBusan.exception.DuplicateUserIdException;
import com.project.VisitBusan.service.MemberService;
import com.project.VisitBusan.service.MemberServiceImpl;
import com.project.VisitBusan.service.ProfileImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {
    private final MemberService memberService;
    private final MemberServiceImpl memberServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageService profileImageService;

    //----------------------- //
    // 회원가입
    //----------------------- //
    // 회원 등록: GET, POST
    @GetMapping(value = "/signup")
    public String memberRegisterForm(Model model) {

    // 데이터가 없는 memberDTO생성 : form에 입력한 데이터와 1:1 맵핑
        model.addAttribute("memberDTO", new MemberDTO());

        // 포워딩: 뷰리졸브
        return "members/signup";
    }

    @PostMapping(value = "/signup")
    public String memberRegister(@Valid @ModelAttribute MemberDTO memberDTO,
                                 Model model) {

        log.info("=> memberDTO: " + memberDTO);

        try {
            // dto -> entity -> email중복 체크 ->  save
            memberService.saveMember(memberDTO);
        } catch (Exception e) { // 중복된 이메일 등록시 예외발생되는 Exception 처리
            model.addAttribute("errorMessage", e.getMessage());
            return "members/signup";// 입력폼으로 포워딩
        }
        //회원가입 후 로그인 페이지 이동
        return "redirect:/login";
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
            memberServiceImpl.validateDuplicateMember(userId, email);

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

    //----------------------- //
    // 로그인, 로그아웃 처리
    //----------------------- //
    // 1. 로그인
    @GetMapping(value = "/login")
    public String loginMember(String error, String logout) {
        log.info("=> login ");
        // 로그인 폼이 있는 페이지로 포워딩
        return "members/login";
    }

    // 1-1. 로그인 실패시 처리할 url
    @GetMapping("/login/error")
    public String loginError(Model model) {
        log.info("==> login error");
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호 확인해주세요.");

        return "members/login";
    }

    //----------------------- //
    // 회원정보 조회
    //----------------------- //

    /*1. 회원 목록 조회*/
//    @PreAuthorize("isAuthenticated") //로그인 인증 완료
//    @GetMapping(value = "/mypage")
//    public String findAll(Model model) {
//        List<MemberDTO> memberDTOList = memberService.findAll();
//        model.addAttribute("memberList", memberDTOList);
//        log.info("회원목록 조회 ==> " + memberDTOList);
//
//        return "members/myPage";
//    }

    /*2. 마이페이지 조회*/
    @PreAuthorize("isAuthenticated") //로그인 인증 완료
    @GetMapping(value = "/mypage")
    public String memberMyPageForm(Model model) {
        //로그인한 사용자 ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //회원 정보 조회
        MemberDTO memberDTO = memberService.findMember(userId);

        //프로필 이미지 조회
        ProfileImageDTO profileImageDTO = new ProfileImageDTO();
        profileImageService.findImage(profileImageDTO, userId);

        model.addAttribute("member", memberDTO);
        model.addAttribute("profileImage", profileImageDTO);
        model.addAttribute("fileName", profileImageDTO.getFileName());
        log.info("profileImageDTO ==> " + profileImageDTO);

        return "members/myPage";
    }


    @PostMapping(value = "/mypage/check") //데이터 전송
    public ResponseEntity<String> updateMemberCheck(@Valid @ModelAttribute MemberDTO memberDTO,
                                                    BindingResult bindingResult){

        String errorMessage = "";
        String password = memberDTO.getPassword();

        // 유효성 검사 오류 처리
        if (password.isEmpty()) {
            errorMessage ="비밀번호를 입력해주세요.";
        } else {
            // 멤버 조회
            MemberDTO member = memberService.findMember(memberDTO.getUserId());

            // 비밀번호 확인
            if(!passwordEncoder.matches(memberDTO.getPassword(), member.getPassword())) {
                errorMessage ="비밀번호가 일치하지 않습니다.";
            } else if (bindingResult.hasErrors()) {

                //errorMessage = bindingResult.toString();
                errorMessage = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(", "));
            }

        }

        log.info("errorMessage ==========>"+errorMessage);

        return ResponseEntity.ok().body(errorMessage);

    }


    /*3. 회원정보 수정*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @PreAuthorize("isAuthenticated") //로그인 인증 완료
    @PostMapping(value = "/mypage/modify") //데이터 전송
    public String updateMember(@Valid @ModelAttribute MemberDTO memberDTO, //valid 유효성 검사
                               BindingResult bindingResult, //유효성 검사 후 데이터 담는 객체
                               RedirectAttributes redirectAttributes) { //일회성 데이터 전달

        //String showswich = "1";

        // 유효성 검사 오류 처리
        if (bindingResult.hasErrors()) {

            // 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호를 입력해주세요.");
            return "redirect:/mypage"; // 유효성 검사 오류 시 다시 마이페이지로 이동
        }
        /*
        // 멤버 조회
        MemberDTO member = memberService.findMember(memberDTO.getUserId());

        //비밀번호 확인
        if(!passwordEncoder.matches(memberDTO.getPassword(), member.getPassword())) {
            // 비밀번호 불일치 예외 처리
            // 1회용 정보유지 : redirect방식으로 요청시 정보관리하는 객체
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            redirectAttributes.addFlashAttribute("showswich", showswich);
            return "redirect:/mypage";
        }
        */

        //회원정보 수정
        memberService.modify(memberDTO);
        redirectAttributes.addFlashAttribute("message", "회원정보가 수정되었습니다.");

        return "redirect:/mypage";
    }

    /*4. 회원정보 삭제*/
    @PreAuthorize("isAuthenticated") //로그인 인증 완료
    @PostMapping(value = "/mypage/delete")
    public String removeMember(@ModelAttribute MemberDTO memberDTO,
                               RedirectAttributes redirectAttributes) {

        //해당 userId 회원 정보 들고오기
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            memberService.remove(userId);
            redirectAttributes.addFlashAttribute("result","deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 삭제를 실패하였습니다.");
            return "redirect:/mypage";
        }
        return "redirect:/logout";
    }
}