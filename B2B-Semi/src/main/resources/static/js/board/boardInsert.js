/* 목록으로 돌아가는 버튼 */
const goToListBtn = document.querySelector("#communityList");

goToListBtn.addEventListener("click", () => {
	let path = location.pathname.split("/");
	
	console.log(path);
		
    if (path[1] === "editBoard" && path.length >= 3) {
        // 'board' 뒤의 2번째 요소를 'community'로 변경하고 3번째 요소까지만 유지
		path[1] = "board";
		path[3] = path[2];
        path[2] = "community";
    }

    // 새로운 경로 조합
    const newUrl = path.join("/") + location.search;
	
    // 실제 이동
    location.href = newUrl;
});


// 작성 폼 유효성 검사 및 제출 처리
const insertBtn = document.querySelector("#boardInsert");
insertBtn.addEventListener("click", e => {
	const boardTitle = document.querySelector("[name='boardTitle']");
	const boardContent = document.querySelector("[name='boardContent']");
	const secretCheck = document.querySelector("[name='secretCheck']");
	const selectCheckBox = document.querySelector("#selectCheckBox");
	
	// 체크 여부에 따른 값 세팅
	if(selectCheckBox != null) {
		if(selectCheckBox.checked) {
			secretCheck.value = "Y";
		} else {
			secretCheck.value = "N";
		}
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
	
	document.querySelector("#boardWriteFrm").submit();	

});
