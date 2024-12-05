package edu.kh.project.member.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	/** 로그인 서비스
	 * @param memberId
	 * @return
	 */
	Member login(String memberId);
	
	/** 이메일 중복 체크
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/** 아이디 중복 체크
	 * @param memberId
	 * @return
	 */
	int checkId(String memberId);

	/** 닉네임 중복 체크
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	/** 회원가입 서비스
	 * @param inputMember
	 * @return
	 */
	int signup(Member inputMember);

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
