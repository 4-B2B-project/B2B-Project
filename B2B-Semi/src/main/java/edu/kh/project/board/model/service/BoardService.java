package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;

public interface BoardService {

	List<Map<String, Object>> selectBoardTypeList();

	/** 게시판 게시글 조회
	 * @param cp
	 * @return
	 */
	Map<String, Object> communityList(int cp, int boardCode);

	/** 게시판 검색 게시글 조회
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> communitySearchList(int cp, Map<String, Object> paramMap);

	/** 게시판 글 상세 조회
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

	/** 조회수 1증가 서비스
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);

	/** 메인 페이지 공지 게시글 상단 3개 조회
	 * @return
	 */
	List<Board> selectMainNotice();

	/** 메인 페이지 자유 게시판 게시글 상단 3개 조회
	 * @return
	 */
	List<Board> selectMainCommnunity();

}
