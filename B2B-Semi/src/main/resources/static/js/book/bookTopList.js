// 도서 목록 렌더링 함수
// html에 books 내용이 있을때 렌더링 처리 함수
function renderBookList() {
	const bookList = document.getElementById('bookList');
	bookList.innerHTML = '';

	// 순번 변수
	let i = 1;
	
	if(books != null) {
		
		books.forEach(book => {
			// div생성
			const bookCard = document.createElement('div');
			bookCard.className = 'book-card';
			
			// HTML 생성
			bookCard.innerHTML = `
	            <div class="rank-badge">${i}</div>
	            <img src="${book.coverUrl}" alt="${book.title}" class="book-cover">
	            <div class="book-info"
					data-bookId="${book.bookId}" data-title="${book.fullTitle}"
					data-coverUrl="${book.coverUrl}" data-author="${book.author}"
					data-rating="${book.customerReviewRank}" data-genres="${book.genres}"
					data-description="${book.description}" data-reviewCount="${book.reviewCount}"
					data-steamCount="${book.steamCount}" data-full-author="${book.fullAuthor}">
	                <h3 class="book-title">${book.title}
	                </h3>
	                <p class="book-author">${book.fullAuthor}</p>
	                <div class="book-stats">
	                    <div class="stat-item">
	                        <span class="stat-value">${book.customerReviewRank}</span>
	                        <span class="stat-label">평점</span>
	                    </div>
	                    <div class="stat-item">
	                        <span class="stat-value">${book.reviewCount}</span>
	                        <span class="stat-label">리뷰</span>
	                    </div>
	                </div>
	            </div>
	        `;
			bookList.appendChild(bookCard);
			i++;
		});
	}
}

// 초기 도서 목록 렌더링
renderBookList();

// .bookInfo 클래스를 가진 모든 DOM 요소를 선택
const bookInfoRows = document.querySelectorAll('.book-info');

// 선택된 요소들에 대해 반복 처리
bookInfoRows.forEach(row => {
	// 각 row에 클릭 이벤트 리스너 추가
	row.addEventListener('click', () => {
		const modal = new bootstrap.Modal(document.getElementById('bookDetailModal'));
		
		// 아래부터 row 클릭된 요소의 데이터를 가져와 세팅작업 진행
		
		// tr 요소에서 필요한 데이터 가져오기 (data-attributes 사용)
		const bookId = row.getAttribute('data-bookId');
		const bookTitle = row.getAttribute('data-title');
		const bookCover = row.getAttribute('data-coverUrl');
		const bookAuthor = row.getAttribute('data-full-author');
		const bookRating = row.getAttribute('data-rating');
		const bookGenres = row.getAttribute('data-genres');
		const bookDescription = row.getAttribute('data-description');
		const reviewCount = row.getAttribute('data-reviewCount');
		const steamCount = row.getAttribute('data-steamCount');


		// 모달 내 요소 업데이트
		document.querySelector('.book-detail-title').innerHTML = bookTitle;
		document.querySelector('.book-detail-cover').src = bookCover;
		document.querySelector('.book-detail-author').innerHTML = bookAuthor;
		document.querySelector('.book-detail-stats .stat-item:first-child span').textContent = bookRating;
		document.querySelector('.book-detail-stats .stat-item:nth-child(2) span').textContent = reviewCount;
		document.querySelector('.book-detail-stats .stat-item:last-child span').textContent = steamCount;
		document.querySelector('.avgScore').textContent = "평균 " + bookRating + " : 10.0";
		document.querySelector('.reviewCount').textContent = "총 " + reviewCount + "개의 리뷰";

		// bookId 저장
		document.querySelector('#selectBookId').value = bookId;
		
		// 찜 여부 조회
		isBookSteam(bookId);


		// 장르 업데이트
		const genreContainer = document.querySelector('.book-detail-genres');
		genreContainer.innerHTML = ''; // 기존 장르 제거
		bookGenres.split(',').forEach(genre => {

			// 태그 생성 및 class, text입히기
			const genreBadge = document.createElement('span');
			genreBadge.className = 'badge bg-primary me-2';
			genreBadge.textContent = genre;

			// 장르 태그 안에 자식으로 추가
			genreContainer.appendChild(genreBadge);
		});


		// 책 소개 업데이트
		document.querySelector('.book-synopsis-text').innerHTML = bookDescription;

		// 모달 보여주기
		modal.show();

		// 댓글 조회 비동기 함수
		selectReviewList(bookId);
	});
});

// 찜하기 구현
const steamBtn = document.querySelector("#steamBtn");
if(steamBtn != null) {
	steamBtn.addEventListener("click", () => {
		
		const bookId = document.getElementById("selectBookId").value;
		
		fetch("/book/steamBook", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(bookId)
		})
		.then(resp => resp.json())
		.then(result => {
			if(result == 1) {
				steamBtn.style.backgroundColor = "#4f46e5";
				steamBtn.style.color = "white";
				alert("찜 완료");
				
			} else if(result == 2) {
				steamBtn.style.backgroundColor = "white";
				steamBtn.style.color = "#4F46E5";
				alert("찜 취소")
				
			} else {
				alert("찜하기 오류 발생");
				
			}
			
		});
		
	});
	
}

// 찜 여부 조회
function isBookSteam(bookId) {
	
	if(loginMember != null) {
		memberNo = loginMember.memberNo;
		
		obj = {
			"memberNo" : memberNo,
			"bookId" : bookId
		};
		
		fetch("/book/isBookSteam"	, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(obj)
		})
		.then(resp => resp.json())
		.then(result => {
			console.log();
			if(result == 1) {
				steamBtn.style.backgroundColor = "#4f46e5";
				steamBtn.style.color = "white";
				
			} else {
				steamBtn.style.backgroundColor = "white";
				steamBtn.style.color = "#4F46E5";
				
			}
			 		
		});
	}
	
}

// 댓글 리뷰 목록 조회
function selectReviewList(bookId) {

	// 기존 html 비우기
	document.querySelector(".review-list").innerHTML = '';

	// 댓글 목록 비동기 get 방식 조회
	fetch(`/book/selectReviewList?bookId=${bookId}`, {
		method: "GET",
		headers: { "Content-Type": "application/json" }
	})
	.then(resp => resp.json())
	.then(result => {

		// 조회되는 댓글 목록이 있을때
		if (result.length > 0) {
			result.forEach(({ MEMBER_NICKNAME, COMMENT, STAR_POINT, WRITE_DATE, PROFILE_IMG }) => {
				// 별점은 따로 불러오기
				const starHTML = getStarHTML(STAR_POINT);
				
				if(PROFILE_IMG == null || PROFILE_IMG == 'undefined') {
					PROFILE_IMG = userDefaultIamge;
				}

				const reviewHTML = `
					<div class="review-item">
						<div class="review-header d-flex justify-content-between align-items-center mb-2">
							<div class="reviewer-info d-flex align-items-center">
								<img src="${PROFILE_IMG}" alt="User" class="reviewer-avatar">
								<div class="ms-2">
									<div class="reviewer-name">${MEMBER_NICKNAME}</div>
									<div class="review-date text-muted">${WRITE_DATE}</div>
								</div>
							</div>
							<div class="review-actions">
								<div class="review-rating mb-1">
									${starHTML}
								</div>
							</div>
						</div>
						<p class="review-text mb-2">${COMMENT}</p>
					</div>
	            `;

				// html 안에 대입
				document.querySelector(".review-list").innerHTML += reviewHTML;
			});

		} else {

			// 댓글이 없을때
			const reviewHTML = `
				<div class="review-item">
					<p class="review-text mb-2">작성된 리뷰가 없습니다.</p>
				</div>
            `;

			document.querySelector(".review-list").innerHTML += reviewHTML;
		}

	});

}

// 별개수 생성 html
function getStarHTML(starPoint) {
	let starPointHTML = '';

	// 별1개 짜리 생성
	for (let i = 1; i <= starPoint / 2; i++) {
		starPointHTML += `<i class="fas fa-star text-warning"></i>`;
	}

	// 별 반개짜리 생성
	if (starPoint % 2 != 0) {
		starPointHTML += `<i class="fas fa-star-half-alt text-warning"></i>`;
	}

	// 총 점수
	starPointHTML += `&nbsp;<span>${starPoint}점</span>`;

	return starPointHTML;
}
