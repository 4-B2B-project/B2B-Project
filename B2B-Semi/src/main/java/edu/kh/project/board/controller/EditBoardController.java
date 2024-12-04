package edu.kh.project.board.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("editBoard")
@Controller
@Slf4j
@SessionAttributes({ "loginMember" })
@RequiredArgsConstructor
public class EditBoardController {
	
	private final BoardService service;
	
	/** 게시판 등록 페이지 이동
	 * @param boardNo
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode,
			Model model) {
		
		return "board/boardInsert";
	}

	/** 게시글 작성
	 * @param boardCode
	 * @param inputBoard
	 * @param loginMember
	 * @param ra
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode,
							  @ModelAttribute Board inputBoard,
							  @SessionAttribute("loginMember") Member loginMember,
							  RedirectAttributes ra) throws Exception {
		
		// 1. boardCode, 로그인한 회원 번호를 inputBoard에 세팅
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		
		int boardNo = service.boardInsert(inputBoard);
		
		// 3. 서비스 결과에 따라 message, 리다이렉트 경로 지정
		String path = null;
		String message = null;
		
		if(boardNo > 0) {
			
			path = "/board/community/" + boardCode;	//	/board/1/2002 -> 상세 조회
			message = "게시글이 작성 되었습니다!";
			
		} else {
			
			path = "boardInsert";	//	/editBoard/1/insert
			message = "게시글 작성 실패";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	  
	/** 게시글 삭제
	 * @param boardCode
	 * @param boardNo
	 * @param cp
	 * @param paramMap
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@RequestMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete")
	public String boardDelete(@PathVariable("boardCode") int boardCode,
							  @PathVariable("boardNo") int boardNo,
							  @RequestParam(value="cp", required = false, defaultValue = "1") int cp,
							  @RequestParam Map<String, Object> paramMap,
							  @SessionAttribute("loginMember") Member loginMember,
							  RedirectAttributes ra) {
		
		// 데이터 세팅
		paramMap.put("boardCode", boardCode);
		paramMap.put("boardNo", boardNo);
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		int result = 0;
		
		if(loginMember.getMemberAuth() == 2) {
			// 관리자 권한 게시글 삭제
			result = service.adminBoardDelete(paramMap);
			
		} else {
			// 사용자 권한 게시글 삭제
			result = service.boardDelete(paramMap);
			
		}
		
		
		String path = null;
		String message = null;
		
		String searchInput = (String) paramMap.get("searchInput");
		
		if(result > 0) {
			// 삭제 성공시 검색조건 유지하고 첫 페이지로 이동
			path = String.format("/board/community/%d?cp=%d&searchType=%s&searchInput=%s", boardCode, 1, paramMap.get("searchType"), searchInput);
			message = "삭제 되었습니다!";
			
		} else {
			// 삭제 실패시 검색조건 유지한 게시글 상세정보로 이동
			path = String.format("/board/%d/%d?cp=%d&searchType=%s&searchInput=%s", boardCode, boardNo, cp, paramMap.get("searchType"), searchInput);
			message = "삭제 실패";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
	}
	
	/** 게시글 수정
	 * @param boardCode
	 * @param boardNo
	 * @param cp
	 * @param paramMap
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@RequestMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			@RequestParam Map<String, Object> paramMap,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra) {
		
		// 데이터 세팅
		paramMap.put("boardCode", boardCode);
		paramMap.put("boardNo", boardNo);
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		int result = service.boardUpdate(paramMap);
		
		String path = null;
		String message = null;
		
		String searchInput = (String) paramMap.get("searchInput");
		
		if(result > 0) {
			// 수정 성공시 검색조건 유지하고 첫 페이지로 이동
			path = String.format("/board/community/%d?cp=%d&searchType=%s&searchInput=%s", boardCode, 1, paramMap.get("searchType"), searchInput);
			message = "수정 되었습니다!";
			
		} else {
			// 수정 실패시 검색조건 유지한 게시글 상세페이지 이동
			path = String.format("/board/%d/%d?cp=%d&searchType=%s&searchInput=%s", boardCode, boardNo, cp, paramMap.get("searchType"), searchInput);
			message = "수정 실패";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
	}
	
}
