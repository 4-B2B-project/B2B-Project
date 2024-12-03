package edu.kh.project.myPage.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	

	/** 회원 정보 수정
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int editInfo(Member inputMember, String[] memberAddress);
	
	/**  비밀번호 변경
	 * @param paramMap
	 * @param memberNo
	 * @return
	 */
	int changePw(Map<String, Object> paramMap, int memberNo);

	/** 찜한 도서목록 호출
	 * @param memberNo
	 * @return
	 */
	List<Book> selectFavoriteBooks(int memberNo); 

	
	/** 내정보 - 프로필 이미지 수정
	 * @param profileImg
	 * @param loginMember
	 * @return
	 */
	int profileImageInfo(MultipartFile profileImg, Member loginMember) throws Exception;


	/** 게시글 목록 조회
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectBoardList(int memberNo, int cp);

	
	/** 게시글 목록 검색 조회
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> searchBoardList(int cp, Map<String, Object> paramMap);


	
	/** 댓글 목록 조회
	 * @param memberNo
	 * @return
	 */
	Map<String, Object> selectCommentList(int memberNo, int cp);

	
	/** 댓글 목록 검색 조회
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> commentSearchList(int cp, Map<String, Object> paramMap);

	
	
	/** 게시글 상세정보 조회
	 * @param boardNo
	 * @return
	 */
	Board selectBoardDetail(int boardNo);

	
	
	/** 게시글 상세정보 수정
	 * @param inputBoard
	 * @param deleteOrderList
	 * @return
	 */
	int boardUpdate(Board inputBoard);

	/** 게시글 삭제
	 * @param map
	 * @return
	 */
	int boardDelete(Map<String, Integer> map);

	
	
	/** 댓글 상세정보 조회
	 * @param boardNo
	 * @return
	 */
	Comment selectCommentDetail(int commentNo);

	
	/**  댓글,답글 등록
	 * @param comment
	 * @return
	 */
	int replyInsert(Comment comment);

	
	
	/** 좋아요한 도서 수(count)
	 * @param memberNo
	 * @return
	 */
	int getFavoriteBookCount(int memberNo);

	/** 댓글 수(count)
	 * @param memberNo
	 * @return
	 */
	int getCommentCount(int memberNo);

	/** 게시글 수(count)
	 * @param memberNo
	 * @return
	 */
	int getBoardCount(int memberNo);

	/** 댓글 수정
	 * @param comment
	 * @return
	 */
	int updateComment(Comment comment);

	/** 댓글 삭제
	 * @param commentNo
	 * @param memberNo
	 * @return
	 */
	int deleteComment(int commentNo, int memberNo);

	
	
	
	
	
	
	

	
	

}
