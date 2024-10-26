package com.project.VisitBusan.dto;

import com.project.VisitBusan.config.CustomSecurityConfig;
import com.project.VisitBusan.constant.Role;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.entity.ProfileImage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor  // 데이터가 없는 상태로 객체 생성함
@Builder
public class MemberDTO {

    private Long id;
    @NotBlank(message = "아이디 입력은 필수입니다.")
    private String userId;
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Length(min=4, max=16, message = "4자 이상 16자 이하로 입력해주세요.")
    private String password;
    @NotBlank(message = "이름은 입력은 필수입니다.")  // Null 체크 및 문자열의 경우 길이 0인지 및 빈문자열("") 검사
    private String name;
    @NotBlank(message = "이메일 입력은 필수입니다.")  // Null 체크 및 문자열의 경우 길이 0인지 및 빈문자열("") 검사
    @Email(message = "이메일 형식에 맞춰 입력해주세요.")
    private String email;
    @NotBlank(message = "주소 입력은 필수입니다.")
    private String address;

    private String profileText;
    private ProfileImageDTO profileImage;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    // 사용자 정의 User 객체(AuthMemberDTO) 생성해서 사용
    // Role data 임시 저장 용
    private Role role;
    private Set<Role> roleSet; //사용자 권한

    public static ModelMapper modelMapper = new ModelMapper();


    public Member toMemberEntity(PasswordEncoder passwordEncoder){
        return Member.builder()
                .userId(userId)
                .name(name)
                .password(passwordEncoder.encode(password))
                .address(address)
                .build();
    }

    public static MemberDTO toMemberDTO(Member member) {
        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);
        if (member.getProfileImage() != null) {
            ProfileImageDTO profileImageDTO = ProfileImageDTO.toProfileImageDTO(member.getProfileImage());
            memberDTO.setProfileImage(profileImageDTO);
        }
        return memberDTO;
    }

    public static Member toEntity(MemberDTO memberDTO) {
        Member member = modelMapper.map(memberDTO, Member.class);
        // 프로필 이미지가 있을 경우 처리
        if (memberDTO.getProfileImage() != null) {
            ProfileImage profileImage = ProfileImageDTO.toEntity(memberDTO.getProfileImage());
            member.setProfileImage(profileImage);
        }
        return member;
    }
}