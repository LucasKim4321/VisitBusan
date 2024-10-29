package com.project.VisitBusan.service;

import com.project.VisitBusan.config.CustomSecurityConfig;
import com.project.VisitBusan.dto.*;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.entity.ProfileImage;
import com.project.VisitBusan.exception.DuplicateEmailException;
import com.project.VisitBusan.exception.DuplicateUserIdException;
import com.project.VisitBusan.repository.MemberRepository;
import com.project.VisitBusan.repository.ProfileImageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageRepository profileImageRepository;

    //사용자 추가
    @Override
    public Member saveMember(MemberDTO memberDTO){

        // 1. dto -> entity: Member Entity createMember() 메서드 활용
        Member member = Member.createMember(memberDTO, passwordEncoder);

        // 2. 회원 중복 체크
        validateDuplicateMember(member.getUserId(), member.getEmail());
        System.out.println("===> member" + member);

        // 4. 엔티티에 프로필 이미지 설정
        ProfileImage profileImage = ProfileImage.builder()
                .member(member)
                .fileName("profile_img.jpg")
                .build();
        profileImageRepository.save(profileImage);

        // 중복된 이메일 없을 경우 저장(반영)
        return memberRepository.save(member);
    }


    @Override
    public void validateDuplicateMember(String userId, String email) {
        // 이메일 체크
        if (memberRepository.findByEmail(email) != null) {
            log.info("이미 가입된 이메일입니다: " + email);
            throw new DuplicateEmailException("이미 가입된 이메일입니다.");
        }

        // ID 중복 체크
        if (memberRepository.findByUserIdWithImg(userId).isPresent()) {
            log.info("이미 가입된 ID입니다: " + userId);
            throw new DuplicateUserIdException("이미 사용 중인 ID입니다.");
        }
    }

    //로그인
    @Override
    public Member login(String userId, String password) {
        Optional<Member> optionalMember = memberRepository.findByUserIdWithImg(userId);

        if(optionalMember.isPresent()) {
            //작성한 ID와 PW 일치시 로그인
            Member member = optionalMember.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                return member;
            } else {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID 입니다.");
        }
    }

    //회원 조회
    @Override
    public MemberDTO findMember(String userId) {
        log.info("회원 조회 요청: userId = " + userId);

        Optional<Member> memberOptional = memberRepository.findByUserIdWithImg(userId);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            log.info("조회된 회원: " + member);
            return MemberDTO.toMemberDTO(member);
        } else {
            log.info("회원이 존재하지 않습니다.");
            throw new IllegalArgumentException("해당 회원이 없습니다.");
        }

//        Member member = memberRepository.findByUserIdWithImg(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));
//        return MemberDTO.toMemberDTO(member);
    }

    //회원 수정
    @Override
    public Member modify(MemberDTO memberDTO) {
        log.info("멤버 정보: {}", memberDTO);
        System.out.println("회원 ID: " + memberDTO.getUserId());
        //회원 조회
        Member member = memberRepository.findByUserIdWithImg(memberDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        //비밀번호 검증
        /*if (memberDTO.getPassword() != null && !passwordEncoder.matches(memberDTO.getPassword(), member.getPassword())) {
            // 비밀번호가 맞지 않을시 예외 발생
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        } else if (memberDTO.getPassword() == null){
            // 비밀번호가 적혀있지 않을시 예외 발생
            throw new IllegalArgumentException("비밀번호 작성이 필요합니다.");
        }*/

    // 회원정보 변경
        member.change(
                memberDTO.getName(),
                memberDTO.getEmail(),
                memberDTO.getAddress(),
                memberDTO.getRoleSet(),
                memberDTO.getProfileText() // 추가 정보가 필요한 경우 포함
        );
        System.out.println("======================");
        System.out.println(member);
        System.out.println("======================");
        //저장하기
//        member.setName(memberDTO.getName());
        return memberRepository.save(member);
    }

    //회원 삭제
    @Override
    public void remove(String userId) {
        Member member = memberRepository.findByUserIdWithImg(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        //회원정보만 삭제
        memberRepository.delete(member);
    }

    //전체 조회
    @Override
    public List<MemberDTO> findAll() {
        List<Member> memberList = memberRepository.findAll(); //member 조회
        List<MemberDTO> memberDTOList = new ArrayList<>();  //memberDTO 저장리스트 생성

        for (Member member:memberList) { //엔티티가 DTO로 변환
            MemberDTO memberDTO = MemberDTO.toMemberDTO(member);
            memberDTOList.add(memberDTO);
        }

        return memberDTOList;
    }

    //전체 조회2
    @Override
    public PageResponseDTO<MemberDTO> findAll(PageRequestDTO pageRequestDTO) {

        // 검색 조건에 대한 처리
        String[] types = pageRequestDTO.getTypes();  // 검색 타입(글제목, 글내용, 작성자)
        String keyword = pageRequestDTO.getKeyword(); // 검색 키워드
        Pageable pageable = pageRequestDTO.getPageable("id");

        // 조건 검색 및 페이징한 결과값 가져오기
        Page<Member> result = memberRepository.searchAll(types, keyword, pageable);

        // page객체에 있는 내용을 List구조 가져오기
        List<MemberDTO> dtoList = result.getContent().stream()
                // collection 구조에 있는 entity를 하나씩 dto로 변환하여 List구조에 저장
                .map(member-> modelMapper.map(member,MemberDTO.class))
                .collect(Collectors.toList());

        // 매개변수로 전달받은 객체(pageRequestDTO)를 가지고 PageResponseDTO.Builder()를 통해
        // PageRequestDTO객체 생성되어 필요시 스프링이 필요시점에 주입 시켜줌(list에서 pageRequestDTO객체 사용가능함 )
        return PageResponseDTO.<MemberDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList) // Projection.bean(): JPQL의 결과를 바로 DTO로 처리한 결과를 입력 **
                .total((int)result.getTotalElements())
                .build();
    }
}
