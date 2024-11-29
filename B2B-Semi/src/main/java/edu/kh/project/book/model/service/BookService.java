package edu.kh.project.book.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.book.model.dto.Book;

public interface BookService {
	
	/** 메인 페이지 도서 목록
	 * @return
	 */
	List<Book> mainBookList();

	/** api 도서 등록 (테스트 버전)
	 * @return
	 */
	int insertBook();

	/** api 도서 검색 (테스트 버전)
	 * @param title
	 * @return
	 */
	List<Book> srchBookList(String title);

	/** 도서 목록 조회
	 * @return book
	 */
	Map<String, Object> bookList(int cp);

	/** 도서 목록 검색 조회
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> bookSearchList(int cp, Map<String, Object> paramMap);

	/** 해당 도서 리뷰 목록 조회
	 * @param bookId
	 * @return
	 */
	List<Map<String, Object>> selectReviewList(int bookId);
	
	/** 책 리뷰 작성
	 * @param paramMap
	 * @return
	 */
	int insertBookReview(Map<String, Object> paramMap);

	/** top20 목록 조회
	 * @return
	 */
	List<Book> topList();

	/** 장르별 랭킹 목록
	 * @return
	 */
	List<Book> bookCategoryList();

	/** 장르별 베스트 top10 페이지 장르 목록
	 * @return
	 */
	List<Map<String, Object>> selectCategoryList();

	/** 선택된 장르 도서 top10 조회
	 * @param category
	 * @return
	 */
	List<Book> selectCategortBestBook(String category);

	/** 월간 도서 랭킹 top10 조회
	 * @return
	 */
	List<Book> selectMonthPeriodList();

	/** 연간 도서 랭킹 top10 조회
	 * @return
	 */
	List<Book> selectYearPeriodList();

	/** 상세 기간별 랭킹 조회
	 * @param map
	 * @return
	 */
	Map<String, Object> bookDetailPeriodList(Map<String, Object> map, int cp);

	/** 리뷰 수정
	 * @param paramMap
	 * @return
	 */
	int updateBookReview(Map<String, Object> paramMap);

	/** 리뷰 삭제
	 * @param paramMap
	 * @return
	 */
	int deleteReview(Map<String, Object> paramMap);

	/** 찜하기
	 * @param bookId
	 * @param memberNo
	 * @return
	 */
	int steamBook(int bookId, int memberNo);

	/** 찜 여부 조회
	 * @param paramMap
	 * @return
	 */
	int isBookSteam(Map<String, Object> paramMap);


}
