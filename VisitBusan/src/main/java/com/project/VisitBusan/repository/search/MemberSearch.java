package com.project.VisitBusan.repository.search;

import com.project.VisitBusan.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberSearch {

    Page<Member> search(Pageable pageable);

    // 멤버 조건 검색 조회
    Page<Member> searchAll (String[] types, String keyword, Pageable pageable);

}
