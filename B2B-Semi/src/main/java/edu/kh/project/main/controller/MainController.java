package edu.kh.project.main.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.book.model.service.BookService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final BookService service;
	
	private final BoardService boardService;

	
	/** 메인 페이지
	 * @param model
	 * @return
	 */
	@RequestMapping("/") // "/" 요청 매핑
	public String mainPage(Model model) {
		
		// 도서 목록 조회
		List<Book> bookList = service.mainBookList();
		
		// 공지 게시글 상단 3개 조회
		List<Board> noticeList = boardService.selectMainNotice();
		
		// 자유 게시판 게시글 상단 3개 조회
		List<Board> communityList = boardService.selectMainCommnunity();

		// 데이터를 5개씩 그룹화
		// 2단계 그룹화
		// grid -> card
		List<List<Map<String, Object>>> groupedBooks = new ArrayList<>();

		if (bookList != null && !bookList.isEmpty()) {
			// 5개씩 그룹화
		    for (int i = 0; i < bookList.size(); i += 5) {
		    	// 현재 그룹의 종료 인덱스를 계산 (bookList의 크기를 초과하지 않도록 Math.min 사용)
		        int end = Math.min(i + 5, bookList.size());

		        // 부분 리스트를 Map으로 변환하여 추가
		        List<Map<String, Object>> group = new ArrayList<>();
		        for (Book book : bookList.subList(i, end)) {
		            Map<String, Object> bookMap = new HashMap<>();
		            bookMap.put("title", book.getTitle());
		            bookMap.put("author", book.getAuthor());
		            bookMap.put("rating", book.getCustomerReviewRank());
		            bookMap.put("reviewCount", book.getReviewCount());
		            bookMap.put("coverUrl", book.getCoverUrl());
		            group.add(bookMap);
		        }
		        
		        groupedBooks.add(group);
		    }
		}

		// 모델에 그룹화된 데이터를 추가
		model.addAttribute("bookList", groupedBooks);
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("communityList", communityList);
		return "common/main";
	}
	
	
	// LoginFilter -> loginError 리다이렉트
	// -> message 만들어서 메인페이지로 리다이렉트
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		
		ra.addFlashAttribute("message", "로그인 후 이용해 주세요");
		
		return "redirect:/";
		
	}
	
	
}
