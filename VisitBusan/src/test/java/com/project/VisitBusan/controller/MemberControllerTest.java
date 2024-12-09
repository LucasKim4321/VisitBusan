//package com.project.VisitBusan.controller;
//
//import com.project.VisitBusan.dto.MemberDTO;
//import com.project.VisitBusan.entity.Member;
//import com.project.VisitBusan.service.MemberService;
//import jakarta.validation.constraints.NotBlank;
//import lombok.extern.log4j.Log4j2;
//import org.hibernate.validator.constraints.Length;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
//import org.springframework.test.annotation.Commit;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//// static import ( MockMvc객체)
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Log4j2
//@SpringBootTest
//@Transactional
//@Commit //DB 반영시
//@AutoConfigureMockMvc
//@TestPropertySource(locations = {"classpath:application-test.properties"})
//class MemberControllerTest {
//
//    @Autowired
//    private MemberService memberService;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public Member createMember(String userId, String password) {
////        String encodedPassword = passwordEncoder.encode(password); // 비밀번호 암호화
//
//        MemberDTO memberDTO = MemberDTO.builder()
//                .userId(userId)
//                .name("홍길동")
//                .email("test1002@gmail.com")
//                .password(password)
////                .password(encodedPassword) 테스트의 경우 인코딩이 안되어지게끔 설정
//                .address("부산시")
//                .build();
//
//        return memberService.saveMember(memberDTO);
//    }
//
//    @Test
//    @DisplayName("로그인 성공 테스트")
//    public void loginSuccessTest() throws Exception {
//        String userId = "userId";
//        String password = "1234";
//
//        // 데이터 저장
//        this.createMember(userId, password);
//        log.info("===> createMember");
//        mockMvc.perform(
//                        formLogin()
//                                .loginProcessingUrl("/login") // 로그인 처리 URL
//                                .userParameter("userId") // 폼에서 사용한 사용자 이름 파라미터
//                                .user("userId") //로그인 id
//                                .password(password) // 로그인 pw
//                ) // 로그인 요청
//                .andDo(print()) // 출력
//                .andExpect(SecurityMockMvcResultMatchers.authenticated());
//                // 로그인이 성공하여 인증되었다면 테스트 코드 통과
//    }
//
////    @Test
////    @DisplayName("로그인 실패 테스트")
////    public void loginFailTest() throws  Exception{
////        String email = "test2000@gmail.com";
////        String password = "1234";
////
////        // table에 data 저장
////        this.createMember(email, password);
////
////        mockMvc.perform(
////                        formLogin()
////                                .userParameter("email")
////                                .loginProcessingUrl("/login")
////                                .user(email)
////                                .password("12345"))
////                .andDo(print()) // 출력
////                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());// 로그인이 성공하여 인증되지 않은 결과 값이 출력
////    }
//
//
//}