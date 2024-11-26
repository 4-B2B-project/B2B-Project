package edu.kh.project.myPage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;
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
	List<Book> selectFavoriteBooks(int memberNo); //int memberNo


	
	
	/** 프로필 이미지 변경
	 * @param mem
	 * @return
	 */
	int profileImageInfo(Member mem);
	
	

	

	/** 작성자의 전체 게시글 조회
	 * @param paramMap
	 * @return
	 */
	int getBoardListCount(int memberNo);

	
	
	/** 전체 게시글 목록 조회
	 * @param paramMap
	 * @param pagination
	 * @return
	 */
	List<Board> selectAllBoardList(int memberNo);


	
	/** 검색 결과 조회
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> searchBoardList(int cp, Map<String, Object> paramMap);


	
	
	
	

}
