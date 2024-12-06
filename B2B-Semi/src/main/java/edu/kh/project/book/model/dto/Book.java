package edu.kh.project.book.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Book {

	// DB 항목
	private int bookId;					// 도서 아이디
	private String title;				// 도서 제목
	private String isbn;				// 도서 국제표준번호
	private String author;				// 도서 저자
	private String publisher;			// 도서 출판사
	private String pubDate;				// 도서 출판일
	private String description;			// 도서 설명
	private String coverUrl;			// 도서 커버 사진
	private String firstCategory;		// 도서 첫번째 장르
	private String secondCategory;		// 도서 두번째 장르
	private String isDeleted;			// 도서 삭제여부
	private String createdAt;			// 도서 등록일자
	private String updatedAt;			// 도서 수정일자
	
	// DB외의 별도 추가 항목
	private String genres;				// 도서 1,2장르 합친부분
	private String fullTitle;			// 도서 전체 제목
	private String fullAuthor;			// 작가 전체 제목
	private float customerReviewRank;	// 도서 리뷰 점수
	private int rowNum;					// rownum
	private int reviewCount;			// 리뷰 카운트
	private int steamCount;				// 찜 카운트
	private int rankNum;				// 랭킹 번호
	
	
}
