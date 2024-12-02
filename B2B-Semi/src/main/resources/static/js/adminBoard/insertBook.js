const searchBtn = document.querySelector(".search-button"); // 도서 관리 검색창
const srchBookList = document.querySelector("#srchBookList");
const searchInput = document.querySelector("#searchInput");
const saveBtn = document.querySelector("#saveBtn");
const isbn = document.querySelector("#isbnData");

// 검색 버튼 클릭시
searchBtn.addEventListener("click", () => {
	
	const inputDate = searchInput.value;
	
	if(inputDate == '') {
		alert("검색어를 입력해주세요.");
		searchInput.focus();
		return;
	}

	const obj = {
		bookTitle: inputDate
	}

	// 검색 도서 목록 API 조회
	fetch(`/insertBook/srchBookList`, {
		method: "POST",
		body: JSON.stringify(obj),
		headers: { "Content-Type": "application/json" }
	})
	.then(resp => resp.json())
	.then(result => {
		// 기존 값 비워두기
		const srchBookList = document.getElementById('srchBookList');
		srchBookList.innerHTML = "";

		result.forEach(book => {
			const bookCard = document.createElement('div');
			bookCard.className = 'book-card';
			bookCard.innerHTML = `
           <img src="${book.coverUrl}" 
                alt="${book.title}" 
                class="book-cover">
           <div class="book-title">${book.title}</div>
           <div class="book-author">${book.author}</div>
       `;
	   		// 상세 정보 출력 이벤트 추가
			bookCard.onclick = () => showBookDetail(book);

			// 도서결과 목록 동적으로 추가
			srchBookList.appendChild(bookCard);
		});

	});

})

// 해당 도서 상세정보 출력
function showBookDetail(book) {
	
	const bookDetailSection = document.getElementById('bookDetailSection');
    const bookDetailContent = document.getElementById('bookDetailContent');
			
	bookDetailContent.innerHTML = "";

    // 상세 정보 HTML 생성
    bookDetailContent.innerHTML = `
        <img class='book-cover' src="${book.coverUrl}" alt="${book.title}">
        <div class="book-detail-info">
            <h2>${book.title}</h2>
            <p><strong>저자 : </strong> ${book.author}</p>
            <p><strong>출판사 : </strong> ${book.publisher || '정보 없음'}</p>
            <p><strong>출판일 : </strong> ${book.pubDate || '정보 없음'}</p>
            <p><strong>장르 : </strong> ${book.firstCategory} , ${book.secondCategory}</p>
            <div class="book-description">
                <strong>도서 설명 : </strong>
                <p>${book.description || '상세 설명이 없습니다.'}</p>
            </div>
        </div>
    `;
	
	// 국제표준도서번호 저장
	isbn.value = book.isbn;
	

    // 상세 정보 섹션 표시
    bookDetailSection.style.display = 'block';
}

// 저장 버튼 클릭시
saveBtn.addEventListener("click", () => {

	const obj = {
		isbn : isbn.value
	}
	
	// 검색 도서 목록 API 조회
	fetch(`/insertBook/selectedInsertBook`, {
		method: "POST",
		body: JSON.stringify(obj),
		headers: { "Content-Type": "application/json" }
	})
	.then(resp => resp.text())
	.then(result => {

		if(result > 0) {
			alert("도서 등록이 완료되었습니다.");
			location.reload(true);
			
		} else if(result == -1) {
			alert("이미 등록된 도서입니다");
			
		} else {
			alert("도서 등록이 실패되었습니다.");
			
		} 
		
	});
		
})

// 엔터키 누르면 조회
searchInput.addEventListener("keyup", e => {
	if(e.key == "Enter"){ 
		searchBtn.click();
	}
})