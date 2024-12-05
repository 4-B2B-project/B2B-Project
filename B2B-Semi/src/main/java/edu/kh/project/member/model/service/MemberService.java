package edu.kh.project.member.model.service;

import java.util.Map;

import edu.kh.project.member.model.dto.Member;

public interface MemberService {

	/** 로그인 서비스
	 * @param inputMember
	 * @return
	 */
	Member login(Member inputMember);

	/** 아이디 중복 체크(비동기)
	 * @param memberId
	 * @return
	 */
	int checkEmail(String memberEmail);
	
	/** 아이디 중복 체크(비동기)
	 * @param memberId
	 * @return
	 */
	int checkId(String memberId);

	/** 이름 중복 체크(비동기)
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	/** 회원 가입
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int signup(Member inputMember, String[] memberAddress);

	/** 아이디 찾기
	 * @param map
	 * @return
	 */
	String srchId(Map<String, String> map);

	/** 비밀번호 찾기전 해당 회원 존재 여부 조회
	 * @param map
	 * @return
	 */
	int searchUser(Map<String, String> map);

	/** 비밀번호 변경
	 * @param paramMap
	 * @return
	 */
	int updatePw(Map<String, Object> paramMap);

}
