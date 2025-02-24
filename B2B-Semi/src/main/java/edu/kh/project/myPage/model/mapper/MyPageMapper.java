package edu.kh.project.myPage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.common.util.Pagination;
import edu.kh.project.member.model.dto.Member;

/**
 * 
 */
@Mapper
public interface MyPageMapper {

	
	/** 회원의 비밀번호 조회
	 * @param memberNo
	 * @return
	 */
	String selectPw(int memberNo);

	
	/** 비밀번호 수정
	 * @param paramMap
	 * @return
	 */
	int changePw(Map<String, Object> paramMap);


	/** 회원 정보 수정
	 * @param inputMember
	 * @return
	 */
	int editInfo(Member inputMember);


	/** 찜한 도서 목록 조회
	 * @param memberNo
	 * @return 
	 */
	List<Book> selectFavoriteBooks(int memberNo); 


	
	
	/** 프로필 이미지 변경
	 * @param mem
	 * @return
	 */
	int profileImageInfo(Member mem);
	
	

	

	/** 작성자의 전체 게시글 수 조회
	 * @param paramMap
	 * @return
	 */
	int getBoardListCount(int memberNo);

	
	/** 게시글 목록 조회
	 * @param paramMap
	 * @param pagination
	 * @return
	 */
	List<Board> selectAllBoardList(int memberNo, RowBounds rowBounds);

	
	
	
	
	
	/** 게시글 수 조회
	 * @param paramMap
	 * @return
	 */
	int getSearchBoardListCount(Map<String, Object> paramMap);
	
	
	/** 게시글 검색 결과 조회
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	List<Board> searchBoardList(Map<String, Object> paramMap, RowBounds rowBounds);

	
	
	
	

	
	


	
	/** 검색 조건에 따른 댓글 수 조회
	 * @param memberNo
	 * @return
	 */
	int getCommentListCount(int memberNo);

	/** 댓글 목록 조회
	 * @param memberNo
	 * @return
	 */
	List<Comment> selectAllCommentList(int memberNo, RowBounds rowBounds);


	
	
	
	/** 검색 조건에 따른 댓글 수 조회
	 * @param paramMap
	 * @return
	 */
	int getSearchCommentListCount(Map<String, Object> paramMap);


	/** 댓글 검색 결과 조회
	 * @param paramMap
	 * @param rowBounds
	 * @return
	 */
	List<Comment> searchCommentList(Map<String, Object> paramMap, RowBounds rowBounds);


	
	/** 게시글 상세 정보 조회
	 * @param boardNo
	 * @return
	 */
	Board selectBoardDetail(int boardNo);


	/** 게시글 상세정보 수정
	 * @param inputBoard
	 * @return
	 */
	int boardUpdate(Board inputBoard);


	/** 게시글 삭제
	 * @param map
	 * @return
	 */
	int boardDelete(Map<String, Integer> map);




	

	

	

	

	
	
	
	

}
