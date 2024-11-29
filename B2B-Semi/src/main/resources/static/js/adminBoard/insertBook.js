const searchBookListBtn = document.querySelector("#searchBtn"); // 도서 관리 검색창
const tbody = document.querySelector("#tbody");
const input = document.querySelector("#searchInput");

searchBookListBtn.addEventListener("click", () => {
	window.location.href = `/adminBoard/searchBook?search=${query}`;
})


