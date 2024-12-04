package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.mapper.BoardMapper;
import edu.kh.project.common.util.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor=Exception.class)
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService{
	

	private final BoardMapper mapper;
	
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		// TODO Auto-generated method stub
		return null;
	}

	
	// 게시판 글 목록 조회
	@Override
	public Map<String, Object> communityList(int cp, int boardCode) {
		// 게시글 목록 개수 조회
		int boardCount = mapper.communityCount(boardCode);
		
		// 페이지네이션 진행
		Pagination pagination = new Pagination(cp, boardCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Board> boardList = mapper.communityList(rowBounds, boardCode); 
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		return map;
	}


	// 자유게시판 검색 게시글 조회
	@Override
	public Map<String, Object> communitySearchList(int cp, Map<String, Object> paramMap) {
		// 도서 목록 개수 조회
		int boardCount = mapper.communitySearchCount(paramMap);
		
		// 페이지네이션 진행
		Pagination pagination = new Pagination(cp, boardCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Board> boardList = mapper.communitySearchList(rowBounds, paramMap); 
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		return map;
	}


	// 자유게시판 글 상세 조회
	@Override
	public Board detailCommunity(Board board) {
		return mapper.detailCommunity(board);
	}


	// 게시글 작성
	@Override
	public int boardInsert(Board inputBoard) {
		return mapper.boardInsert(inputBoard);
	}


	// 게시글 삭제
	@Override
	public int boardDelete(Map<String, Object> paramMap) {
		return mapper.boardDelete(paramMap);
	}
	
	
	// 관리자 권한 게시글 삭제
	@Override
	public int adminBoardDelete(Map<String, Object> paramMap) {
		return mapper.adminBoardDelete(paramMap);
	}


	// 게시글 수정
	@Override
	public int boardUpdate(Map<String, Object> paramMap) {
		return mapper.boardUpdate(paramMap);
	}


	// 조회수 1 증가 서비스
	@Override
	public int updateReadCount(int boardNo) {
		
		// 조회수 1증가
		int result = mapper.updateReadCount(boardNo);
		
		if(result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		
		// 실패한 경우 -1 반환
		return -1;
		
	}


	// 메인 페이지 공지 게시글 상단 3개 조회
	@Override
	public List<Board> selectMainNotice() {
		return mapper.selectMainNotice();
	}


	// 메인 페이지 자유 게시판 게시글 상단 3개 조회
	@Override
	public List<Board> selectMainCommnunity() {
		return mapper.selectMainCommnunity();
	}
}
