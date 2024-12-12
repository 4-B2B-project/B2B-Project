package edu.kh.project.member.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
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
	
	// 관리자 대쉬보드 홈
	@GetMapping("dashAdmin")
	public String dashAdmin(Model model, @SessionAttribute("loginMember") Member loginMember) {
		
		int bookCount = Adservice.bookCount();
		
		int memberCount = Adservice.memberCount();
		
		int boardCount = Adservice.boardCount();
		
		model.addAttribute("activeMenu", "dashAdmin");
		model.addAttribute("bookCount", bookCount);
		model.addAttribute("memberCount", memberCount);
		model.addAttribute("boardCount", boardCount);
		model.addAttribute("member", loginMember);
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
	
	// 관리자 프로필 사진 변경.
	@PostMapping("editProfile")
	public String profileImage(@RequestParam("profileImg") MultipartFile profileImg,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) throws Exception {
		
		int result = Adservice.profileImage(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0) {
			message = "프로필 사진이 변경되었습니다.";
		}
		else {
			message = "변경에 실패했습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:dashAdmin";
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
		
		// 검색 조건이 없는 경우 기본 조회.
		if(!paramMap.containsKey("key") || !paramMap.containsKey("search")) {
			map = Adservice.memberList(cp);
		}
		
		else {
			map = Adservice.memberSearchList(cp, paramMap);
		}
		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("memberList", map.get("memberList"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
	    model.addAttribute("delfl", paramMap.get("delfl"));
	    model.addAttribute("activeMenu", "memberManage");
	    
		return "adminBoard/memberManage";
	}
	
	// 회원 검색.
	@GetMapping("searchMember")
	public String searchMember(@RequestParam Map<String, Object> paramMap, Model model, @RequestParam(value="cp", required = false, defaultValue="1") int cp) {
		
		Map<String, Object> map = Adservice.searchMember(paramMap, cp);
		
		model.addAttribute("memberList", map.get("memberList"));
		model.addAttribute("pagination", map.get("pagination"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
	    model.addAttribute("delfl", paramMap.get("delfl"));
	    model.addAttribute("activeMenu", "memberManage");
		
		return "adminBoard/memberManage";
	}
	
	// 회원 정보 수정 페이지 이동.
	@GetMapping("updateMember")
	public String updateMember(@RequestParam("memberNo") int memberNo, Model model,
							@RequestParam(value = "key", required = false) String key,
				            @RequestParam(value = "search", required = false) String search,
				            @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
				            @RequestParam(value="delfl", required = false) String delfl) {
		
		Member selectedMember = Adservice.selectedMember(memberNo);
		
		String[] address = (selectedMember.getMemberAddress() != null) ? selectedMember.getMemberAddress().split("\\^\\^\\^") : new String[3];
		
		if (address.length == 3) {
			model.addAttribute("postcode", address[0]);
			model.addAttribute("address", address[1]);
			model.addAttribute("detailAddress", address[2]);
		}
		
		model.addAttribute("member", selectedMember);
		model.addAttribute("memberNo", memberNo);
		model.addAttribute("activeMenu", "memberManage");
		model.addAttribute("key", key);
		model.addAttribute("search", search);
		model.addAttribute("cp", cp);
		model.addAttribute("delfl", delfl);
		
		return "adminBoard/updateMember";
	}
	
	// 회원 정보 수정 (POST)
	@PostMapping("updateMember")
	public String updateInfo(RedirectAttributes ra,Member inputMember, 
							@RequestParam(value="memberNo", required =false, defaultValue="0") int memberNo,
							@RequestParam("memberAddress") String[] memberAddress,
						    @RequestParam(value = "key", required = false) String key,
						    @RequestParam(value = "search", required = false) String search,
						    @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
						    @RequestParam(value="delfl", required = false) String delfl,
						    Model model) {
		
		int result = Adservice.updateInfo(inputMember, memberAddress);
		
		if(key != null) {
			key = URLDecoder.decode(key, StandardCharsets.UTF_8);
			key = URLEncoder.encode(key, StandardCharsets.UTF_8);
			}
		
		if (search != null) {
	        search = URLDecoder.decode(search, StandardCharsets.UTF_8);
	        search = URLEncoder.encode(search, StandardCharsets.UTF_8);
	    }
		
		
		String message = null;
		
		String path = null;
		
		if(result > 0) {
			message = "회원 정보 수정 성공함.";
			
			path = String.format("/adminBoard/memberManage?cp=%d&key=%s&search=%s&delfl=%s", cp, key, search, delfl);
			
			model.addAttribute("cp", cp);
		}
		else {
			message = "정보 수정 실패...";
			path = "/adminBoard/updateMember";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}
	
	// 회원 추방/탈퇴 복구.
	@ResponseBody
	@PostMapping("updateStatus")
	public Map<String, Object> updateStatus(@RequestBody Map<String, Object> paramMap) {
		
		List<String> memberNos = (List<String>)paramMap.get("memberNos");
		String action = (String) paramMap.get("action");
		
		boolean updateY = action.equals("탈퇴");
		
		int result = Adservice.updateStatus(memberNos, updateY);
		
		Map<String, Object> map = new HashMap<>();
		
		if (result > 0) {
			map.put("success", true);
		}
		else {
			map.put("success", false);
			map.put("message", "업데이트 실패...");
	}
	
	return map;
		
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
		
		model.addAttribute("activeMenu", "bookManage");
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("bookList", map.get("bookList"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
	    model.addAttribute("delfl", paramMap.get("delfl"));
		
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
	    model.addAttribute("delfl", paramMap.get("delfl"));
	    model.addAttribute("activeMenu", "bookManage");
		
		return "adminBoard/bookManage";
		
	}

	// 도서 삭제/삭제 복구
	@ResponseBody
	@PostMapping("updateBookStatus")
	public Map<String, Object> updateBookStatus(@RequestBody Map<String, Object> paramMap) {
		
		List<String> bookList = (List<String>) paramMap.get("bookList");
		String action = (String) paramMap.get("action");
		
		boolean updateY = action.equals("삭제");
		int result = Adservice.updateBookStatus(bookList, updateY);
		
		Map<String, Object> map = new HashMap<>();
		
		if (result > 0) {
			map.put("success", true);
		}
		else {
			map.put("success", false);
			map.put("message", "업데이트 실패...");
		}
		
		return map;
		
	}
	
	// 선택한 도서 데이터 보내기.
	@GetMapping("updateBook")
	public Book updateBook(@RequestParam("bookId") int bookId, Model model) {
		
		Book book = Adservice.selectBookDetail(bookId);
		
		model.addAttribute("activeMenu", "bookManage");
		
		return book;
	}
	
	// 도서 수정 페이지.
	@GetMapping("updateBookPage/{bookId:[0-9]+}")
	public String updateBookPage(@PathVariable int bookId, Model model,
			@RequestParam(value="key", required = false) String key,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="delfl", required = false) String delfl,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp) {
		Book book = Adservice.selectBookDetail(bookId);
		
		model.addAttribute("book", book);
		model.addAttribute("key", key);
		model.addAttribute("search", search);
		model.addAttribute("cp", cp);
		model.addAttribute("delfl", delfl);
		model.addAttribute("activeMenu", "bookManage");
		
		return "adminBoard/updateBook";
	}
	
	// 도서 수정하기.
	@ResponseBody
	@PostMapping("updateBook")
	public Map<String, Object> updateBook(@RequestBody Book book, 
			@RequestParam(value="key", required = false) String key,
			@RequestParam(value="delfl", required = false) String delfl,
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
				ra.addAttribute("delfl", delfl);
			}
			else {
				map.put("success", false);
			}
			
		} catch (Exception e) {
			map.put("success", false);
		}
		
		return map;
		
	}
	
	// 도서 수정 취소.
	@GetMapping("cancelUpdateBook")
	public String cancelUpdateBook(@RequestParam(value="key", required = false) String key,
			@RequestParam(value="search, required = false") String search,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			@RequestParam(value="delfl", required = false) String delfl,
			RedirectAttributes ra) {
		
		ra.addAttribute("key", key != null ? key : "");
		ra.addAttribute("search", search != null ? search : "");
		ra.addAttribute("cp", cp);
		ra.addAttribute("delfl", delfl);
		
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
		
		model.addAttribute("activeMenu", "boardManage");
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
	    model.addAttribute("key", paramMap.get("key"));
	    model.addAttribute("search", paramMap.get("search"));
	    model.addAttribute("delfl", paramMap.get("delfl"));
		
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
	    model.addAttribute("delfl", paramMap.get("delfl"));
	    model.addAttribute("activeMenu", "boardManage");
		
		return "adminBoard/boardManage";
	}
	
	// 게시글 상세.
	@GetMapping("boardDetail/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardNo") int boardNo, Model model, RedirectAttributes ra,
			@RequestParam(value="key", required = false) String key,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			@RequestParam(value="delfl", required = false) String delfl) {
		
		Board board = Adservice.boardDetail(boardNo);
		
		if(board == null) {
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
			return "redirect:/adminBoard/boardManage";
		}
		
		model.addAttribute("board", board);
		model.addAttribute("key", key);
		model.addAttribute("search", search);
		model.addAttribute("cp", cp);
		model.addAttribute("delfl", delfl);
		model.addAttribute("activeMenu", "boardManage");
		return "adminBoard/boardDetail";
	}
	
	// 게시글 수정 버튼.
	@PostMapping("boardDetail/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable ("boardNo") int boardNo, 
			@RequestParam Map<String, Object> paramMap,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			@RequestParam(value="key", required = false) String key,
			@RequestParam(value="search" , required = false) String search,
			@RequestParam(value="delfl", required = false) String delfl,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra) {
	
		paramMap.put("boardNo", boardNo);
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		int result = Adservice.boardUpdate(paramMap);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			path = String.format("/adminBoard/boardManage?cp=%d&key=%s&search=%s&delfl=%s" , cp, key, search, delfl );
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
			RedirectAttributes ra,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			@RequestParam(value="delfl", required = false) String delfl,
			@RequestParam(value="key", required = false) String key,
			@RequestParam(value="search" , required = false) String search) {
		
		paramMap.put("boardNo", boardNo);
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		int result = Adservice.boardDelete(paramMap);
		
		String path = null;
		String message = null;
		
		if ( result > 0) {
			path = String.format("/adminBoard/boardManage?cp=%d&key=%s&search=%s&delfl=%s", 1, key, search, delfl);
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
	public Map<String, Object> updateBoardStatus(@RequestBody Map<String, Object> paramMap) {
		
		List<String> boardList = (List<String>)paramMap.get("boardList");
		String action = (String)paramMap.get("action");
		
		boolean updateY = action.equals("삭제");
		
		int result = Adservice.updateBoardStatus(boardList, updateY);

		Map<String, Object> map = new HashMap<>();
		
		if(result > 0) {
			map.put("success", true);
		}
		else {
			map.put("success", false);
			map.put("message", "업데이트 실패...");
		}
		
		return map;
		
	}
	
}