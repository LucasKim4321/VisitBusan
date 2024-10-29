package com.project.VisitBusan.service;

import com.project.VisitBusan.dto.ProfileImageDTO;
import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.entity.ProfileImage;
import com.project.VisitBusan.repository.MemberRepository;
import com.project.VisitBusan.repository.ProfileImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ProfileImageServiceImpl implements ProfileImageService {
    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;

    @Value("${com.project.VisitBusan.path}")  // properties파일 설정한 path 값 읽어오기
    private String uploadPath;  // => "c:/javaStudy/upload 인식

    @Override
    public void findImage(ProfileImageDTO profileImageDTO, String userId) {
        Member member = memberRepository.findByUserIdWithImg(userId)
                .orElseThrow(() -> new UsernameNotFoundException("계정이 존재하지 않습니다."));

        if(member.getProfileImage() == null) {
            profileImageDTO.setFileName("profile_img.jpg");
        } else {
            profileImageDTO.setFileName(member.getProfileImage().getFileName());
        }
    }

    //이미지 수정
    @Override
    public void upload(ProfileImageDTO profileImageDTO) {
        //1. 수정이미지 존재 확인
        String fileName = profileImageDTO.getFileName();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        // 현재 로그인한 사용자 ID 가져오기
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByUserIdWithImg(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("계정이 존재하지 않습니다."));

        //3. 기존 이미지 조회
        ProfileImage originalImage = profileImageRepository.findByMember(member)
                .orElse(null);

        //기존 이미지 삭제
        if(originalImage != null) {
            profileImageRepository.delete(originalImage); //기존 이미지 삭제
            profileImageRepository.flush(); //반영

            // 파일 시스템에서 이미지 파일 삭제
            String oldFilePath = uploadPath + "/" + originalImage.getFileName();
            deleteFileFromSystem(oldFilePath);  // 파일 시스템에서 이미지 삭제

            log.info("====>  profileImageRepository : " + profileImageRepository);
            log.info("====>  originalImage : " + originalImage);
        }

        //새 이미지 저장
        ProfileImage newProfileImage = new ProfileImage();
        newProfileImage.setFileName(fileName);
        newProfileImage.setMember(member);

        if(profileImageDTO.getId() != null) {
            newProfileImage.setId(profileImageDTO.getId());
        }

        //저장
        profileImageRepository.save(newProfileImage);
    }

    private void deleteFileFromSystem(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);  // 파일이 존재하면 삭제
            log.info("====> 파일 삭제 성공 : " + filePath);
        } catch (IOException e) {
            log.error("파일 삭제 실패: " + filePath, e);
        }
    }

}
