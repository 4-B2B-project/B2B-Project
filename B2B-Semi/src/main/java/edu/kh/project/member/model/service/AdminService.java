package edu.kh.project.member.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;

public interface AdminService {
	
	// 도서 수 조회.
	public int bookCount();
	
	// 회원 수 조회.
	public int memberCount();

	// 게시글 수 조회.
	public int boardCount();
	
	// 관리자 프로필 사진 변경.
	public int profileImage(MultipartFile profileImg, Member loginMember) throws Exception;
	
	// 관리자 정보 수정.
	public int editInfo(Member inputMember, String[] memberAddress);

	// 회원 전체 목록 조회.
	public Map<String, Object> memberList(int cp);
	
	// 검색한 회원 목록 조회.
	public Map<String, Object> memberSearchList(int cp, Map<String, Object> paramMap);
	
	// 회원 검색.
	public Map<String, Object> searchMember(Map<String, Object> paramMap, int cp);
	
	// 선택한 회원 정보 수정 페이지.
	public Member selectedMember(int memberNo);
	
	// 회원 정보 수정.
	public int updateInfo(Member inputMember, String[] memberAddress);

	// 회원 추방/탈퇴 복구.
	public int updateStatus(List<String> memberIds, boolean updateY);

	// 도서 전체 목록.
	public Map<String, Object> bookList(int cp);
	
	// 검색한 도서 목록.
	public Map<String, Object> bookSearchList(int cp, Map<String, Object> paramMap);
	
	// 도서 삭제/삭제 복구.
	public int updateBookStatus(List<String> bookList, boolean updateY);
	
	// 도서 데이터 보내기/도서 수정 페이지
	public Book selectBookDetail(int bookId);
	
	// 도서 수정.
	public int updateBook(Book book);
	
	// 게시글 전체 목록.
	public Map<String, Object> boardList(int cp);

	// 검색한 게시글 목록/검색
	public Map<String, Object> boardSearchList(int cp, Map<String, Object> paramMap);
	
	// 게시글 상세 조회.
	public Board boardDetail(int boardNo);
	
	// 게시글 수정.
	public int boardUpdate(Map<String, Object> paramMap);
	
	// 게시글 삭제.
	public int boardDelete(Map<String, Object> paramMap);

	// 게시글 삭제/삭제 복구.
	public int updateBoardStatus(List<String> boardList, boolean updateY);

}