package edu.kh.project.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("adminBoard")
@SessionAttributes({"loginMember"})
@RequiredArgsConstructor
public class AdminController {

	private final AdminService Adservice;
	
	
	@GetMapping("dashAdmin")
	public String dashAdmin() {
		return "adminBoard/dashAdmin";
	}

	// 관리자 정보 수정 페이지
	@GetMapping("editInfo")
	public String adminEditInfo(@SessionAttribute("loginMember") Member loginMember, Model model) {
		
		String memberAddress = loginMember.getMemberAddress();
		
		if (memberAddress != null) {
			String[] arr = memberAddress.split("\\^\\^\\^");
			
			if (arr.length == 3) {
				model.addAttribute("postcode", arr[0]);
				model.addAttribute("address", arr[1]);
				model.addAttribute("detailAddress", arr[2]);
			}
			else {
				model.addAttribute("message", "주소 형식이 잘못 되었습니다.");
			}
		}
		
		return "adminBoard/editInfo";
	}
	
	// 관리자 정보 수정.
	@PostMapping("editInfo")
	public String editAdminInfo(Member inputMember, @SessionAttribute("loginMember") Member loginMember,
			@RequestParam("memberAddress") String[] memberAddress, RedirectAttributes ra) {
		
		inputMember.setMemberNo(loginMember.getMemberNo());
		
		int result = Adservice.editInfo(inputMember, memberAddress);
		
		String message = null;
		
		if (result > 0) {
			loginMember.setMemberNo(inputMember.getMemberNo());
			loginMember.setMemberId(inputMember.getMemberId());
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberBookCategory(inputMember.getMemberBookCategory());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
			
			message = "정보가 수정되었습니다.";
			
		}
		
		else {
			message = "수정에 실패했습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:dashAdmin";
	}
	
	// 회원 관리 페이지
	@GetMapping("memberManage")
	public String memberManage(Model model, @RequestParam(value="cp", required=false, defaultValue = "1") int cp, @RequestParam Map<String, Object> paramMap) {
		
		Map<String, Object> map;
		
		if(paramMap.get("key") == null || paramMap.get("search") == null) {
			map = Adservice.memberList(cp);
		}
		
		else {
			map = Adservice.memberSearchList(cp, paramMap);
		}
		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("memberList", map.get("memberList"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
		
	    log.debug("memberList :" + map.get("memberList"));
	    
		return "adminBoard/memberManage";
	}
	
	// 회원 검색.
	@GetMapping("searchMember")
	public String searchMember(@RequestParam Map<String, Object> paramMap, Model model) {
		
		List<Member> memberList = Adservice.searchMember(paramMap);
		
		log.debug("memberList :" + memberList); // map :{MEMBER_DEL_FL=N, ENROLL_DATE=2024년 11월 22일, MEMBER_ID=user03, MEMBER_NICKNAME=유저삼}

		model.addAttribute("memberList", memberList);
		
		return "adminBoard/memberManage";
	}
	
	// 회원 정보 수정 페이지 이동.
	@GetMapping("updateMember")
	public String updateMember(@RequestParam("memberNo") int memberNo, Model model) {
		
		Member selectedMember = Adservice.selectedMember(memberNo);
		
		String[] address = (selectedMember.getMemberAddress() != null) ? selectedMember.getMemberAddress().split("\\^\\^\\^") : new String[3];
		
		model.addAttribute("member", selectedMember);
		model.addAttribute("memberNo", memberNo);
		model.addAttribute("postcode", address[0]);
		model.addAttribute("address", address[1]);
		model.addAttribute("detailAddress", address[2]);
		
		return "adminBoard/updateMember";
	}
	
	// 회원 정보 수정 (POST)
	@PostMapping("info")
	public String updateInfo(RedirectAttributes ra,Member inputMember, @RequestParam(value="memberNo", required =false, defaultValue="0") int memberNo, @RequestParam("memberAddress") String[] memberAddress) {
		
		int result = Adservice.updateInfo(inputMember, memberAddress);
		
		log.debug("memberNo : " + memberNo);
		
		String message = null;
		
		if(result > 0) {
			message = "회원 정보 수정 성공함.";
			
		}
		else {
			message = "정보 수정 실패...";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:memberManage";
	}
	
	// 회원 추방/탈퇴 복구.
	@ResponseBody
	@PostMapping("updateStatus")
	public ResponseEntity<?> updateStatus(@RequestBody Map<String, Object> paramMap) {
		
		List<String> memberNos = (List<String>)paramMap.get("memberNos");
		String action = (String) paramMap.get("action");
		
		boolean updateY = action.equals("탈퇴");
		
		log.debug("memberNos :" + memberNos);
		
		int result = Adservice.updateStatus(memberNos, updateY);
		
		if(result > 0) {
			return ResponseEntity.ok(Map.of("success", true));
		}
		else {
			return ResponseEntity.ok(Map.of("success", false, "message", "업데이트 실패.."));
		}
		
	}
	
	// 도서 관리 페이지 이동.
	@GetMapping("bookManage")
	public String bookManage(Model model, @RequestParam(value="cp", required=false, defaultValue = "1") int cp, @RequestParam Map<String, Object> paramMap) {
		
		Map<String, Object> map;
		
		if(paramMap.get("key") == null || paramMap.get("search") == null) {
			map = Adservice.bookList(cp);
		}
		else {
			map = Adservice.bookSearchList(cp, paramMap);
		}
		
		log.debug("map :" + map);
		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("bookList", map.get("bookList"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
		
		return "adminBoard/bookManage";
	}
	
	// 도서 검색.
	@GetMapping("searchBook")
	public String searchBook(@RequestParam Map<String, Object> paramMap, Model model,
			@RequestParam(value="cp", required =false, defaultValue = "1") int cp) {
		
		Map<String, Object> map = Adservice.bookSearchList(cp, paramMap);
		
		model.addAttribute("bookList", map.get("bookList"));
		model.addAttribute("pagination", map.get("pagination"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
		
		return "adminBoard/bookManage";
		
	}
	
	// 도서 삭제/삭제 복구.
	@ResponseBody
	@PostMapping("updateBookStatus")
	public ResponseEntity<?> updateBookStatus(@RequestBody Map<String, Object> paramMap) {
		
		List<String> bookList = (List<String>)paramMap.get("bookList");
		String action = (String)paramMap.get("action");
		
		log.debug("bookList : " + bookList);
		
		boolean updateY = action.equals("삭제");
		
		int result = Adservice.updateBookStatus(bookList, updateY);
		
		if(result > 0) {
			return ResponseEntity.ok(Map.of("success", true));
		}
		else {
			return ResponseEntity.ok(Map.of("success", false, "message", "업데이트 실패..."));
		}
		
	}
	
	// 선택한 도서 데이터 보내기.
	@GetMapping("updateBook")
	public Book updateBook(@RequestParam("bookId") int bookId) {
		
		Book book = Adservice.selectBookDetail(bookId);
		
		return book;
	}
	
	// 도서 수정 페이지.
	@GetMapping("updateBookPage/{bookId:[0-9]+}")
	public String updateBookPage(@PathVariable int bookId, Model model) {
		Book book = Adservice.selectBookDetail(bookId);
		
		model.addAttribute("book", book);
		
		return "adminBoard/updateBook";
	}
	
	// 도서 수정하기.
	@ResponseBody
	@PostMapping("updateBook")
	public Map<String, Object> updateBook(@RequestBody Book book, 
			@RequestParam(value="key", required = false) String key,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			RedirectAttributes ra) {
		
		Map<String,Object> map = new HashMap<>();
		
		try {
			
			int result = Adservice.updateBook(book);
			
			if (result > 0) {
				map.put("success", true);
				ra.addAttribute("key", key != null ? key : "");
				ra.addAttribute("search", search != null ? search : "");
				ra.addAttribute("cp", cp);
			}
			else {
				map.put("success", false);
			}
			
		} catch (Exception e) {
			map.put("success", false);
		}
		
		log.debug("Key: " + key + ", Search: " + search + ", CP: " + cp);
		
		return map;
		
	}
	
	// 도서 수정 취소.
	@GetMapping("cancelUpdateBook")
	public String cancelUpdateBook(@RequestParam(value="key", required = false) String key,
			@RequestParam(value="search, required = false") String search,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			RedirectAttributes ra) {
		
		ra.addAttribute("key", key != null ? key : "");
		ra.addAttribute("search", search != null ? search : "");
		ra.addAttribute("cp", cp);
		
		log.debug("Key: " + key + ", Search: " + search + ", CP: " + cp);
		
		return "redirect:/adminBoard/bookManage";
	}
	
	// 게시글 관리 페이지.
	@GetMapping("boardManage")
	public String boardManage(Model model, @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
										@RequestParam Map<String, Object> paramMap) {
		
		Map<String, Object> map;
		
		if(paramMap.get("key") == null || paramMap.get("search") == null) {
			map = Adservice.boardList(cp);
		}
		else {
			map = Adservice.boardSearchList(cp, paramMap);
		}
		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
		
		return "adminBoard/boardManage";
	}
	
	// 게시글 검색.
	@GetMapping("searchBoardList")
	public String searchBoardList(@RequestParam Map<String, Object> paramMap, Model model,
									@RequestParam(value="cp", required=false, defaultValue = "1") int cp) {
		
		Map<String, Object> map = Adservice.boardSearchList(cp, paramMap);
		
		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("pagination", map.get("pagination"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
		
		return "adminBoard/boardManage";
	}
	
	// 게시글 상세.
	@GetMapping("boardDetail/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardNo") int boardNo, Model model, RedirectAttributes ra) {
		
		Board board = Adservice.boardDetail(boardNo);
		
		if(board == null) {
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
			return "redirect:/adminBoard/boardManage";
		}
		
		model.addAttribute("board", board);
		return "adminBoard/boardDetail";
	}
	
	// 게시글 수정 버튼.
	@PostMapping("boardDetail/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable ("boardNo") int boardNo, 
			@RequestParam Map<String, Object> paramMap,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra) {
	
		paramMap.put("boardNo", boardNo);
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		int result = Adservice.boardUpdate(paramMap);
		
		String path = null;
		String message = null;
		
//		String searchInput = (String) paramMap.get("searchInput");
		
		if(result > 0) {
			path = "/adminBoard/boardManage";
			message = "수정 완료.";
		}
		else {
			path = "/adminBoard/boardDetail";
			message = "수정 실패.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}
	
	// 게시글 삭제 버튼.
	@PostMapping("boardDetail/{boardNo:[0-9]+}/delete")
	public String boardDelete(@PathVariable("boardNo") int boardNo,
			@RequestParam Map<String, Object> paramMap,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra) {
		
		paramMap.put("boardNo", boardNo);
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		int result = Adservice.boardDelete(paramMap);
		
		String path = null;
		String message = null;
		
		if ( result > 0) {
			path = "/adminBoard/boardManage";
			message = "게시글 삭제 완료.";
		}
		else {
			path = "/boardDetail";
			message = "삭제 실패.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}
	
	// 게시글 삭제 / 삭제 복구.
	@ResponseBody
	@PostMapping("updateBoardStatus")
	public ResponseEntity<?> updateBoardStatus(@RequestBody Map<String, Object> paramMap) {
		
		List<String> boardList = (List<String>)paramMap.get("boardList");
		String action = (String)paramMap.get("action");
		
		log.debug("boardList : " + boardList);
		log.debug("Request paramMap: " + paramMap);
		log.debug("boardList: " + boardList);
		log.debug("action: " + action);
		
		boolean updateY = action.equals("삭제");
		
		int result = Adservice.updateBoardStatus(boardList, updateY);
		
		log.debug("updateY: " + updateY);
		log.debug("Update result: " + result);
		
		if(result > 0) {
			return ResponseEntity.ok(Map.of("success", true));
		}
		else {
			return ResponseEntity.ok(Map.of("success", false, "message", "업데이트 실패..."));
		}
		
	}
	
}