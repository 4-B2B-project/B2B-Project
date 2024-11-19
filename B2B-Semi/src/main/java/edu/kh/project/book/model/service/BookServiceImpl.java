package edu.kh.project.book.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.book.model.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor=Exception.class)
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService{

	private final BookMapper mapper;
	
	@Override
	public int insertBook() {
		RestTemplate restTemplate = new RestTemplate();
	    String apiUrl = "https://www.aladin.co.kr/ttb/api/ItemList.aspx"
	                    + "?ttbkey=ttbeotmd12131437001"  // 발급받은 API 키
	                    + "&QueryType=Bestseller"
	                    + "&MaxResults=50"
	                    + "&start=1"
	                    + "&SearchTarget=Book"
	                    + "&output=js"
	                    + "&Version=20131101";
	    
	    // JSON 응답 받기
	    Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

	    // 결과 리스트 추출
	    List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("item");
	    List<Book> bookList = new ArrayList<>();
	    
	    for(int i = 0; i < items.size(); i++) {
	    	Map<String, Object> item = items.get(i);
	    	
	    	// categoryName 파싱
	        String categoryName = (String) item.get("categoryName");
	        String firstCategory = null;
	        String secondCategory = null;

	        if (categoryName != null) {
	            String[] categories = categoryName.split(">");
	            if (categories.length > 1) {
	                firstCategory = categories[1].trim(); // 1차 카테고리
	            }
	            if (categories.length > 2) {
	                secondCategory = categories[2].trim(); // 2차 카테고리
	            }
	        }
	        
	        log.debug("secondCategory : " + secondCategory);
	    	
	    	Book book = Book.builder()
	                .bookId((Integer) item.get("itemId")) // itemId를 Integer로 변환
	                .title((String) item.get("title")) // 제목
	                .isbn((String) item.get("isbn")) // ISBN (13자리)
	                .author((String) item.get("author")) // 저자
	                .publisher((String) item.get("publisher")) // 출판사
	                .pubDate((String) item.get("pubDate")) // 출판일
	                .description((String) item.get("description")) // 책 설명
	                .coverUrl((String) item.get("cover")) // 커버 이미지 URL
	                .firstCategory(firstCategory) // 1차 카테고리
	                .secondCategory(secondCategory) // 2차 카테고리
	                .updatedAt((String) item.get("updatedAt")) // 업데이트 시간
	                .customerReviewRank((Integer) item.get("customerReviewRank")) // 리뷰 점수
	                .build();
	    	
	    	mapper.insertBook(book);

	        bookList.add(book); // 리스트에 추가
	    	
	    }
	    
	    log.debug("bookList : " + bookList);
	    


	    // 데이터 저장
		return 0;
	}
	
}
