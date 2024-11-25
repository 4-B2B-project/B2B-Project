package edu.kh.project.book.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.book.model.dto.Book;

@Mapper
public interface BookMapper {

	/** 도서 등록 (테스트 버전)
	 * @param book
	 */
	void insertBook(Book book);

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

	
	
}
