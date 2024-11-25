package edu.kh.project.myPage.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	
	// 회원 정보 수정
	int editInfo(Member inputMember, String[] memberAddress);
	
	// 비밀번호 변경
	int changePw(Map<String, Object> paramMap, int memberNo);

	
	// 찜한 도서목록 호출
	List<Book> selectFavoriteBooks(); //int memberNo

	

}
