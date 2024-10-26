package com.project.VisitBusan.service;

import com.project.VisitBusan.constant.Role;
import com.project.VisitBusan.dto.MemberDTO;
import com.project.VisitBusan.dto.PageRequestDTO;
import com.project.VisitBusan.dto.PageResponseDTO;
import com.project.VisitBusan.dto.ProfileImageDTO;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.entity.ProfileImage;
import com.project.VisitBusan.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface MemberService {
    //사용자 추가
    Member saveMember(MemberDTO memberDTO);
    //Member saveMember(MemberDTO memberDTO, ProfileImageDTO profileImageDTO);
    //회원중복체크
    void validateDuplicateMember(String userId, String email);
    //로그인
    Member login(String userId, String password);
    //회원 조회
    MemberDTO findMember(String userId);
    //전체 조회
    List<MemberDTO> findAll();
    //전체 조회2
    PageResponseDTO<MemberDTO> findAll(PageRequestDTO pageRequestDTO);
    //회원 수정
    Member modify(MemberDTO memberDTO);
    //회원 삭제
    void remove(String userId);

    // dto -> entity 변환
    default Member dtoToEntity(MemberDTO memberDTO,
                               PasswordEncoder passwordEncoder) {
        Member member = Member.builder()
                .userId(memberDTO.getUserId())
                .name(memberDTO.getName())
                .email(memberDTO.getEmail())
                .address(memberDTO.getAddress())
                .profileText(memberDTO.getProfileText())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .build();

        //권한 설정시
        member.addRole(Role.USER);

        //프로필 이미지 처리
        if (memberDTO.getProfileImage() != null){
            ProfileImage profileImage = ProfileImageDTO.toEntity(memberDTO.getProfileImage());

            member.setProfileImage(profileImage);
        }

        return member;
    }

    //entity -> dto 변환 (조회용)
    default MemberDTO entityToDto(Member member) {
        MemberDTO memberDTO = MemberDTO.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .address(member.getAddress())
                .profileText(member.getProfileText())
                .build();

        // 프로필 이미지 처리
        if (member.getProfileImage() != null) {
            ProfileImageDTO profileImageDTO = ProfileImageDTO.toProfileImageDTO(member.getProfileImage());
            memberDTO.setProfileImage(profileImageDTO);
        }
        return memberDTO;
    }
}
