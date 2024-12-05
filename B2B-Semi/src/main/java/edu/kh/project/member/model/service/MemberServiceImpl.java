package edu.kh.project.member.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import edu.kh.project.member.model.dto.Member;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	private final MemberMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Override
	public Member login(Member inputMember) {
				// 암호화 진행
		
				// bcrypt.encode(문자열) : 문자열을 암호화하여 반환
				String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
				log.debug("bcryptPassword : " + bcryptPassword);
				
				// bcrypt.matches(평문, 암호화) : 평문과 암호화가 일치하면 true, 아니면 false
				// boolean result = bcrypt.matches(inputMember.getMemberPw(), 
				// "$2a$10$mQuTt31FyF3uXL2qAkF21eZsPnoQP6zeo9pKCevmsWtGJEOsKtFhu");
				
				// log.debug("result : " + result);
				
				// 1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회
				Member loginMember = mapper.login(inputMember.getMemberId());
				
				// 2. 만약에 일치하는 이메일이 없어서 조회 결과가 null 인 경우
				if(loginMember == null) return null;
				
				// 3. 입력 받은 비밀번호(평문 : inputMember.getMemberPw())와 
				//    암호화된 비밀번호(loginMember.getMemberPw()) 
				//    두 비밀번호가 일치하는지 확인
				
				// 일치하지 않으면
				if( !bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw()) ) {
					return null;
				}
				
				// 로그인 결과에서 비밀번호 제거
				loginMember.setMemberPw(null);
				
				
				return loginMember;
	}
	

	// 이메일 중복 체크
	@Override
	public int checkEmail(String memberEmail) {
		return mapper.checkEmail(memberEmail);
	}
	
	// 아이디 중복 체크
	@Override
	public int checkId(String memberId) {
		return mapper.checkId(memberId);
	}


	// 닉네임 중복체크
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}
	
	
	// 회원가입 서비스
	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		// 주소가 입력된 경우
		if (!inputMember.getMemberAddress().equals(",,")) {

			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);

		} else {
			// 주소가 입력되지 않은 경우
			inputMember.setMemberAddress(null); // null 저장
		}

		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);

		// 회원 가입 매퍼 메서드 호출
		return mapper.signup(inputMember);
	}


	// 아이디 찾기
	@Override
	public String srchId(Map<String, String> map) {
		return mapper.srchId(map);
	}


	// 비밀번호 찾기전 해당 회원 존재 여부 조회
	@Override
	public int searchUser(Map<String, String> map) {
		return mapper.searchUser(map);
	}


	// 비밀번호 변경
	@Override
	public int updatePw(Map<String, Object> paramMap) {
		
		String encPw = bcrypt.encode((String) paramMap.get("newPw"));

		paramMap.put("encPw", encPw);
		
		return mapper.updatePw(paramMap);
	}
	
	
}
