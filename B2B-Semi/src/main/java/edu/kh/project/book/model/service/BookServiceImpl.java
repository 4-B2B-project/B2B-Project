package edu.kh.project.book.model.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.book.model.mapper.BookMapper;
import edu.kh.project.common.util.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor=Exception.class)
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService{

	private final BookMapper mapper;
	
	
	// 메인페이지 도서 목록 조회
	@Override
	public List<Book> mainBookList() {
		return mapper.mainBookList();
	}
	
	
	// 도서 등록 (차후 사용예정)
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

	
	// 도서 검색 (차후 사용예정)
	@Override
	public List<Book> srchBookList(String title) {
		
		RestTemplate restTemplate = new RestTemplate();
	    String apiUrl = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx"
	                    + "?ttbkey=ttbeotmd12131437001"  // 발급받은 API 키
	                    + "&Query=" + title
	                    + "&QueryType=Title"
	                    + "&MaxResults=10"
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
	    	

	        bookList.add(book); // 리스트에 추가
	    	
	    }
	    
	    log.debug("bookList : " + bookList);
		
		return bookList;
	}


	// 도서 목록 조회
	@Override
	public Map<String, Object> bookList(int cp) {
		
		// 도서 목록 개수 조회
		int bookCount = mapper.bookCount();
		
		// 페이지네이션 진행
		Pagination pagination = new Pagination(cp, bookCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Book> bookList = mapper.bookList(rowBounds); 
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("bookList", bookList);
		
		return map;
	}
	
	
	// 도서 목록 검색 조회
	@Override
	public Map<String, Object> bookSearchList(int cp, Map<String, Object> paramMap) {
		
		// 평점 선택이 안 비어 있을때
		if(paramMap.get("ratingFilter") != "" && paramMap.get("ratingFilter") != null) {
			
			// int 형 변환후 해당 점수대 조회하기 위한 작업
			int ratingFilter = Integer.parseInt((String) paramMap.get("ratingFilter"));
			int nextRatingFilter = ratingFilter + 1;
			paramMap.put("nextRatingFilter", nextRatingFilter);
			
		}
		
		// 도서 목록 개수 조회
		int bookCount = mapper.bookSearchCount(paramMap);
		
		// 페이지 네이션 진행
		Pagination pagination = new Pagination(cp, bookCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Book> bookList = mapper.bookSearchList(rowBounds, paramMap); 
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("bookList", bookList);
		
		return map;
	}
	
	
	// 해당 도서 리뷰 목록 조회
	@Override
	public List<Map<String, Object>> selectReviewList(int bookId) {
		return mapper.selectReviewList(bookId);
	}


	// 책 리뷰 작성
	@Override
	public int insertBookReview(Map<String, Object> paramMap) {
		
		// 맵 객체 생성
	    Map<String, Object> map = new HashMap<>();
	    Map<String, Object> updateMap = new HashMap<>();

	    // 해당 map에 유저번호, 도서번호 대입
	    map.put("memberNo", paramMap.get("memberNo"));
	    map.put("bookId", paramMap.get("bookId"));

	    // 해당 유저가 해당 도서번호에 작성한 리뷰가 있는지 확인
	    int result = mapper.selectReviewMember(map);

	    // 리뷰가 1개 이상 있을 경우
	    if (result > 0) {
	        // 이미 리뷰를 작성했으므로, 0 반환
	        return 0;
	        
	    } else {

            // 기존 도서 평점과 리뷰 수 조회
            Map<String, Object> ratingMap = new HashMap<>();
            ratingMap.put("bookId", paramMap.get("bookId"));
            
            // 기존 도서 평점과 리뷰 수 가져오기
            Map<String, Object> bookRatingData = mapper.getBookRatingData(ratingMap);
            
            // 기존 리뷰 수 (REVIEWCOUNT가 null일 수 있으므로 0으로 처리)
            int currentReviewCount = bookRatingData.containsKey("REVIEWCOUNT") 
                                      ? ((BigDecimal) bookRatingData.get("REVIEWCOUNT")).intValue() 
                                      : 0;
            
            // 기존 평점 (AVERAGERATING이 null일 수 있으므로 0으로 처리)
            double currentAvgRating = bookRatingData.containsKey("AVERAGERATING")
                                       ? ((BigDecimal) bookRatingData.get("AVERAGERATING")).doubleValue()
                                       : 0.0;
            
            // 새로운 별점 (현재 리뷰의 별점)
            int newStarPoint = (int) paramMap.get("starPoint");
            double currentStarPoint = newStarPoint;

            // 첫 번째 리뷰일 경우, 새로운 평점은 기존 별점과 동일
            float newAvgRating = 0;
            if (currentReviewCount == 0) {
                // 첫 리뷰일 경우 새로운 평균은 입력된 별점 그대로 반영
                newAvgRating = newStarPoint;
                
            } else {
                // 새로운 평균 계산
                newAvgRating = (float) (((currentAvgRating * currentReviewCount) + currentStarPoint) / (currentReviewCount + 1));
                
            }

            // 평점 업데이트 정보 준비
            updateMap.put("bookId", paramMap.get("bookId"));
            updateMap.put("averageRating", newAvgRating);

            // 도서 평점 업데이트
            mapper.updateBookRating(updateMap);
            
            	// 리뷰가 없을 경우 insert 진행
	        int insertResult = mapper.insertBookReview(paramMap);
    	        
	        if(insertResult > 0) {
	        	return insertResult;
 	        	
	        } else {
	        	return 0;
	        	
	        }
	    }
		
	}

	
	// top20 도서 목록 조회
	@Override
	public List<Book> topList() {
		return mapper.topList();
	}


	// 장르별 랭킹 목록 조회
	@Override
	public List<Book> bookCategoryList() {
		return mapper.bookCategoryList();
	}


	// 장르별 베스트 top10 페이지 장르 목록
	@Override
	public List<Map<String, Object>> selectCategoryList() {
		return mapper.selectCategoryList();
	}
	
	
	// 선택된 장르 도서 top10 조회
	@Override
	public List<Book> selectCategortBestBook(String category) {
		return mapper.selectCategortBestBook(category);
	}


	// 월간 도서 랭킹 top10 조회
	@Override
	public List<Book> selectMonthPeriodList() {
		return mapper.selectMonthPeriodList();
	}


	// 연간 도서 랭킹 top10 조회
	@Override
	public List<Book> selectYearPeriodList() {
		return mapper.selectYearPeriodList();
	}


	// 상세 기간별 랭킹 조회
	@Override
	public Map<String, Object> bookDetailPeriodList(Map<String, Object> paramMap, int cp) {
		
		// 도서 목록 개수 조회
		int bookCount = mapper.bookDetailPeriodListCount(paramMap);
		
		// 페이지 네이션 진행
		Pagination pagination = new Pagination(cp, bookCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Book> bookList = mapper.bookDetailPeriodList(rowBounds, paramMap); 
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("bookList", bookList);
		
		return map;
		
	}


	// 리뷰 수정
	@Override
	public int updateBookReview(Map<String, Object> paramMap) {
		Map<String, Object> updateMap = new HashMap<>();
		
		// 기존 도서 평점과 리뷰 수 조회
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("bookId", paramMap.get("bookId"));
		
        // 기존 도서 평점과 리뷰 수 가져오기
		Map<String, Object> bookRatingData = mapper.getBookRatingData(ratingMap);
        
		// 기존 평균과 리뷰 수 가져오기
		BigDecimal avgRatingBigDecimal = (BigDecimal) bookRatingData.get("AVERAGERATING");
		double currentAvgRating = avgRatingBigDecimal.doubleValue();
		
		BigDecimal reviewCountBigDecimal = (BigDecimal) bookRatingData.get("REVIEWCOUNT");
		int currentReviewCount = reviewCountBigDecimal.intValue();

		// 기존 별점 가져오기 (수정 전 별점)
		int previousStarPoint = (int) paramMap.get("previousStarPoint");

		// 새로운 별점 가져오기 (수정 후 별점)
		int newStarPoint = (int) paramMap.get("starPoint");

		// 기존 합계 계산
		double currentTotalRating = currentAvgRating * currentReviewCount;

		// 새로운 합계 계산 (기존 합계에서 이전 별점을 빼고, 새로운 별점을 더함)
		double newTotalRating = currentTotalRating - previousStarPoint + newStarPoint;

		// 새로운 평균 계산
		float newAvgRating = (float) (newTotalRating / currentReviewCount);

		// 데이터 세팅
		updateMap.put("bookId", paramMap.get("bookId"));
		updateMap.put("averageRating", newAvgRating);

		// 도서 평점 수정
		mapper.updateBookRating(updateMap);
		
		// 리뷰 수정 진행
		int result = mapper.updateBookReview(paramMap);
		
		if(result > 0) {
			return result;
			
		} else {
			return 0;
			
		}
		
	}


	// 리뷰 삭제
	@Override
	public int deleteReview(Map<String, Object> paramMap) {
		Map<String, Object> updateMap = new HashMap<>();
		
		// 기존 도서 평점과 리뷰 수 조회
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("bookId", paramMap.get("bookId"));
		
        // 기존 도서 평점과 리뷰 수 가져오기
		Map<String, Object> bookRatingData = mapper.getBookRatingData(ratingMap);
        
		// bookRatingData에서 "AVERAGERATING" 값을 BigDecimal로 가져옴
		BigDecimal avgRatingBigDecimal = (BigDecimal) bookRatingData.get("AVERAGERATING");

		// BigDecimal을 double로 변환
		double currentAvgRating = avgRatingBigDecimal.doubleValue();
        
        // 기존 리뷰 수
		BigDecimal reviewCount = (BigDecimal) bookRatingData.get("REVIEWCOUNT");
		int currentReviewCount = reviewCount.intValue();
        
        // 새로운 별점 (현재 리뷰의 별점)
        int newStarPoint = (int) paramMap.get("starPoint");
        double currentStarPoint = newStarPoint; 
        
        // 새로운 평균 계산
        // 새로운 평균= (기존 평균×기존 리뷰 수)-새로운 별점 / (기존 리뷰 수)
        // 0일 경우를 대비해서 미리 기존 리뷰 수를 계산한다
        int updateReviewCount = currentReviewCount - 1;
        
        // 별점 변수 선언
        float newAvgRating;
        
        if(updateReviewCount > 0) {
        	// 기존 리뷰 수가 1개 이상으로 있을때
        	newAvgRating = (float) (((currentAvgRating * currentReviewCount) - currentStarPoint) / (updateReviewCount));
        	
        } else {
        	// 기존 리뷰 수가 0개일때 별점은 0점으로
        	newAvgRating = 0;
        }
        
        // 데이터 세팅
        updateMap.put("bookId", paramMap.get("bookId"));
        updateMap.put("averageRating", newAvgRating);
        
        // 도서 평점 수정
        int resultPoint = mapper.updateBookRating(updateMap);
        
        if(resultPoint > 0) {
        
	        int result = mapper.deleteReview(paramMap);
	        return result;
		
		} else {
			return 0;
		
		}
		
	}


	// 찜하기
	@Override
	public int steamBook(int bookId, int memberNo) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("bookId", bookId);
		map.put("memberNo", memberNo);
		
		// 찜여부 확인
		int count = mapper.selectSteamBook(map);
		int result = 0;
		
		if(count > 0) {
			// 찜 취소 데이터 삭제
			result = mapper.deleteSteamBook(map) + 1;
			
		} else {
			// 찜 하기 데이터 등록
			result = mapper.insertSteamBook(map);
		}
		
		return result;
	}


	// 찜 여부 조회
	@Override
	public int isBookSteam(Map<String, Object> paramMap) {
		return mapper.selectSteamBook(paramMap);
	}
	
}
