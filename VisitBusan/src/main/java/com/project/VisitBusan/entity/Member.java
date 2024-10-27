package com.project.VisitBusan.entity;

import com.project.VisitBusan.constant.Role;
import com.project.VisitBusan.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="member")
@Getter@Setter
@ToString//(exclude = "roleSet")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member extends BaseEntity {

    //------------------ 기본필드 ----------------------//

    @Id  // 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)  //  중복 허용 x
    private String email;
    private String address;
    @Column(length = 100)
    private String profileText; //프로필 자기소개
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "profile_image_id") //프로필 이미지 ID 연결
    private ProfileImage profileImage;

    //------------------ 사용자 권한 ----------------------//

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Role> roleSet = new HashSet<>(); //사용자 권한

    // 역할 추가
    public void addRole(Role role) {
        this.roleSet.add(role);
    }
    // 모든 역할 제거
    public void clearRoles() {
        this.roleSet.clear();
    }

    public String getRoleKey() {
        return this.roleSet.toString();
    }

    //------------------ 변경메서드 ----------------------//

    //데이터 수정하는 메서드
    public void change(String name,
                       String email,
                       String address,
                       Set<Role> roleSet,
                       String profileText
                        ) {

        if(name!= null && !name.isEmpty()) {
            this.name = name; //닉네임
        }
        if(email!= null && !email.isEmpty()) {
            this.email = email; //이메일
        }
        if(address!= null && !address.isEmpty()) {
            this.address = address; //주소
        }
        if(roleSet!= null && !(roleSet.contains(Role.ROOT))) {
            this.roleSet = roleSet; //권한
        }
        if(profileText!= null && !profileText.isEmpty()) {
            this.profileText = profileText; //프로필 자기소개
        }
    }

    //------------------ 정적 팩토리 ----------------------//

    // 1.엔티티 메서드 방법 : createMember():  dto -> entity
    public static Member createMember(MemberDTO memberDTO,
                                      PasswordEncoder passwordEncoder){
        Member member = new Member();

        member.setUserId(memberDTO.getUserId());
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        member.setAddress(memberDTO.getAddress());

        // 비밀번호 -> 암호화 작업
        String password = passwordEncoder.encode(memberDTO.getPassword());
        member.setPassword(password);

        //권한 설정
        if (memberDTO.getRoleSet() != null) {
//            memberDTO.getRoleSet().forEach(role->member.addRole(role));
            memberDTO.getRoleSet().forEach(member::addRole);
        }
        else {
            // member.setRole(Role.USER); // Set<Role> 사용전
            member.addRole(Role.USER);    // Set<Role> 사용후
        }



        return member;
    }


    //------------------ 프로필 이미지 ----------------------//

    //프로필 이미지 설정
    public void setProfileImage(ProfileImage profileImage) {
        //이미지가 이미 있을경우 null 처리
        if(this.profileImage != null) {
            this.profileImage.setMember(null);
        }

        //새로운 이미지 업로드
        this.profileImage = profileImage;
        if (profileImage != null) {
            profileImage.setMember(this);
        }
    }


    // 삭제 처리 기능
    public void clearProfileImage() {
        if(this.profileImage != null) {
            //양방향이기에 연관관계를 해제 후 null 설정
            this.profileImage.setMember(null); //연결되어있는 member 없앰
            this.profileImage = null; //이미지 상태도 null 설정
        }
    }

}