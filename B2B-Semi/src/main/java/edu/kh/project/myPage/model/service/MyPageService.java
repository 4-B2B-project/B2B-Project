package edu.kh.project.myPage.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	
	// 회원 정보 수정
	int editInfo(Member inputMember, String[] memberAddress);
	
	// 비밀번호 변경
	int changePw(Map<String, Object> paramMap, int memberNo);

	
	// 찜한 도서목록 호출
	List<Book> selectFavoriteBooks(int memberNo); //int memberNo

	
	
	/** 내정보 - 프로필 이미지 수정
	 * @param profileImg
	 * @param loginMember
	 * @return
	 */
	int profileImageInfo(MultipartFile profileImg, Member loginMember) throws Exception;


}
