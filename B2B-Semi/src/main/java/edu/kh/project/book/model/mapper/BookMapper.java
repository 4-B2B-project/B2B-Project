package edu.kh.project.book.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.book.model.dto.Book;

@Mapper
public interface BookMapper {
	
	/** 주간 인기 도서
	 * @return
	 */
	List<Book> mainBookList();

	/** 도서 등록
	 * @param book
	 */
	int insertBook(Book book);
	
	/** 등록된 도서가 있는지 조회
	 * @param isbn
	 * @return
	 */
	int srchBook(String isbn);

	/** 도서 목록 개수 조회
	 * @return
	 */
	int bookCount();
	
	/** 도서 목록 조회
	 * @return
	 */
	List<Book> bookList(RowBounds rowBounds);

	/** 도서 목록 검색 개수 조회
	 * @param paramMap
	 * @return
	 */
	int bookSearchCount(Map<String, Object> paramMap);

	/** 도서 목록 검색 조회
	 * @param rowBounds
	 * @param paramMap
	 * @return
	 */
	List<Book> bookSearchList(RowBounds rowBounds, Map<String, Object> paramMap);

	/** 해당 도서 리뷰 목록 조회
	 * @param bookId
	 * @return
	 */
	List<Map<String, Object>> selectReviewList(int bookId);

	/** 해당 멤버의 도서 리뷰작성 여부 조회
	 * @param map
	 * @return
	 */
	int selectReviewMember(Map<String, Object> map);
	
	/** 책 리뷰 작성
	 * @param paramMap
	 * @return
	 */
	int insertBookReview(Map<String, Object> paramMap);

	/** 기존 도서 평점과 리뷰 개수 가져오기
	 * @param ratingMap
	 * @return
	 */
	Map<String, Object> getBookRatingData(Map<String, Object> ratingMap);

	/** 도서 평점 수정
	 * @param updateMap
	 */
	int updateBookRating(Map<String, Object> updateMap);

	/** top20 도서 목록 조회
	 * @return
	 */
	List<Book> topList();

	/** 장르별 랭킹 목록 조회
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

	/** 상세 기간별 랭킹 개수 조회
	 * @param paramMap
	 * @return
	 */
	int bookDetailPeriodListCount(Map<String, Object> paramMap);

	/** 상세 기간별 랭킹 조회
	 * @param rowBounds
	 * @param paramMap
	 * @return
	 */
	List<Book> bookDetailPeriodList(RowBounds rowBounds, Map<String, Object> paramMap);

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

	/** 찜 여부 확인
	 * @param map
	 * @return
	 */
	int selectSteamBook(Map<String, Object> map);

	/** 찜 도서 삭제
	 * @param map
	 * @return
	 */
	int deleteSteamBook(Map<String, Object> map);

	/** 찜 등록
	 * @param map
	 * @return
	 */
	int insertSteamBook(Map<String, Object> map);

	
}
