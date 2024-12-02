package edu.kh.project.member.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;

public interface AdminService {

	public int updateInfo(Member inputMember, String[] memberAddress);

	public Member selectedMember(int memberNo);

	public List<Member> searchMember(Map<String, Object> paramMap);

	public int updateStatus(List<String> memberIds, boolean updateY);

	public Map<String, Object> boardList(int cp);

	public Map<String, Object> boardSearchList(int cp, Map<String, Object> paramMap);

	public int updateBoardStatus(List<String> boardList, boolean updateY);

	public List<Board> searchBoard(Map<String, Object> paramMap);

	public Map<String, Object> bookList(int cp);

	public Map<String, Object> bookSearchList(int cp, Map<String, Object> paramMap);

	public int updateBookStatus(List<String> bookList, boolean updateY);

	public Map<String, Object> searchBookList(Map<String, Object> paramMap, int cp);

	public Map<String, Object> memberList(int cp);

	public Map<String, Object> memberSearchList(int cp, Map<String, Object> paramMap);


	public Book selectBookDetail(int bookId);


	public int updateBook(Book book);

	public Board boardDetail(int boardNo);

	public int boardUpdate(Map<String, Object> paramMap);

	public int boardDelete(Map<String, Object> paramMap);

	public int editInfo(Member inputMember, String[] memberAddress);

}