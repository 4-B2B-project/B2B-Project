package edu.kh.project.myPage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@RestController // @ResponseBody 계속 설정해줄 필요 없음

//fetch - 비동기 요청
// "/myPage" 요청이 오면 해당 컨트롤러에서 잡아서 처리함
// @ResponseBody를 매번 메서드에 추가

@SessionAttributes({ "loginMember" })
@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

	private final MyPageService service;

	/**
	 * @param loginMember : 세션에 존재하는 loginMember를 얻어와 매개변수에 대입
	 * @return
	 */
	// 내정보 화면 이동
	@GetMapping("info") // /myPage/info GET 방식 요청
	public String info() {
		return "myPage/myPage-info";
	}

	
	
	// 찜한 도서 목록 이동
	@GetMapping("favBook") // /myPage/favBook GET 방식 요청
	public String favBook(
				
				Model model,
				RedirectAttributes ra) { /*@SessionAttribute("loginMember") Member loginMember*/
		
		//int memberNo = loginMember.getMemberNo(); // 로그인한 회원 번호
		
		// 찜한 도서 목록 가져오기
		List<Book> favoriteBooks = service.selectFavoriteBooks(); //memberNo
		
		// 모델에 데이터 추가
		model.addAttribute("favoriteBooks", favoriteBooks);
		
		String message = null;
		
		

		return "myPage/myPage-favBook";
	}

	
	
	// 게시글 목록 이동
	@GetMapping("boardList") // /myPage/boardList GET 방식 요청
	public String boardList() {
		return "myPage/myPage-boardList";
	}

	// 댓글 목록 이동
	@GetMapping("commentList") // /myPage/commentList GET 방식 요청
	public String commentList() {
		return "myPage/myPage-commentList";
	}

	// 비밀번호 변경 이동
	@GetMapping("changePw") // /myPage/changePw GET 방식 요청
	public String changePw() {
		return "myPage/myPage-changePw";
	}

	// 내 정보 수정 화면 이동
	@GetMapping("editInfo") // /myPage/changePw GET 방식 요청
	public String editInfo() {
		return "myPage/myPage-editInfo";
	}

	// ---------------------------- POST -------------------------------------

	/**
	 * 내정보 수정
	 * 
	 * @param inputMember   : 커맨드 객체(@ModelAttribute 생략된 상태) 제출된 회원 닉네임, 전화번호, 주소
	 * @param loginMember   : 로그인한 회원 정보 (회원 번호 사용할 예정)
	 * @param memberAddress : 주소만 따로 받은 String[]
	 * @param ra            : 리다이렉트 시 request scope로 message같은 데이터 전달
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo() {

		// POST 요청에 대한 처리 로직 작성
		// 예를 들어, 폼에서 전달된 데이터를 처리하고 DB에 업데이트

		return "redirect:/myPage/info"; // POST 후 다시 GET 요청으로 리다이렉트 (정보 갱신 후 내 정보 페이지로 돌아감)
	}

	/**
	 * 찜한 도서 목록 출력
	 * 
	 * @return
	 */
	@PostMapping("favBook")
	public String uploadFavbook() {

		return null;

	}

	/**
	 * 내가 작성한 게시글 목록 출력
	 * 
	 * @return
	 */
	@PostMapping("boardList")
	public String uploadBoardList() {

		return null;

	}

	/**
	 * 내가 작성한 댓글 목록 출력
	 * 
	 * @return
	 */
	@PostMapping("commentList")
	public String uploadCommentList() {

		return null;

	}

	/**
	 * 비밀번호 변경
	 * 
	 * @param paramMap    : 모든 파라미터 맵으로 저장
	 * @param loginMember : 세션에 등록된 현재 로그인한 회원 정보
	 * @return ra : 리다이렉트 시 request scope 로 메시지 전달
	 */
	@PostMapping("changePw") // /myPage/changePw POST 요청 매핑
	public String changePw2(
			@SessionAttribute("loginMember") Member loginMember,
			@RequestParam Map<String, Object> paramMap,
			RedirectAttributes ra
			) {

		// 로그인한 회원의 번호
		int memberNo = loginMember.getMemberNo();

		// 회원 비밀번호 수정
		// 현재 + 새 + 회원번호를 서비스 전달
		int result = service.changePw(paramMap, memberNo);

		String path = null;
		String message = null;

		if (result > 0) {
			// 변경 성공 시
			// 메시지 " 비밀번호 변경"
			// 리다이렉트 /myPage/info

			message = "비밀번호 변경 성공!!!";
			path = "/myPage/info";

		} else {
			// 변경 실패시
			// 메시지 " 비밀번호 변경 실패"
			// 리다이렉트 /myPage/changePw

			message = "현재 비밀번호가 일치하지 않습니다.";
			path = "/myPage/changePw";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;

	}

	// 내 정보 수정 화면 이동
	@PostMapping("editInfo") // /myPage/changePw GET 방식 요청
	public String editInfo(Member inputMember,
			@SessionAttribute("loginMember") Member loginMember,
			@RequestParam("memberAddress") String[] memberAddress,
			RedirectAttributes ra
			
			) {
		
		// inputMember에 현재 로그인한 회원의 번호를 꺼내옴
		inputMember.setMemberNo(loginMember.getMemberNo());
		
		// 회원 정보 수정 서비스
		int result = service.editInfo(inputMember,memberAddress);
		
		String message = null;
		
		if(result > 0) {	// 수정 성공
			// loginMember 새로 세팅
			// 우리가 방금 바꾼 값으로 세팅
						
			// -> loginMember는 세션에 저장된 로그인한 회원 정보가 저장된 객체를 참조하고 있다!! 
						
			// -> loginMember를 수정하면
			// 세션에 저장된 로그인한 회원 정보가 수정된다.
						
			// == 세션 데이터와 DB 데이터를 맞춤
						
			// 세션에 저장된 회원 정보 수정
			loginMember.setMemberNo( loginMember.getMemberNo());
						
			
			// 회원 이름(ID), 닉네임, 전화번호, 주소, 회원번호
			loginMember.setMemberId(inputMember.getMemberId()); // 이름
			
			loginMember.setMemberNickname(inputMember.getMemberNickname()); // 닉네임
						
			loginMember.setMemberTel(inputMember.getMemberTel()); // 전화번호
						
			loginMember.setMemberAddress(inputMember.getMemberAddress()); // 주소
			
		} else {			// 수정 실패
			message = "회원 정보 수정 실패";
			
		}
		
		ra.addFlashAttribute("message",message);
		
	
		
		return "redirect:info";
	}

}
