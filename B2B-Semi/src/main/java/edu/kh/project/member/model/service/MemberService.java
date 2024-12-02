package edu.kh.project.member.model.service;

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
	int checkId(String memberId);

	/** 이름 중복 체크(비동기)
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	int signup(Member inputMember, String[] memberAddress);

}
