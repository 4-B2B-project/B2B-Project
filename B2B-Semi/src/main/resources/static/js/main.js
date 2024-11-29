const getCookie = (key) => {

	const cookies = document.cookie; // "K=V; K=V; ... "

	const cookieList = cookies.split("; ") // ["K=V", "K=V"...]
		.map(el => el.split("=")); // ["K", "V"]...


	const obj = {}; // 비어있는 객체 선언

	for (let i = 0; i < cookieList.length; i++) {
		const k = cookieList[i][0]; // key 값
		const v = cookieList[i][1]; // value 값
		obj[k] = v;
	}

	return obj[key]; // 매개변수로 전달받은 key와

}

const insertBook = null;

if (insertBook != null) {
	insertBook.addEventListener("click", e => {

		fetch("/book/insertBook", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
		})
			.then(resp => resp.text())
			.then(result => {

				alert("success");

			});

	});

}

const srchBookList = document.querySelector("#srchBookList");

if(srchBookList != null) {
	
	srchBookList.addEventListener("click", e => {
		
		const title = document.querySelector("#bookTitle");
		const bookList = document.querySelector("#bookList");
		bookList.innerHTML = "";

		fetch("/book/srchListBook", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: title.value
		})
		.then(resp => resp.json())
		.then(result => {

			if (result.length === 0) {
				const tr = document.createElement("tr");
				tr.innerHTML = `<td>해당 제목의 책이 없습니다</td>`;
				memberList.append(tr);
				return;
			}

			// 한 줄씩 회원 정보 출력
			for (let i = 0; i < result.length; i++) {
				let tr = document.createElement("tr");

				let bookTitle = document.createElement("td");
				bookTitle.append(result[i].title);
				
				let bookAuthor = document.createElement("td");
				bookAuthor.append(result[i].author);
				
				// 책 표지 (Cover) 이미지 생성
				let bookCoverTd = document.createElement("td"); // 이미지가 들어갈 칸
				let bookCoverImg = document.createElement("img"); // 이미지 태그 생성
				bookCoverImg.src = result[i].coverUrl; // 이미지 경로 설정
				bookCoverImg.alt = "Book Cover"; // 대체 텍스트 설정 (옵션)
				bookCoverImg.style.width = "100px"; // 이미지 크기 조정 (옵션)
				bookCoverImg.style.height = "auto"; // 비율 유지

				bookCoverTd.appendChild(bookCoverImg); // td 태그에 이미지 추가
				
				
				// td태그의 내용들을 tr에 추가
				tr.append(bookTitle, bookAuthor, bookCoverTd);

				// tr태그를 tbody인 memberList에 추가
				bookList.append(tr);
			}
			

		});

	});
	
} 


const genreCard = document.querySelectorAll(".genre-card");

genreCard.forEach(row => {
	row.addEventListener("click", e => {
		const category = e.target.innerText;
		location.href = `/book/bestCategoryList?category=${encodeURIComponent(category)}`;
		
	});
});