package com.project.VisitBusan.controller;

import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Log4j2
@RequestMapping("/roletest")
@RequiredArgsConstructor
public class RoleTestController {

    public final MemberRepository memberRepository;

    // ------------------------------ //
    //
    // 메서드 단위로 권한 설정 여부 테스트
    //
    // CustomSecurityConfig 클래스
    // @EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
    // 어노테이션 선언
    //
    // ------------------------------ //

    // 유저 권한만 허용
    // 권한 부족시 로그인 페이지 뜸
//    @Secured(value="USER")  // user객체에 authorities값이 제대로 안들어갔을 경우(ROLE_미포함시) 이거만 됨
    @Secured(value="ROLE_USER")  // @Secured() : 한개의 권한만 가능
//    @PreAuthorize("hasRole('USER')")
//    @PostAuthorize("isAuthenticated() and hasRole('ROLE_USER')")  // isAuthenticated() 동록된 회원일 시
    @GetMapping("/user")
    public @ResponseBody String userInfo() {  // @ResponseBody body에 리턴값 출력
        return "유저만 접근 가능한 페이지입니다~";
    }

    // 관리자 권한만 허용
//    @Secured(value="ROLE_ADMIN")
//    @PreAuthorize("hasRole('ADMIN')")  // @Secured() : 한개의 권한만 가능
    @PostAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public @ResponseBody String adminInfo() {  // @ResponseBody body에 리턴값 출력
        return "관리자만 접근 가능한 페이지입니다~";
    }

    // 최고관리자 권한만 허용
//    @Secured(value="ROLE_ADMIN")
//    @PreAuthorize("hasRole('ADMIN')")  // @Secured() : 한개의 권한만 가능
    @PostAuthorize("isAuthenticated() and hasRole('ROLE_ROOT')")
    @GetMapping("/root")
    public @ResponseBody String rootInfo() {  // @ResponseBody body에 리턴값 출력
        return "최고관리자만 접근 가능한 페이지입니다~";
    }

    // 로그인 상태이면서 관리자 또는 유저 권한만 허용
//    @PreAuthorize("hasAnyRole()")
    @PostAuthorize("isAuthenticated() and ( hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') )")
    @GetMapping("/adminuser")
    public @ResponseBody String userAndAdminInfo() {  // @ResponseBody body에 리턴값 출력
        return "관리자와 유저 둘 다 접근 가능한 페이지입니다~";
    }

//    // 로그인 상태이면서 USER 권한을 가지고 있거나 특정 유저인 경우
//    @PostAuthorize("isAuthenticated() and ( hasRole('ROLE_USER') or returnObject.name==principal.name )")
//    @GetMapping("/selectOne/{userId}")
//    public @ResponseBody Member selectOneInfo(@PathVariable("userId") String userId) {  // @ResponseBody body에 리턴값 출력
//        // 유저 또는 특정한 유저만 접근 가능한 페이지입니다~
//        return memberRepository.findByUserId(userId);
//    }
//    // 로그인 상태이면서 USER 권한을 가지고 있거나 특정 유저인 경우
//    @PostAuthorize("isAuthenticated() and ( hasRole('ROLE_USER') or returnObject==principal.name )")
//    @GetMapping("/selectOne/{userId}")
//    public @ResponseBody String selectOneInfo2(@PathVariable("userId") String userId) {  // @ResponseBody body에 리턴값 출력
//        // 유저 또는 특정한 유저만 접근 가능한 페이지입니다~
//        return memberRepository.findByUserId(userId).getUserId();
//    }
    // 로그인 상태이면서 USER 권한을 가지고 있거나 특정 유저인 경우
    @PostAuthorize("isAuthenticated() and ( hasRole('ROLE_USER') or #userId==principal.username )")
    @GetMapping("/selectOne/{userId}")
    public @ResponseBody String selectOneInfo3(@PathVariable("userId") String userId) {  // @ResponseBody body에 리턴값 출력
        return "유저 또는 특정한 유저만 접근 가능한 페이지입니다~"+memberRepository.findByUserId(userId);
    }


    @GetMapping("/test1")
    public @ResponseBody String test1 () {
        return "permitAll() 권한 설정입니다~";
    }
    @GetMapping("/test2")
    public @ResponseBody String test2 () {
        return "authenticated() 권한 설정입니다~";
    }
    @GetMapping("/test3")
    public @ResponseBody String test3 () {
        return "hasRole('USER') 권한 설정입니다~";
    }
    @GetMapping("/test4")
    public @ResponseBody String test4 () {
        return "hasRole('ADMIN') 권한 설정입니다~";
    }

}

/*
SecurityConfig.java 파일로 가서 해당 클래스에 @EnableMethodSecurity 애노테이션을 달아준다.
이 애노테이션은 함수에 권한 처리 애노테이션을 붙일 수 있게할지 말지에 관한 설정을 담당한다.

@Secured 애노테이션을 붙여주면, 애노테이션에 인자로 받은 권한이 유저에게 있을 때만 실행하도록 할 수 있다.
@PreAuthorize: 메서드가 실행되기 전에 인증을 거친다.
@PostAuthorize: 메서드가 실행되고 나서 응답을 보내기 전에 인증을 거친다.

hasRole([role]) : 현재 사용자의 권한이 파라미터의 권한과 동일한 경우 true
hasAnyRole([role1,role2 ...]) : 현재 사용자의 권한 파라미터의 권한 중 일치하는 것이 있는 경우 true
principal: 사용자를 증명하는 주요객체(User)를 직접 접근할 수 있다.
authentication : SecurityContext에 있는 authentication 객체에 접근 할 수 있다.
permitAll : 모든 접근 허용
denyAll : 모든 접근 비허용
isAnonymous() : 현재 사용자가 익명(비로그인)인 상태인 경우 true
isRememberMe() : 현재 사용자가 RememberMe 사용자라면 true
isAuthenticated() : 현재 사용자가 익명이 아니라면 (로그인 상태라면) true
isFullyAuthenticated() : 현재 사용자가 익명이거나 RememberMe 사용자가 아니라면 true
 */