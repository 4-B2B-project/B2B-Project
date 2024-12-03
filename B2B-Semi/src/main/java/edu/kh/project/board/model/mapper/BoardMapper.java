package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	/** 자유게시판 글 개수 조회
	 * @return
	 */
	int communityCount(int boardCode);

	/** 자유게시판 글 목록 조회
	 * @param rowBounds
	 * @return
	 */
	List<Board> communityList(RowBounds rowBounds, int boardCode);

	/** 자유게시판 글 검색 개수 조회
	 * @return
	 */
	int communitySearchCount(Map<String, Object> paramMap);

	/** 자유게시판 글 검색 목록 조회
	 * @param rowBounds
	 * @return
	 */
	List<Board> communitySearchList(RowBounds rowBounds, Map<String, Object> paramMap);

	/** 자유게시판 글 상세 조회
	 * @param board
	 * @return
	 */
	Board detailCommunity(Board board);

	/** 게시글 작성
	 * @param inputBoard
	 * @return
	 */
	int boardInsert(Board inputBoard);

	/** 게시글 삭제
	 * @param paramMap
	 * @return
	 */
	int boardDelete(Map<String, Object> paramMap);

	/** 게시글 수정
	 * @param paramMap
	 * @return
	 */
	int boardUpdate(Map<String, Object> paramMap);

	/** 조회수 1증가 
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);

	/** 현재 조회수 조회
	 * @param boardNo
	 * @return
	 */
	int selectReadCount(int boardNo);

	/** 메인 페이지 공지 게시글 상단 3개 조회
	 * @return
	 */
	List<Board> selectMainNotice();

	/** 메인 페이지 자유 게시판 게시글 상단 3개 조회
	 * @return
	 */
	List<Board> selectMainCommnunity();

}
