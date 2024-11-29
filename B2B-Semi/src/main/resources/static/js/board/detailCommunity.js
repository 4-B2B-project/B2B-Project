document.addEventListener('DOMContentLoaded', () => {
	// 사용할 태그들 변수 선언
    const editBtn = document.querySelector("#editBtn");
    const saveBtn = document.querySelector("#saveBtn");
    const cancelBtn = document.querySelector("#cancelBtn");
    const boardTitleDisplay = document.querySelector("#boardTitleDisplay");
    const boardTitleEdit = document.querySelector("#boardTitleEdit");
    const boardContentDisplay = document.querySelector("#boardContentDisplay");
    const boardContentEdit = document.querySelector("#boardContentEdit");

    // 수정 버튼 클릭시
    editBtn.addEventListener("click", () => {
        // 기존 태그 숨기기
        boardTitleDisplay.style.display = 'none';
        boardContentDisplay.style.display = 'none';

        // 수정 태그 보여주기
        boardTitleEdit.style.display = 'inline';
        boardContentEdit.style.display = 'block';

        //토글 버튼 스타일
        editBtn.style.display = 'none';
        deleteBtn.style.display = 'none';
        saveBtn.style.display = 'inline-block';
        cancelBtn.style.display = 'inline-block';
    });

    // 취소 버튼 클릭스
    cancelBtn.addEventListener("click", () => {
        // 기존의 값으로 세팅
        boardTitleEdit.value = boardTitleDisplay.textContent;
        boardContentEdit.value = boardContentDisplay.textContent;

        // 수정 태그 숨기기
        boardTitleEdit.style.display = 'none';
        boardContentEdit.style.display = 'none';

        // 기존 태그 보여주기
        boardTitleDisplay.style.display = 'inline';
        boardContentDisplay.style.display = 'block';

        // 토글 버튼 스타일
        editBtn.style.display = 'inline-block';
        deleteBtn.style.display = 'inline-block';
        saveBtn.style.display = 'none';
        cancelBtn.style.display = 'none';
    });

    // 수정 버튼 클릭시 
    saveBtn.addEventListener("click", () => {
        const newTitle = boardTitleEdit.value.trim();
        const newContent = boardContentEdit.value.trim();

        if (newTitle === "") {
            alert("제목을 입력해주세요.");
            return;
        }

        if (newContent === "") {
            alert("내용을 입력해주세요.");
            return;
        }

        // 폼 객체체 만들기
        const form = document.createElement("form");
        form.action = location.pathname.replace("board", "editBoard") + "/update";
        form.method = "POST";

        // 제목 값 세팅
        const titleInput = document.createElement("input");
        titleInput.type = "hidden";
        titleInput.name = "boardTitle";
        titleInput.value = newTitle;

		// 내용 값 세팅
        const contentInput = document.createElement("input");
        contentInput.type = "hidden";
        contentInput.name = "boardContent";
        contentInput.value = newContent;

        // 검색값 세팅
        const params = new URLSearchParams(location.search);
        const cp = params.get("cp");
        const searchType = params.get("searchType");
        const searchInput = params.get("searchInput");

		// 페이징
        if (cp) {
            const cpInput = document.createElement("input");
            cpInput.type = "hidden";
            cpInput.name = "cp";
            cpInput.value = cp;
            form.append(cpInput);
        }

		// 검색조건
        if (searchType) {
            const searchTypeInput = document.createElement("input");
            searchTypeInput.type = "hidden";
            searchTypeInput.name = "searchType";
            searchTypeInput.value = searchType;
            form.append(searchTypeInput);
        }

		// 검색값
        if (searchInput) {
            const searchInputElement = document.createElement("input");
            searchInputElement.type = "hidden";
            searchInputElement.name = "searchInput";
            searchInputElement.value = encodeURIComponent(searchInput);
            form.append(searchInputElement);
        }

        // form에 붙이기
        form.append(titleInput);
        form.append(contentInput);

        // 화면에 form태그 추가하고 실행
        document.querySelector("body").append(form);
        form.submit();
    });
});

/* 목록으로 돌아가는 버튼 */
const goToListBtn = document.querySelector("#communityList");

goToListBtn.addEventListener("click", () => {

	let path = location.pathname.split("/");
	path[2] = "community";

	// 새로운 경로 조합
	const newUrl = path.join("/") + location.search;

	console.log(newUrl);
	location.href = newUrl;
});

const deleteBtn = document.querySelector("#deleteBtn");

if (deleteBtn != null) {
	deleteBtn.addEventListener("click", () => {

		if (!confirm("삭제 하시겠습니까?")) {
			alert("취소됨")
			return;
		}

		const url = location.pathname.replace("board", "editBoard") + "/delete";

		// form태그 생성
		const form = document.createElement("form");
		form.action = url;
		form.method = "POST";

		// cp값을 저장할 input 생성
		const cpInput = document.createElement("input");
		cpInput.type = "hidden";
		cpInput.name = "cp";
		
		// 검색조건 저장할 input 생성
		const srchtype = document.createElement("input");
		srchtype.type = "hidden";
		srchtype.name = "searchType";
		
		// 검색 값 저장할 input 생성
		const srchInput = document.createElement("input");
		srchInput.type = "hidden";
		srchInput.name = "searchInput";

		// 쿼리스트링에서 검색조건 값 가져오기
		const params = new URLSearchParams(location.search)
		const type = params.get("searchType");
		srchtype.value = type;
		
		// 쿼리스트링에서 검색 값 가져오기
		const inp = params.get("searchInput");
		const encodedSearchInput = encodeURIComponent(inp);
		srchInput.value = encodedSearchInput;
		
		// 페이징값 넣기
		const cp = params.get("cp");
		cpInput.value = cp;

		form.append(srchtype);
		form.append(srchInput);
		form.append(cpInput);

		// 화면에 form태그를 추가한 후 제출하기
		document.querySelector("body").append(form);
		form.submit();

	});
}