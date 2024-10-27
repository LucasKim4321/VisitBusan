package com.project.VisitBusan.repository.search;

import com.project.VisitBusan.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
// 구현 클래스 : 반드시 '인터페이스이름+impl" 표현해야 한다.
public class MemberSearchImpl extends QuerydslRepositorySupport implements MemberSearch {

    // 인자가 있을 경우
    /*
    public BoardSearchImpl(Class<?> domainClass) {
    super(domainClass);
    */

    public MemberSearchImpl() {
        super(Member.class);
    }

    @Override
    public Page<Member> searchAll(String[] types, String keyword, Pageable pageable) {    // pageable은 항상 마지막에 와야함

        QMember member = QMember.member;

        JPQLQuery<Member> query = from(member);

        // 조건문을 작성하는 클래스
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if ((types != null && types.length>0) && keyword != null) {  // 검색 키워드가 있으면

            // 타입
            for (String type : types) {
                switch (type) {
                    case "userId":
                        booleanBuilder.or(member.userId.contains(keyword)); break;
                    case "name":
                        booleanBuilder.or(member.name.contains(keyword)); break;
                    case "email":
                        booleanBuilder.or(member.email.contains(keyword)); break;
                    case "address":
                        booleanBuilder.or(member.address.contains(keyword)); break;
                }
            } // end for


        } // end if type

        // 카테고리 조건 추가
//            booleanBuilder.and(member.category.contains(category));

        query.where(booleanBuilder);

        // paging 추가
//        this.getQuerydsl().applyPagination(pageable, query);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(member.id.desc());

        // query 실행
        List<Member> list = query.fetch();
        long count = query.fetchCount();
        // 또는 long count = query.fetch().size();

        return new PageImpl<>(list, pageable, count);
    }

} // end class

/** 페이징(Paging)은 데이터베이스나 리스트에서 대량의 데이터를 한 번에 처리하지 않고, 여러 페이지로 나누어 처리하는 방법입니다. 이렇게 하면 한 번에 다루는 데이터의 양을 줄여서 성능을 개선하고, 사용자가 데이터를 쉽게 탐색할 수 있게 됩니다. 페이징을 구현할 때 일반적으로 사용하는 요소들은 다음과 같습니다:

 페이지 번호(Page Number):

 사용자가 보고자 하는 페이지의 번호입니다. 일반적으로 1부터 시작합니다.
 페이지 크기(Page Size):

 한 페이지에 포함될 데이터의 개수입니다. 예를 들어, 페이지 크기가 10이면 한 페이지에 10개의 데이터가 표시됩니다.
 총 데이터 개수(Total Count):

 전체 데이터의 총 개수입니다. 이는 페이징의 총 페이지 수를 계산하는 데 사용됩니다.
 총 페이지 수(Total Pages):

 총 데이터 개수와 페이지 크기를 사용하여 계산됩니다. 예를 들어, 총 데이터가 50개이고 페이지 크기가 10이면 총 페이지 수는 5가 됩니다.
 오프셋(Offset):

 데이터베이스 쿼리에서 특정 페이지의 데이터를 가져오기 위해 사용하는 시작 지점입니다. 예를 들어, 페이지 크기가 10이고 페이지 번호가 2인 경우, 오프셋은 10이 됩니다(즉, 11번째 데이터부터 가져옴).
 페이징의 예
 페이지 번호: 1

 오프셋: 0
 가져올 데이터 개수: 10
 페이지 번호: 2

 오프셋: 10
 가져올 데이터 개수: 10
 Spring Data JPA에서 페이징 사용 예시
 Spring Data JPA에서는 Pageable 인터페이스를 사용하여 페이징을 쉽게 구현할 수 있습니다. 예를 들어, 다음과 같은 코드를 통해 페이징을 구현할 수 있습니다:

 java
 코드 복사
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Pageable;

 public void getPagedData() {
 int page = 0; // 페이지 번호 (0부터 시작)
 int size = 10; // 페이지 크기

 Pageable pageable = PageRequest.of(page, size);
 Page<MyEntity> resultPage = myRepository.findAll(pageable);

 System.out.println("Total pages: " + resultPage.getTotalPages());
 System.out.println("Total elements: " + resultPage.getTotalElements());
 System.out.println("Current page number: " + resultPage.getNumber());
 System.out.println("Current page size: " + resultPage.getSize());
 resultPage.getContent().forEach(System.out::println); // 현재 페이지의 데이터
 }
 위 예시에서 PageRequest.of(page, size)를 통해 Pageable 객체를 생성하고, findAll(pageable) 메서드를 사용하여 특정 페이지의 데이터를 가져옵니다. Page 객체는 페이징 관련 정보를 제공하며, getContent() 메서드를 통해 현재 페이지의 데이터를 리스트 형태로 가져올 수 있습니다.

 페이징의 장점
 성능 향상: 한 번에 처리하는 데이터 양을 줄여서 메모리 사용량을 줄이고, 쿼리 성능을 향상시킵니다.
 사용자 경험 개선: 사용자가 데이터를 쉽게 탐색하고, 필요한 데이터를 빠르게 찾을 수 있도록 도와줍니다.
 관리 용이성: 대량의 데이터를 관리하고 처리하는 데 효율적입니다.
 페이징을 적절히 사용하면 대량의 데이터를 효과적으로 처리하고, 애플리케이션의 성능과 사용성을 크게 개선할 수 있습니다.*/

/** JPA에서 제공하는 PageImpl클래스는 3개의 파라미터로 Page<T>를 생성
 public PageImpl(List<T> content, Pageable pageable, long total) {

 super(content, pageable);

 this.total = pageable.toOptional().filter(it -> !content.isEmpty())//
 .filter(it -> it.getOffset() + it.getPageSize() > total)//
 .map(it -> it.getOffset() + content.size())//
 .orElse(total);
 }
 public String toString() {

 String contentType = "UNKNOWN";
 List<T> content = getContent();

 if (!content.isEmpty() && content.get(0) != null) {
 contentType = content.get(0).getClass().getName();
 }

 return String.format("Page %s of %d containing %s instances", getNumber() + 1, getTotalPages(), contentType);
 }*/

/** 페이징 기능 applyPagination
 Applies the given {@link Pageable} to the given {@link JPQLQuery}.
 @param pageable must not be {@literal null}.
 @param query must not be {@literal null}.
 @return the Querydsl {@link JPQLQuery}.

 public <T> JPQLQuery<T> applyPagination(Pageable pageable, JPQLQuery<T> query) {

 Assert.notNull(pageable, "Pageable must not be null");
 Assert.notNull(query, "JPQLQuery must not be null");

 if (pageable.isUnpaged()) {
 return query;
 }

 query.offset(pageable.getOffset());
 query.limit(pageable.getPageSize());

 return applySorting(pageable.getSort(), query);
 }*/
