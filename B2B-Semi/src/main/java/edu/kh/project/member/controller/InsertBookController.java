package edu.kh.project.member.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.book.model.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("insertBook")
@RequiredArgsConstructor
public class InsertBookController {

	private final BookService service;
	
	/** 도서 추가 페이지.
	 * @return
	 */
	@GetMapping("addBook")
	public String addBook(Model model) {
		
		model.addAttribute("activeMenu", "bookManage");
		
		return "/adminBoard/insertBook";
	}
	
	/** 도서 검색API
	 * @param book
	 * @return
	 */
	@ResponseBody
	@PostMapping("srchBookList")
	public List<Book> srchBookList(@RequestBody Map<String, Object> book) {
	
		// 입력한 제목 도서 목록 검색
		String title = (String) book.get("bookTitle");
		List<Book> bookList = service.srchBookList(title);
		
		return bookList;
	}

	/** 선택된 도서 등록
	 * @param book
	 * @param ra
	 * @return
	 */
	@ResponseBody
	@PostMapping("selectedInsertBook")
	public int selectedInsertBook(@RequestBody Map<String, Object> book,
								  RedirectAttributes ra) {
		
		// 해당도서의 국제표준도서번호 가져오기
		String isbn = (String) book.get("isbn");
		
		// 등록된 도서가 있는지 조회
		int srchBook = service.srchBook(isbn);
		
		// 반환값 변수
		int result = 0;
		
		// 등록된 도서가 있을경우
		if(srchBook > 0) {
			// 등록된 도서가 있다는 구분값 -1 대입
			result = -1;
			
		} else {
			// 등록된 도서가 없을경우 해당 도서 등록
			result = service.selectedInsertBook(isbn);
			
		}
		
		return result;
	}
		
}
