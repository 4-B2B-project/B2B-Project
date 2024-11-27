package edu.kh.project.member.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;

public interface AdminService {

	public Map<String, Object> selectBoardList(int boardCode, int cp);

	public List<Member> selectMemberList();

	/** 회원 수정 버튼 클릭 시.
	 * @param inputMember
	 * @return
	 */
	public int updateMember(Member inputMember);

	public int updateInfo(Member inputMember, String[] memberAddress);

	public Member selectedMember(String memberId);

	public List<Member> searchMember(Map<String, Object> paramMap);

	public int updateStatus(List<String> memberIds, boolean updateY);

	public Board selectOne(Map<String, Integer> map);

	public Map<String, Object> boardList(int cp);

	public Map<String, Object> boardSearchList(int cp, Map<String, Object> paramMap);

	public int updateBoardStatus(List<String> boardList, boolean updateY);

	public List<Board> searchBoard(Map<String, Object> paramMap);

	public Map<String, Object> bookList(int cp);

	public Map<String, Object> bookSearchList(int cp, Map<String, Object> paramMap);

	public int updateBookStatus(List<String> bookList, boolean updateY);

	public int insertNewBook(Map<String, Object> paramMap);

}