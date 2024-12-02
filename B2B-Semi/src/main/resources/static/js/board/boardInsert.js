/* 목록으로 돌아가는 버튼 */
const goToListBtn = document.querySelector("#communityList");

goToListBtn.addEventListener("click", () => {
	// 현재 경로: /board/detailCommunity/1?cp=1&boardNo=5
	// 원하는 경로: /board/community/1?cp=1

	let path = location.pathname.split("/"); // 경로를 "/" 기준으로 분리
	path[2] = "community"; // "detailCommunity"를 "community"로 변경

	// 새로운 경로 조합
	const newUrl = path.join("/") + location.search;

	console.log(newUrl); // 디버깅용
	location.href = newUrl; // 새로운 URL로 이동
});


// 작성 폼 유효성 검사 및 제출 처리
document.querySelector("#boardWriteFrm").addEventListener("submit", e => {
	const boardTitle = document.querySelector("[name='boardTitle']");
	const boardContent = document.querySelector("[name='boardContent']");
	const secretCheck = document.querySelector("[name='secretCheck']");
	const selector = document.querySelector("[name='selector']");
	
	if(selector != null) {
		secretCheck.value = selector.value;
	}
	
	// 유효성 검사
	if (boardTitle.value.trim().length === 0) {
		alert("제목을 작성해 주세요");
		e.preventDefault();
		boardTitle.focus();
		return;
	}

	// 유효성 검사
	if (boardContent.value.trim().length === 0) {
		alert("내용을 작성해 주세요");
		boardContent.focus();
		e.preventDefault();
		return;
	}
	

});
