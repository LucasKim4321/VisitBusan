package com.project.VisitBusan.repository;

import com.project.VisitBusan.entity.Member;
import com.project.VisitBusan.repository.search.MemberSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberSearch {

    Member findByEmail(String email);

    @EntityGraph(attributePaths = "profileImage")
    @Query("select b from Member b where b.userId = :userId")
    Optional<Member> findByUserId(@Param("userId") String userId);

}



/*

JpaRepository에서 지원하는 메서드

<S extends T> save(S entity)    : 엔티티 저장 및 수정
void delete(t entity)           : 엔티티 삭제
count()                         : 엔티티 총 개수 반환
Iterable<T> finaAll(0)          : 모든 엔티티 조회

 */