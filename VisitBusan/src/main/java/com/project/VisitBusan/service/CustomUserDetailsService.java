package com.project.VisitBusan.service;

import com.project.VisitBusan.dto.AuthMemberDTO;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.repository.MemberRepository;
import groovy.lang.Lazy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;  // 자동 로그인 설정할 때 이거 때문에 에러남

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("==> loadUserByUsername: "+username);

        // 1. 테스트용 더미 User객체 생성하기
//        UserDetails userDetails = User.builder()
//                .username("user1@test.com")
//                .password(passwordEncoder.encode("12345"))  // 반드시 패스워드 인코드 필요
//                .authorities("ROLE_USER")
//                .build();

        // Member Entity(DB)에 있는 정보를 기준으로 Authentication 처리
        Optional<Member> result = memberRepository.findByUserIdWithImg(username);  // 회원 검색
        //예외 발생
//        if (member == null) {  // 미가입 회원일 경우 / Optional 이므로 코드 변경
//            throw new UsernameNotFoundException(username);
//        }
        Member member = result.orElseThrow(() -> new UsernameNotFoundException(username));
        log.info("==> Member info: " + member);
        // 2. security 자체적으로 제공하는 User객체 사용
        // security 자체적으로 제공하는 user객체는 username, password, authorities 값 밖에 저장 못 함

        /*
        // 회원일 경우 로그인 페이지에서 받은 유저 정보로 유저 객체를 생성
        // DB에 있는 회원정보를 User객체를 통해 정보를 전달하여 UserDetails 객체 생성
        UserDetails userDetails = User.builder()
                .username(member.getEmail())
                .password(member.getPassword())  // 암호화 인코드 불필요
                // ROLE_ADMIN, ROLE_USER, ROLE_XXX
                // ROLE을 자동으로 붙여주는 기능을 이용하거나 수동으로 붙여야 권한이 제대로 설정됨.
                .authorities("ROLE_"+member.getRole().toString())
                .build();
        return userDetails;
        */


        // 3. 사용자 정의 User 객체(AuthMemberDTO) 생성해서 사용
        // AuthMemberDTO, MemberService, Member, MemberRepository 참조
        // Db에 있는 회원정보를 User객체로부터 상속받은 AuthMemberDTO 객체에 정보를 저장한 후
        // UserDetails 객체 생성
        // 확인필요 ** 위에 먼가 한거 같은데 모르겠네

        AuthMemberDTO authMemberDTO = new AuthMemberDTO(
                member.getName(),
                member.getEmail(),
                member.getAddress(),
                member.getUserId(),
                member.getPassword(),
                member.getRoleSet()
                        .stream()
                        .map(role->
                                new SimpleGrantedAuthority("ROLE_"+role.name())  // 반드시 대문자로 설정
                        ).collect(Collectors.toList())
        );

        log.info("==> authMemberDTO: "+authMemberDTO);

        return authMemberDTO;

    } // end loadUserByUsername();

}

/*
 * UserDetailsService: DB에서 회원 정보를 가져오는 역할
 * loadUserByUsername(): 회원정보를 조회하여 사용자의 정보와 권한을 갖는 UserDetails 인터페이스 반환
 * UserDetails: 시큐리티에서 회원 정보를 담기위해서 사용되는 인터페이스, 직접구현 or 시큐리티에서 제공하는 User클래스 사용
 *
 */