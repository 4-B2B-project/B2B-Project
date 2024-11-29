package edu.kh.project.myPage.controller;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.common.util.Pagination;
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

	/** 내정보 화면 이동
	 * @param loginMember
	 * @param model
	 * @return
	 */
	@GetMapping("info") 
	public String info(@SessionAttribute("loginMember") Member loginMember, Model model) {

		System.out.println("loginMember: " + loginMember);

		if (loginMember != null) {
			model.addAttribute("member", loginMember);
		}

		return "myPage/myPage-info";
	}

	/** 내 정보 수정 화면 이동
	 * @param loginMember
	 * @param model
	 * @return
	 */
	@GetMapping("editInfo") 
	public String editInfo(@SessionAttribute("loginMember") Member loginMember, Model model) {

		// 현재 로그인한 회원의 주소를 꺼내옴
		// 현재 로그인한 회원 정보 -> Session에 등록된 상태(loginMember)

		log.debug("loginMember : " + loginMember);

		String memberAddress = loginMember.getMemberAddress();
		// 13379^^^경기 성남시 수정구 분당수서로 1181-24^^^101호

		// 주소가 있을 경우에만 동작
		if (memberAddress != null) {

			// 구분자 "^^^" 를 기준으로
			// memberAddress 값을 쪼개어 String[]로 반환

			String[] arr = memberAddress.split("\\^\\^\\^");
			// 이스케이프 문자를 사이사이 넣어줘야 함

			// -> ["13379", "경기 성남시 수정구 분당수서로 1181-24", "101호"]
			// 0번 1번 2번 인덱스

			if (arr.length == 3) {
				model.addAttribute("postcode", arr[0]);
				model.addAttribute("address", arr[1]);
				model.addAttribute("detailAddress", arr[2]);
			} else {
				// 배열 길이가 부족한 경우, 처리할 메시지 또는 기본 값 설정
				model.addAttribute("message", "주소 형식이 잘못되었습니다.");
			}
		}

		return "myPage/myPage-editInfo";
	}

	
	/** 찜한 도서 목록 이동
	 * @param loginMember
	 * @param model
	 * @param ra
	 * @return
	 */
	@GetMapping("favBook") 
	public String favBook(@SessionAttribute("loginMember") Member loginMember, Model model,
			RedirectAttributes ra) { 

		int memberNo = loginMember.getMemberNo(); 

		// 찜한 도서 목록 가져오기
		List<Book> favoriteBooks = service.selectFavoriteBooks(memberNo); 

		// 모델에 데이터 추가
		model.addAttribute("favoriteBooks", favoriteBooks);

		if (favoriteBooks == null || favoriteBooks.isEmpty()) {
			model.addAttribute("favoriteBooks", favoriteBooks); // 빈 리스트도 전달 (뷰에서 처리 가능)
			model.addAttribute("message", "찜한 도서가 없습니다."); // 메시지 추가
		} else {
			model.addAttribute("favoriteBooks", favoriteBooks);
		}

		return "myPage/myPage-favBook";
	}

	
	/** 내가 작성한 게시글 목록 이동
	 * @param loginMember
	 * @param cp
	 * @param model
	 * @param paramMap
	 * @return
	 */
	@GetMapping("boardList") 
	public String boardList(
			@SessionAttribute("loginMember") Member loginMember,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap) {

		
		int memberNo = loginMember.getMemberNo();
		Map<String, Object> boardList = null;

		if (!paramMap.isEmpty()) {
			paramMap.remove("cp"); 
		}

		if (paramMap.isEmpty()) {
			// 검색 조건이 없을 경우
			// 게시글 목록 조회

			boardList = service.selectBoardList(memberNo, cp);

		} else {
			paramMap.put("memberNo", memberNo);
			// 검색 조건이 있을 경우
			// 게시글 목록 검색 조회
			log.debug("사용자가 작성한 게시글 검색 요청, 조건: {}", paramMap);
			boardList = service.searchBoardList(cp, paramMap);

		}

		// 데이터 전달
		model.addAttribute("pagination", boardList.get("pagination"));
		model.addAttribute("boardList", boardList.get("boardList"));

		// 검색 데이터 전달
		model.addAttribute("paramMap", paramMap); // 검색 조건 및 파라미터

		log.debug("paramMap: {}", paramMap);
		log.debug("boardList: {}", boardList);

		return "myPage/myPage-boardList";
	}

	
	/** 게시글 상세 정보 이동
	 * @param boardNo
	 * @param model
	 * @param loginMember
	 * @return
	 */
	@GetMapping("boardDetail")
	public String boardDetail(@RequestParam("boardNo") int boardNo, Model model,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {

		// 서비스 호출하여 게시글 번호에 해당하는 게시글 정보를 가져옴
		Board boardDetail = service.selectBoardDetail(boardNo);

		if (boardDetail == null) {
			return "redirect:/myPage/boardList"; // 게시글이 없을 경우 목록으로 리다이렉트
		}

		// 모델에 게시글 정보 추가
		model.addAttribute("boardDetail", boardDetail);

		// 게시글 상세보기 페이지로 이동
		return "myPage/myPage-boardDetail";
	}


	/** 댓글 목록 이동
	 * @param loginMember
	 * @param cp
	 * @param model
	 * @param paramMap
	 * @return
	 */
	@GetMapping("commentList") 
	public String commentList(@SessionAttribute("loginMember") Member loginMember,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap) {

		int memberNo = loginMember.getMemberNo();

		Map<String, Object> commentList = null;

		if (!paramMap.isEmpty()) {
			paramMap.remove("cp");
		}

		if (paramMap.isEmpty()) {
			// 검색 조건이 없을 경우
			// 게시글 목록 조회

			commentList = service.selectCommentList(memberNo, cp);

		} else {
			paramMap.put("memberNo", memberNo);
			// 검색 조건이 있을 경우
			// 게시글 목록 검색 조회
			log.debug("사용자가 작성한 댓글 검색 요청, 조건: {}", paramMap);
			commentList = service.commentSearchList(cp, paramMap);

		}

		// 데이터 전달
		model.addAttribute("pagination", commentList.get("pagination"));
		model.addAttribute("commentList", commentList.get("commentList"));
		// 검색 데이터 전달
		model.addAttribute("paramMap", paramMap); // 검색 조건 및 파라미터


		log.debug("paramMap: {}", paramMap);
		log.debug("commentList: {}", commentList);

		return "myPage/myPage-commentList";
	}

	
	
	

	/** 게시글 상세정보 수정
	 * @param boardNo
	 * @param model
	 * @param loginMember
	 * @return
	 */
	@GetMapping("/{boardNo}/update")
	public String updateBoardForm(@PathVariable("boardNo") int boardNo, Model model,
			@SessionAttribute("loginMember") Member loginMember) {

		// 게시글 상세 정보를 조회해서 수정 폼에 뿌려줌
		Board boardDetail = service.selectBoardDetail(boardNo);

		if (boardDetail == null) {
			return "redirect:/myPage/boardList"; // 게시글이 없는 경우 목록으로 이동
		}

		model.addAttribute("boardDetail", boardDetail);
		return "myPage/myPage-boardEdit"; // 수정 폼 페이지
	}
	
	// 댓글 목록 화면 이동 및  수정
	
	
	
	
	
	/** 비밀번호 변경 이동
	 * @return
	 */
	@GetMapping("changePw") 
	public String changePw() {
		return "myPage/myPage-changePw";
	}

	// ---------------------------- POST -------------------------------------

	/**
	 * 프로필 이미지 변경
	 * 
	 * @param profileImg
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping("info")
	public String profileImageInfo(@RequestParam("profileImg") MultipartFile profileImg,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) throws Exception {

		int result = service.profileImageInfo(profileImg, loginMember);

		String message = null;

		if (result > 0)
			message = "변경 성공!";
		else
			message = "변경 실패..";

		ra.addFlashAttribute("message", message);

		// POST 요청에 대한 처리 로직 작성
		// 예를 들어, 폼에서 전달된 데이터를 처리하고 DB에 업데이트

		return "redirect:info"; // POST 후 다시 GET 요청으로 리다이렉트 (정보 갱신 후 내 정보 페이지로 돌아감)
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
	@PostMapping("changePw") 
	public String changePw2(@SessionAttribute("loginMember") Member loginMember,
			@RequestParam Map<String, Object> paramMap, RedirectAttributes ra) {

		
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


	/** 내 정보 수정 화면 이동
	 * @param inputMember
	 * @param loginMember
	 * @param memberAddress
	 * @param ra
	 * @return
	 */
	@PostMapping("editInfo") // /myPage/changePw GET 방식 요청
	public String editInfo(Member inputMember, @SessionAttribute("loginMember") Member loginMember,
			@RequestParam("memberAddress") String[] memberAddress, RedirectAttributes ra

	) {

		
		inputMember.setMemberNo(loginMember.getMemberNo());

		// 회원 정보 수정 서비스
		int result = service.editInfo(inputMember, memberAddress);

		String message = null;

		if (result > 0) { // 수정 성공
			// loginMember 새로 세팅
			// 우리가 방금 바꾼 값으로 세팅

			// -> loginMember는 세션에 저장된 로그인한 회원 정보가 저장된 객체를 참조하고 있다!!

			// -> loginMember를 수정하면
			// 세션에 저장된 로그인한 회원 정보가 수정된다.

			// == 세션 데이터와 DB 데이터를 맞춤

			// 세션에 저장된 회원 정보 수정
			loginMember.setMemberNo(loginMember.getMemberNo());

			// 닉네임, 전화번호, 선호 장르, 주소, 회원번호
			loginMember.setMemberId(inputMember.getMemberId()); // 이름

			loginMember.setMemberNickname(inputMember.getMemberNickname()); // 닉네임

			loginMember.setMemberTel(inputMember.getMemberTel()); // 전화번호

			loginMember.setMemberBookCategory(inputMember.getMemberBookCategory()); // 선호 장르

			loginMember.setMemberAddress(inputMember.getMemberAddress()); // 주소

			message = "회원 정보 수정 성공!";

		} else { // 수정 실패
			message = "회원 정보 수정 실패";

		}

		ra.addFlashAttribute("message", message);

		return "redirect:info";

	}

	
	/** 게시글 상세 정보 수정
	 * @param boardNo
	 * @param inputBoard
	 * @param loginMember
	 * @return
	 */
	@PostMapping("/{boardNo}/update")
	@ResponseBody // 비동기 요청에 응답하기 위해 JSON 형태로 반환
	public Map<String, Object> boardUpdate(
			@PathVariable("boardNo") int boardNo, 
			@RequestBody Board inputBoard,
			@SessionAttribute("loginMember") Member loginMember) {

		// 1. 커맨드 객체(inputBoard)에 ,boardNo, memberNo 세팅

		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		// inputBoard => (제목, 내용, boardNo, memberNo)

		// 2. 게시글 수정 서비스 호출 후 결과 반환 받기
		int result = service.boardUpdate(inputBoard);

		// 3. 서비스 결과에 따라 JSON 형태의 응답 생성
		Map<String, Object> response = new HashMap<>();
		response.put("success", result > 0);
		response.put("message", result > 0 ? "게시글이 수정되었습니다" : "수정 실패");

		return response;
	}


	/** 게시글 상세 정보 삭제
	 * @param boardNo
	 * @param loginMember
	 * @param ra
	 * @return
	 * 
	 * 서버 리소스를 변경하거나 삭제하는 작업은 HTTP 표준에서 GET 방식이
	// 아닌 POST, PUT, DELETE 같은 메서드로 처리하도록 권장
	 */
	@RequestMapping(value ="/{boardNo}/delete", method = RequestMethod.POST ) 
	@ResponseBody // 비동기 요청에 응답하기 위해 JSON 형태로 반환
	public Map<String, Object> boardDelete(
						@PathVariable("boardNo") int boardNo,
						@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) {

		
		Map<String, Integer> map = new HashMap<>();
		
		map.put("boardNo", boardNo);
		map.put("memberNo", loginMember.getMemberNo());

		int result = service.boardDelete(map);

		

		 // JSON 응답 생성
	    Map<String, Object> response = new HashMap<>();
	    response.put("success", result > 0);
	    response.put("message", result > 0 ? "게시글이 삭제되었습니다." : "게시글 삭제에 실패했습니다.");

	    return response;

	}

}
