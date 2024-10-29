package com.project.VisitBusan.service;

import com.project.VisitBusan.constant.Role;
import com.project.VisitBusan.dto.MemberDTO;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Commit
@Transactional(readOnly = false)
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Log4j2
@SpringBootTest
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("Member Entity 생성")
    public void insertMember() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Member member = Member.builder()
                    .userId(i +"userId5")
                    .name("홍길동2" + i)
                    .email("test5" + i + "@email.com5")
                    .password(passwordEncoder.encode("1111"))
                    .address("부산시 진구")
                    .build();

            // 1~100까지는 USER권한 추가
            member.addRole(Role.USER);

            log.info("==> member:" + member);
            Member savedMember = memberRepository.save(member);

        });
        log.info("Test 끝");
    }
    @Test
    @DisplayName("회원 조회 Test")
    public void findMember() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            String userId = i +"userId";
            Optional<Member> memberOptional = memberRepository.findByUserIdWithImg(userId);
            log.info("test start ==> ");
            if (memberOptional.isPresent()) {
                Member member = memberOptional.get();
                //상세 정보 출력
                log.info("==> userId: " + member.getUserId());
                log.info("==> name: " + member.getName());
                log.info("==> email: " + member.getEmail());
                log.info("==> password: " + member.getPassword());
                log.info("==> address: " + member.getAddress());
                log.info("==> profileText: " + member.getProfileText());
            } else {
                log.info("아이디를 찾을수 없습니다. ==> " + userId);
            }

//            member.getRoleSet().forEach(role -> log.info("=> member role :" + role.name()));
        });
    }
    @Test
    @DisplayName("회원 수정 Test")
    public void ModifyMember() {
        //더비 데이터
        Member member = Member.builder()
            .userId("userId5")
            .name("홍길동2")
            .email("test5@email.com5")
            .password(passwordEncoder.encode("1111"))
            .address("부산시 진구")
            .build();

        memberRepository.save(member);

        // 수정할 DTO 객체 생성
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUserId(member.getUserId()); // 수정할 회원의 userId 설정
        memberDTO.setName("홍길동3"); // 수정할 이름 설정
        memberDTO.setEmail("test5_updated@email.com5"); // 수정할 이메일 설정
        memberDTO.setAddress("서울시 강남구"); // 수정할 주소 설정

        // 회원 정보 수정 실행
        Member modMember = memberService.modify(memberDTO);

        Assertions.assertThat(modMember.getName()).isEqualTo(member.getName());
        Assertions.assertThat(modMember.getEmail()).isEqualTo(member.getEmail());
        Assertions.assertThat(modMember.getAddress()).isEqualTo(member.getAddress());
    }
}


