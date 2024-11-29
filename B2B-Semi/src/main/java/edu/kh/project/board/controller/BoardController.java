package edu.kh.project.board.controller;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("board")
@Controller
@Slf4j
@SessionAttributes({ "loginMember" })
@RequiredArgsConstructor
public class BoardController {

	private final BoardService service;
	
	
	/** 게시판 페이지
	 * @param model
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	@GetMapping("community/{boardCode:[0-9]+}")
	public String community(Model model, 
							@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
							@RequestParam Map<String, Object> paramMap,
							@PathVariable("boardCode") int boardCode) {
		
		Map<String, Object> map = new HashMap<>();
		
		if (!paramMap.isEmpty()) {
			paramMap.remove("cp"); // paramMap에서 cp 제거
		}

		if (paramMap.isEmpty()) {
			// 자유게시판 글 목록 조회
			map = service.communityList(cp);

		} else {
			// 자유게시판 글 목록 검색 조회
			map = service.communitySearchList(cp, paramMap);

		}
		
		
		// 데이터 전달
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		// 검색 데이터 전달
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("cp", cp);
		model.addAttribute("searchType", paramMap.get("searchType"));
		model.addAttribute("searchInput", paramMap.get("searchInput"));
		
		return "board/community";
	}
	
	
	/** 게시판 상세 페이지
	 * @param boardNo
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String detailCommunity(@PathVariable("boardCode") int boardCode,
								  @PathVariable("boardNo") int boardNo,
								  Board board,
								  Model model) {
		
		Board detailCommunity = service.detailCommunity(board);
		model.addAttribute("board", detailCommunity);
		
		return "board/detailCommunity";
	}
	
	/** 게시판 등록 페이지 이동
	 * @param boardNo
	 * @return
	 */
	@GetMapping("boardInsert/{boardCode:[0-9]+}")
	public String boardInsert(@PathVariable("boardCode") int boardCode,
			Model model) {
		
		return "board/boardInsert";
	}
	
	
}
