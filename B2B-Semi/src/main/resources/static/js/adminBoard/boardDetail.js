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

        // 폼 객체 만들기
        const form = document.createElement("form");
        form.action = `/adminBoard/boardDetail/${boardNo}/update`;
        form.method = "POST";

		form.innerHTML = `<input type="hidden" name="boardTitle" value="${newTitle}">
		<input type="hidden" name="boardContent" value="${newContent}">`;

		const params = new URLSearchParams(location.search);
		for( const [key, value] of params.entries()) {
			const input = document.createElement("input");
			input.tpye = "hidden";
			input.name = key;
			input.value = encodeURIComponent(value);
			form.appendChild(input);
		}
		

        // 화면에 form태그 추가하고 실행
        document.querySelector("body").appendChild(form);
		
        form.submit();
    });
});

/* 목록으로 돌아가는 버튼 */
const goToListBtn = document.querySelector("#communityList");

goToListBtn.addEventListener("click", () => {

	const URLParams = new URLSearchParams(window.location.search);

	const key = URLParams.get('key') || '';
	const search = URLParams.get('search') || '';
	const cp = URLParams.get('cp') || '';
	const delfl = URLParams.get('delfl') || '';
	if(key == null) {
		window.location.href = `/adminBoard/boardManage?cp=${cp}`;
	}
	else {
		window.location.href = `/adminBoard/boardManage?cp=${cp}&key=${key}&search=${search}&delfl=${delfl}`;
	}
	
});

const deleteBtn = document.querySelector("#deleteBtn");

if (deleteBtn != null) {
	deleteBtn.addEventListener("click", () => {

		if (!confirm("삭제 하시겠습니까?")) {
			alert("취소됨")
			return;
		}

		// form태그 생성
		const form = document.createElement("form");
		form.action = `/adminBoard/boardDetail/${boardNo}/delete`;
		form.method = "POST";

		const params = new URLSearchParams(location.search);
		
		for (const [key, value] of params.entries()) {
		    const input = document.createElement("input");
		    input.type = "hidden";
		    input.name = key;
		    input.value = encodeURIComponent(value);
		    form.appendChild(input);
		}

		// 화면에 form태그를 추가한 후 제출하기
		document.querySelector("body").appendChild(form);
		form.submit();

	});
}