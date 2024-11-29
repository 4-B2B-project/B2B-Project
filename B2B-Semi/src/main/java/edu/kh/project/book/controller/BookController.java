package edu.kh.project.book.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.book.model.service.BookService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("book")
@Controller
@RequiredArgsConstructor
@SessionAttributes({ "loginMember" })
@Slf4j
public class BookController {

	private final BookService service;

	/**
	 * 도서 목록 조회
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("bookList")
	public String bookList(Model model, @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@RequestParam Map<String, Object> paramMap) {

		Map<String, Object> map = null;

		if (!paramMap.isEmpty()) {
			paramMap.remove("cp"); // paramMap에서 cp 제거
		}

		if (paramMap.isEmpty()) {
			// 도서 목록 조회
			map = service.bookList(cp);

		} else {
			// 도서 목록 검색 조회
			map = service.bookSearchList(cp, paramMap);

		}
		
		// 데이터 전달
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("bookList", map.get("bookList"));

		// 검색 데이터 전달
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("searchType", paramMap.get("searchType"));
		model.addAttribute("searchInput", paramMap.get("searchInput"));
		model.addAttribute("genreFilter", paramMap.get("genreFilter"));
		model.addAttribute("ratingFilter", paramMap.get("ratingFilter"));

		return "book/bookList";
	}
	
	/**
	 * top20 목록 조회
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("topList")
	public String topList(Model model) {

		// 도서 랭크 top20 목록 조회
		List<Book> bookList = service.topList();
		model.addAttribute("bookList", bookList);

		return "book/bookTopList";
	}

	                 
	/** 장르별 랭킹 목록
	 * @param model
	 * @return
	 */
	@GetMapping("categoryList")
	public String categoryList(Model model) {

		List<Book> bookList = service.bookCategoryList();

		// 데이터를 5개씩 그룹화
		// 3단계 그룹화
		// swiper -> grid -> card 형식으로 
		// 전체 종류 -> 도서5개grid -> 5개의 도서내용
		List<List<List<Book>>> groupedBooks = new ArrayList<>(); 

		if (bookList != null && !bookList.isEmpty()) {
			
		    // 10개씩 그룹화
		    for (int i = 0; i < bookList.size(); i += 10) {
		    	// 현재 그룹의 종료 인덱스를 계산 (bookList의 크기를 초과하지 않도록 Math.min 사용)
		        int end = Math.min(i + 10, bookList.size());

		        // 10개씩 묶은 리스트
		        List<Book> group = new ArrayList<>();
		        
		        // bookList의 i부터 end까지의 부분 리스트를 가져옴
		        for (Book book : bookList.subList(i, end)) {
		            group.add(book);
		        }

		        // 10개로 나뉜 group을 5개씩 묶기 위한 하위 그룹 리스트 생성
		        List<List<Book>> subGroups = new ArrayList<>();
		        for (int j = 0; j < group.size(); j += 5) {
		        	// 현재 하위 그룹의 종료 인덱스를 계산 (group의 크기를 초과하지 않도록 Math.min 사용)
		            int subEnd = Math.min(j + 5, group.size());
		            
		            // 5개씩 나누어 subGroups에 추가
		            subGroups.add(group.subList(j, subEnd)); 
		        }

		        // 최종적으로 5개씩 나누어진 리스트를 추가
		        groupedBooks.add(subGroups);
		    }
		}

		// 모델에 그룹화된 데이터를 추가
		model.addAttribute("bookList", groupedBooks);

		return "book/bookCategoryList";
	}
	
	
	/** 장르 선택별 베스트 top10 페이지
	 * @param category
	 * @param model
	 * @return
	 */
	@GetMapping("bestCategoryList")
	public String bestCategoryList(@RequestParam(value="category", required = false, defaultValue = "") String category,
			Model model) {
		
		// 장르별 베스트 top10 페이지 장르 목록
		List<Map<String, Object>> map = service.selectCategoryList();
		model.addAttribute("map", map);
		
		// 선택된 장르가 있는 경우, 해당 장르의 Top10 도서 조회
	    if (category != null && !category.isEmpty()) {
	        List<Book> topBooks = service.selectCategortBestBook(category);
	        model.addAttribute("topBooks", topBooks);
	    }
		
		return "book/bestCategoryList";
	}
	
	/** 기간별 랭킹
	 * @param model
	 * @return
	 */
	@GetMapping("bookPeriodList")
	public String bookPeriodList(Model model) {
		
		// 월간 랭킹 도서 top10 조회
		List<Book> monthBookList = service.selectMonthPeriodList();
		
		// 연간 랭킹 도서 top10 조회
		List<Book> yearBookList = service.selectYearPeriodList();
		
		model.addAttribute("monthBookList", monthBookList);
		model.addAttribute("yearBookList", yearBookList);
		
		return "book/bookPeriodList";
	}
	
	/** 상세 기간별 랭킹
	 * @param model
	 * @return
	 */
	@GetMapping("bookDetailPeriodList")
	public String bookDetailPeriodList(@RequestParam(value = "startDay", required = false) String startDayParam,
	                                   @RequestParam(value = "endDay", required = false) String endDayParam,
	                                   @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
	                                   Model model) {
	    LocalDate endDay = LocalDate.now();
	    LocalDate startDay = endDay.minusDays(7);

	    // 요청 파라미터를 LocalDate로 변환
	    if (startDayParam != null && !startDayParam.isEmpty()) {
	        startDay = LocalDate.parse(startDayParam);
	    }
	    if (endDayParam != null && !endDayParam.isEmpty()) {
	        endDay = LocalDate.parse(endDayParam);
	    }

	    // 서비스에 전달할 데이터 구성
	    Map<String, Object> map = new HashMap<>();
	    map.put("startDay", startDay);
	    map.put("endDay", endDay);

	    // 서비스 호출 및 결과 처리
	    Map<String, Object> bookMap = service.bookDetailPeriodList(map, cp);

	    // 모델에 값 추가
	    model.addAttribute("startDay", startDay.toString());
	    model.addAttribute("endDay", endDay.toString());
	    model.addAttribute("pagination", bookMap.get("pagination"));
	    model.addAttribute("bookList", bookMap.get("bookList"));

	    return "book/bookDetailPeriodList";
	}


	
	/**
	 * 해당 도서 리뷰 목록 조회
	 * @param bookId
	 * @return List<Map<String, Object>>
	 */
	@ResponseBody
	@GetMapping("selectReviewList")
	public List<Map<String, Object>> selectReviewList(@RequestParam("bookId") int bookId) {
		return service.selectReviewList(bookId);
	}

	
	/**
	 * 책 리뷰 작성
	 * @param paramMap
	 * @return result
	 */
	@ResponseBody
	@PostMapping("insertBookReview")
	public int insertBookReview(@RequestBody Map<String, Object> paramMap,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		return service.insertBookReview(paramMap);
	}
	
	
	/** 선택된 장르 도서 top10 조회
	 * @param category
	 * @return List<Book>
	 */
	@ResponseBody
	@GetMapping("selectCategortBestBook")
	public List<Book> selectCategortBestBook(@RequestParam("category") String category) {
		return service.selectCategortBestBook(category);
	}
	
	
	/** 리뷰 수정
	 *  수정되는 리뷰 평점으로 해당 도서의 평점도 수정되야함
	 * @param paramMap
	 * @return
	 */
	@ResponseBody
	@PutMapping("updateBookReview")
	public int updateBookReview(@RequestBody Map<String, Object> paramMap) {
		return service.updateBookReview(paramMap);
	}
	
	
	/** 리뷰 삭제
	 *  삭제되는 리뷰 평점으로 해당 도서의 평점도 수정되야함
	 * @param paramMap
	 * @return
	 */
	@ResponseBody
	@DeleteMapping("deleteReview")
	public int deleteReview(@RequestBody Map<String, Object> paramMap) {
		return service.deleteReview(paramMap);
	}
	
	
	/** 찜하기
	 * @param paramMap
	 * @return
	 */
	@ResponseBody
	@PostMapping("steamBook")
	public int steamBook(@RequestBody int bookId,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		return service.steamBook(bookId, loginMember.getMemberNo());
	}
	
	/** 찜 여부 조회
	 * @param paramMap
	 * @return
	 */
	@ResponseBody
	@PostMapping("isBookSteam")
	public int isBookSteam(@RequestBody Map<String, Object> paramMap) {
		return service.isBookSteam(paramMap);
	}
	

}
