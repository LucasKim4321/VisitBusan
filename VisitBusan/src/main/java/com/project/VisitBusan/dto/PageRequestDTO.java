package com.project.VisitBusan.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;  // 페이지 번호

    @Builder.Default
    private int size = 8;  // 페이지당 보여질 항목 개수

    private String type;  // 검색 종류 = t,c,w,tc,tw,twc,writerId   t=title c=content w=writer
    private String keyword;  // 검색어
    private String bCategory;  // 카테고리 = information, schedule, review, festival ...
    private LocalDate bStartDate;  // 축제 시작 날짜
    private LocalDate bEndDate;  // 축제 끝나는 날짜
    private LocalDateTime regDate;  // 등록일

    // 키워드에 대한 type을 구분하여 배열구조로 반환
    public String[] getTypes() {
        if (type== null || type.isEmpty()) return null;
        if (type.equals("tc") || type.equals("tw") || type.equals("twc")) {
            return type.split("");
        }
        else{
            String[] arrType = {type};
            return arrType;
        }
    }

    // 페이징 초기값 설정
    public Pageable getPageable(String... props) {
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending());
    }

    // 검색 조건 매개변수 설정과 페이지 조건 매개 변수 설정을 처리하는 문자열
    private String link;
    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page="+this.page);
            builder.append("&size="+this.size);

            if (bCategory != null && bCategory.length()>0)  // 카테고리 추가
                builder.append("&bCategory="+bCategory);

            if (type != null && type.length()>0)  // 타입이 있으면 추가
                builder.append("&type="+type);

            if (keyword != null && keyword.length()>0) {  // 키워드가 있으면 추가
                try {
                    builder.append("&keyword=" + URLEncoder.encode(this.keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            if (bStartDate != null)  // 축제 시작 날짜 추가
                builder.append("&bStartDate="+bStartDate);

            if (bEndDate != null)  // 축제 끝나는 날짜 추가
                builder.append("&bEndDate="+bEndDate);

            if (regDate != null)  // 등록일 추가
                builder.append("&regDate="+regDate);

            // link = page=1&size10&type=twc&keyword=URLEncoder.Encode("홍길동")...
            link = builder.toString();
        }

        return link;
    }
}