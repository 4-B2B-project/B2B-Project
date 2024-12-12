package edu.kh.project.member.model.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.common.util.Pagination;
import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor=Exception.class)
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

	private final AdminMapper mapper;
	
	@Value("${my.profile.web-path}")
	private String profileWebPath;
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath;
	
	// 도서 수 조회.
	@Override
	public int bookCount() {
		return mapper.bookCount();
	}

	// 회원 수 조회.
	@Override
	public int memberCount() {
		return mapper.memberCount();
	}

	// 게시글 수 조회.
	@Override
	public int boardCount() {
		return mapper.boardCount();
	}
	
	// 관리자 프로필 사진 변경.
	@Override
	public int profileImage(MultipartFile profileImg, Member loginMember) throws Exception{

		String updatePath = null;
		
		String rename = null;
		
		if(!profileImg.isEmpty()) {
			
			rename = Utility.fileRename(profileImg.getOriginalFilename());
			
			updatePath = profileWebPath + rename;
			
		}
		
		Member mem = Member.builder().memberNo(loginMember.getMemberNo()).profileImg(updatePath).build();
	
		int result = mapper.profileImage(mem);
		
		if(result > 0) {
			
			if(!profileImg.isEmpty()) {
				profileImg.transferTo(new File(profileFolderPath + rename));
			}
			
			loginMember.setProfileImg(updatePath);
			
		}
		
		return result;
	}
	
	// 관리자 정보 수정.
	@Override
	public int editInfo(Member inputMember, String[] memberAddress) {

		if (inputMember.getMemberAddress().equals(",,")) {
			
			inputMember.setMemberAddress(null);
		}
		else {
			
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
		}
		
		return mapper.editInfo(inputMember);
	}
	
	
	// 회원 목록 조회. (검색 x)
	@Override
	public Map<String, Object> memberList(int cp) {

		int memberCount = mapper.memberCount();
		
		Pagination pagination = new Pagination(cp, memberCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Member> memberList = mapper.memberList(rowBounds);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("memberList", memberList);
		
		return map;
	}
	
	// 회원 목록 조회. (검색 o)
	@Override
	public Map<String, Object> memberSearchList(int cp, Map<String, Object> paramMap) {

	    if (paramMap.get("search") != null) {
	        paramMap.put("search", ((String)paramMap.get("search")).trim());
	    }
		
		int memberCount = mapper.searchMemberCount(paramMap);
		
		Pagination pagination = new Pagination(cp, memberCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Member> memberList = mapper.memberSearchList(paramMap, rowBounds);
		
		Map<String, Object> result = new HashMap<>();
		result.put("pagination", pagination);
		result.put("memberList", memberList);
		
		return result;
	}
	
	// 회원 검색.
	@Override
	public Map<String, Object> searchMember(Map<String, Object> paramMap, int cp) {

		int listCount = mapper.searchMemberCount(paramMap);
		
		Pagination pagination = new Pagination(cp, listCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		Map<String, Object> searchParam = new HashMap<>();
	    searchParam.put("pagination", pagination);
	    searchParam.putAll(paramMap);
	    
	    List<Member> memberList = mapper.searchMember(searchParam, rowBounds);
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("pagination", pagination);
	    map.put("memberList", memberList);
		
		return map;
	}
	
	// 선택한 회원 정보 조회.
	@Override
	public Member selectedMember(int memberNo) {

		return mapper.selectedMember(memberNo);
	}
	
	// 선택한 회원 정보 수정.
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {

		if(inputMember.getMemberAddress().equals(",,")) {
			inputMember.setMemberAddress(null);
		}
		
		else {
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
		}
		
		return mapper.updateInfo(inputMember);
	}
	
	// 회원 추방/탈퇴 복구.
	@Override
	public int updateStatus(List<String> memberIds, boolean updateY) {
		String status = updateY ? "Y" : "N";
		
		return mapper.updateStatus(memberIds, status);
	}
	
	// 도서 목록 조회. (검색 x)
	@Override
	public Map<String, Object> bookList(int cp) {

		int bookCount = mapper.bookCount();
		
		Pagination pagination = new Pagination(cp, bookCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Book> bookList = mapper.bookList(rowBounds);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("bookList", bookList);
		
		return map;
	}
	
	// 도서 목록 조회. (검색 o)
	@Override
	public Map<String, Object> bookSearchList(int cp, Map<String, Object> paramMap) {

		int bookCount = mapper.searchBookCount(paramMap);
		Pagination pagination = new Pagination(cp, bookCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Book> bookList = mapper.bookSearchList(paramMap, rowBounds);
		
		Map<String, Object> result = new HashMap<>();
		result.put("pagination", pagination);
		result.put("bookList", bookList);
		
		return result;
	}
	
	// 도서 삭제/삭제 복구.
	@Override
	public int updateBookStatus(List<String> bookList, boolean updateY) {

		String status = updateY ? "Y" : "N";
		return mapper.updateBookStatus(bookList, status);
	}

	// 도서 수정 페이지 및 데이터 보내기.
	@Override
	public Book selectBookDetail(int bookId) {

		return mapper.selectBookDetail(bookId);
	}
	
	// 도서 수정.
	@Override
	public int updateBook(Book book) {

		return mapper.updateBook(book);
	}
	
	// 게시판 관리 조회. (검색 x)
	@Override
	public Map<String, Object> boardList(int cp) {
		
		int boardCount = mapper.boardCount();
		
		Pagination pagination = new Pagination(cp, boardCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Board> boardList = mapper.boardList(rowBounds);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		return map;
	}
	
	
	// 게시판 관리 조회. (검색 o) / 게시글 검색.
	@Override
	public Map<String, Object> boardSearchList(int cp, Map<String, Object> paramMap) {
		
		int boardCount = mapper.searchBoardCount(paramMap);
		Pagination pagination = new Pagination(cp, boardCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Board> boardList = mapper.boardSearchList(paramMap, rowBounds);
		
		Map<String, Object> result = new HashMap<>();
		result.put("pagination", pagination);
		result.put("boardList", boardList);
		
		return result;
	}
	
	// 게시글 상세 조회.
	@Override
	public Board boardDetail(int boardNo) {

		return mapper.boardDetail(boardNo);
	}
	
	// 게시글 수정버튼
	@Override
	public int boardUpdate(Map<String, Object> paramMap) {

		return mapper.boardUpdate(paramMap);
	}
	
	// 게시글 삭제버튼
	@Override
	public int boardDelete(Map<String, Object> paramMap) {

		return mapper.boardDelete(paramMap);
	}
	
	// 게시글 삭제/삭제 복구.
	@Override
	public int updateBoardStatus(List<String> boardList, boolean updateY) {
		String status = updateY ? "Y" : "N";
		
		return mapper.updateBoardStatus(boardList, status);
	}

}