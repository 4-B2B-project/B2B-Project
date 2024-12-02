package edu.kh.project.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("member")
@SessionAttributes({"loginMember"})
@RequiredArgsConstructor
@Slf4j
public class MemberController {

	private final MemberService service;

	/** 로그인 페이지 이동
	 * @return
	 */
	@GetMapping("login")
	public String login() {
		return "/member/login";
	}
	
	/** 로그인 진행
	 * @param inputMember
	 * @param ra
	 * @param model
	 * @param saveId
	 * @param resp
	 * @return
	 */
	@PostMapping("login")
	public String login(/*@ModelAttribute*/ Member inputMember, 
						RedirectAttributes ra,
						Model model,
						@RequestParam(value="saveId", required = false) String saveId,
						HttpServletResponse resp ) {
		
		// 체크박스
		// - 체크가    된 경우 : "on"
		// - 체크가  안된 경우 : null
		
		// 로그인 서비스 호출
		Member loginMember = service.login(inputMember);
		
		// 로그인 실패 시
		if(loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		} else {
			
			// Session scope에 loginMember 추가
			model.addAttribute("loginMember", loginMember);
			// 1단계 : request scope 에 세팅됨
			// 2단계 : 클래스 위에 @SessionAttributes() 어노테이션 작성하여
			//		   session scope로 이동
			
			// **********Cookie************
			
			// 이메일 저장 
			
			// 쿠키 객체 생성 (K:V)
			Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
			// saveId=user01@kh.or.kr
			
			// 쿠키가 적용될 경로 설정
			// -> 클라이언트가 어떤 요청을 할 때 쿠키가 첨부될지 지정
			
			// ex)  "/"  :  IP 또는 도메인 또는 localhost
			//			   --> 메인페이지 + 그 하위 주소 모두 
			cookie.setPath("/");
			
			// 쿠키의 만료 기간 지정
			cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 (초 단위로 지정)
			
			// 응답 객체에 쿠키 추가 -> 클라이언트 전달
			resp.addCookie(cookie);
			
		}
		
		log.debug("loginMember" + loginMember);
		
		return "redirect:/"; // 메인페이지 재요청
	}
	
	/** 이메일 중복검사 (비동기 요청)
	 * @return
	 */
	@ResponseBody // 응답 본문(fetch)으로 돌려보냄
	@GetMapping("checkId")   // Get요청 /member/checkEmail 
	public int checkId(@RequestParam("memberId") String memberId) {
		return service.checkId(memberId);
	}
	
	/** 닉네임 중복 검사
	 * @return 중복 1, 아님 0
	 */
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}
	
	/** 회원가입전 약관동의 페이지
	 * @return
	 */
	@GetMapping("submit")
	public String submit() {
		return "/member/submit";
	}
	
	/** 회원가입 페이지 이동
	 * @return
	 */
	@GetMapping("signUp")
	public String signUp() {
		return "/member/signUp";
	}
	
	/** 회원 가입 
	 * @param inputMember : 입력된 회원 정보(memberEmail, memberPw, memberNickname, memberTel, 
	 * 						(memberAddress - 따로 배열로 받아서 처리))
	 * @param memberAddress : 입력한 주소 input 3개의 값을 배열로 전달 [우편번호, 도로명/지번주소, 상세주소]
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는 객체 
	 * @return
	 */
	@PostMapping("signup")
	public String signup(Member inputMember, 
						@RequestParam("memberAddress") String[] memberAddress,
						RedirectAttributes ra ) {
		
		// log.debug("inputMember : " + inputMember);
		
		// 회원가입 서비스 호출
		int result = service.signup(inputMember, memberAddress);
		
		String path = null;
		String message = null;
		
		if(result > 0) { // 성공 시
			message = inputMember.getMemberNickname() + "님의 가입을 환영 합니다!";
			path = "firstProfile";
			
			// 회원 정보를 RedirectAttributes에 추가
	        ra.addFlashAttribute("memberInfo", inputMember);
			
		} else { // 실패
			message = "회원 가입 실패...";
			path = "sigunup";
		}
		
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path; 
	}
	
	/** 로그아웃 진행
	 * @param status
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete();
		
		return "redirect:/";
	}
	
	/** 처음 프로필 페이지 이동
	 * @return
	 */
	@GetMapping("firstProfile")
	public String firstProfile(@ModelAttribute("memberInfo") Member member, Model model) {
	    model.addAttribute("member", member);

	    return "/member/firstProfile";
	}

	
}
