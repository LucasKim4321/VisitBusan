package com.project.VisitBusan.repository;

import com.project.VisitBusan.entity.Board;
import com.project.VisitBusan.repository.search.BoardSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    // Board와 BoardFile 연관관계 : @OneToMany 설정시 => BoardFile 지연 로딩 상태
    // @OneToMany: 기본적으로 지연로딩을 이용: fetch = FetchType.LAZY
    // @EntityGraph: LAZY설정(지연로딩)일지라도 한번에 조인 처리해서 select처리하도록 설정(boardFileSet이 불려오면 읽어짐)

    @EntityGraph(attributePaths = {"boardFileSet"})
    @Query("select b from Board b where b.id = :id")
    Optional<Board> findByIdWithFiles(Long id);

    // 카테고리별로 그룹화된 게시물 리스트를 JPQL로 가져오기 (카테고리 기준 정렬)
    @Query("SELECT b FROM Board b ORDER BY b.category, b.id")
    List<Board> findAllGroupedByCategory();

//    @Query("SELECT b FROM Board b WHERE b.category = :category")
//    List<Board> findTop5ByCategory(@Param("category") String category, Pageable pageable);

    List<Board> findAll();
}

/*
 * JpaRepository 기본 기능 (by ChatGPT)
 *
 * 메서드 이름	설명
 * save(S entity)	주어진 엔티티를 저장하거나 업데이트합니다.
 * saveAll(Iterable<S> entities)	주어진 엔티티 목록을 모두 저장하거나 업데이트합니다.
 * findById(ID id)	주어진 ID에 해당하는 엔티티를 반환합니다. (Optional 타입으로 반환)
 * existsById(ID id)	주어진 ID에 해당하는 엔티티가 존재하는지 확인합니다.
 * findAll()	모든 엔티티를 반환합니다.
 * findAllById(Iterable<ID> ids)	주어진 ID 목록에 해당하는 모든 엔티티를 반환합니다.
 * count()	전체 엔티티의 수를 반환합니다.
 * deleteById(ID id)	주어진 ID에 해당하는 엔티티를 삭제합니다.
 * delete(T entity)	주어진 엔티티를 삭제합니다.
 * deleteAllById(Iterable<? extends ID> ids)	주어진 ID 목록에 해당하는 모든 엔티티를 삭제합니다.
 * deleteAll(Iterable<? extends T> entities)	주어진 엔티티 목록을 모두 삭제합니다.
 * deleteAll()	모든 엔티티를 삭제합니다.
 *
 * 기본기능 외엔 만들어야함.
 * 1. 규칙 적용해서 쉽게 만들기 (만들면 자동 인식)
 * 2. query 지정해서 만들기 (@Query 사용)
 *
 */

/*
 * JpaRepository 지원하는 메서드 (기본적으로 들어있는 기능)
 * <S extends T> save(S entity) : 저장 및 수정
 * void delete(t entity) : 삭제
 * count() : 총 개수 반환
 * Iterable<T> findAll() : 모든 엔티티 조회
 *
 * find+(엔티티이름) +By + 변수(필드)명
 *
 *
 */

// JPA Query Method
// Distinct	findDistinctByLastnameAndFirstname	select distinct …​ where x.lastname = ?1 and x.firstname = ?2
// And	findByLastnameAndFirstname	… where x.lastname = ?1 and x.firstname = ?2
// Or	findByLastnameOrFirstname	… where x.lastname = ?1 or x.firstname = ?2
// Is, Equals	findByFirstname,findByFirstnameIs,findByFirstnameEquals	… where x.firstname = ?1
// Between	findByStartDateBetween	… where x.startDate between ?1 and ?2
// LessThan	findByAgeLessThan	… where x.age < ?1
// LessThanEqual	findByAgeLessThanEqual	… where x.age <= ?1
// GreaterThan	findByAgeGreaterThan	… where x.age > ?1
// GreaterThanEqual	findByAgeGreaterThanEqual	… where x.age >= ?1
// After	findByStartDateAfter	… where x.startDate > ?1
// Before	findByStartDateBefore	… where x.startDate < ?1
// IsNull, Null	findByAge(Is)Null	… where x.age is null
// IsNotNull, NotNull	findByAge(Is)NotNull	… where x.age not null
// Like	findByFirstnameLike	… where x.firstname like ?1
// NotLike	findByFirstnameNotLike	… where x.firstname not like ?1
// StartingWith	findByFirstnameStartingWith	… where x.firstname like ?1 (parameter bound with appended %)
// EndingWith	findByFirstnameEndingWith	… where x.firstname like ?1 (parameter bound with prepended %)
// Containing	findByFirstnameContaining	… where x.firstname like ?1 (parameter bound wrapped in %)
// OrderBy	findByAgeOrderByLastnameDesc	… where x.age = ?1 order by x.lastname desc
// Not	findByLastnameNot	… where x.lastname <> ?1
// In	findByAgeIn(Collection<Age> ages)	… where x.age in ?1
// NotIn	findByAgeNotIn(Collection<Age> ages)	… where x.age not in ?1
// True	findByActiveTrue()	… where x.active = true
// False	findByActiveFalse()	… where x.active = false
// IgnoreCase	findByFirstnameIgnoreCase	… where UPPER(x.firstname) = UPPER(?1)