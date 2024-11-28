package edu.kh.project.member.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;

@Mapper
public interface AdminMapper {

	/** 게시글 수 조회.
	 * @param boardCode
	 * @return
	 */
	int getListCount(int boardCode);

	/** 특정 게시판의 지정된 페이지 목록 조회.
	 * @param boardCode
	 * @param rowBounds
	 * @return
	 */
	List<Board> selectBoardList(int boardCode, RowBounds rowBounds);

	/** 회원 정보 수정(관리자)
	 * @param inputMember
	 * @return
	 */
	int updateMember(Member inputMember);

	int updateInfo(Member inputMember);

	Member selectedMember(int memberNo);

	List<Member> searchMember(Map<String, Object> map);

	int updateStatus(@Param("memberIds")List<String> memberIds, @Param("status") String status);

	Board selectOne(Map<String, Integer> map);

	int boardCount();

	List<Board> boardList(RowBounds rowBounds);

	int updateBoardStatus(@Param("boardList")List<String> boardList, @Param("status") String status);

	List<Board> searchBoard(Map<String, Object> paramMap);

	int bookCount();

	List<Book> bookList(RowBounds rowBounds);

	int updateBookStatus(@Param("bookList")List<String> bookList, @Param("status") String status);

	int insertNewBook(Map<String, Object> paramMap);

	List<Book> bookSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	List<Board> boardSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	List<Book> searchBookList(Map<String, Object> paramMap, RowBounds rowBounds);

	int searchBookCount(Map<String, Object> paramMap);

	int searchBoardCount(Map<String, Object> paramMap);

	int memberCount();

	List<Member> memberList(RowBounds rowBounds);

	int searchMemberCount(Map<String, Object> paramMap);

	List<Member> memberSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	Book selectBookDetail(Map<String, Object> paramMap);


}