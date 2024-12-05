const searchBoardListBtn = document.querySelector("#searchBtn");
const tbody = document.querySelector("#tbody");
const input = document.querySelector("#searchInput");
const checkAll = document.querySelector("#theadCheckAll");

searchBoardListBtn.addEventListener("click", () => {
	
	const key = document.querySelector("#searchKey").value;
	const query = input.value.trim();
	
	window.location.href = `/adminBoard/searchBoard?key=${key}&search=${query}`;
})

input.addEventListener("keyup", e => {
	if(e.key == "Enter") {
		searchBoardListBtn.click();
	}
})

const updateDelFlYBtn = document.querySelector("#updateDelFlY").addEventListener("click", () => updateBoardStatus('삭제'));
const updateDelFlNBtn = document.querySelector("#updateDelFlN").addEventListener("click", () => updateBoardStatus('활성'));

function updateBoardStatus(action) {
	
	const checkboxes = tbody.querySelectorAll(".checkbox:checked");
	if (checkboxes.length === 0) {
		alert("선택된 게시글이 없습니다.");
		return;
	}
	
	const boardList = Array.from(checkboxes).map(box => box.value);
	
	fetch("/adminBoard/updateBoardStatus", {
		method : "POST",
		headers : {"Content-type" : "application/json"},
		body : JSON.stringify({
			boardList: boardList,
			action: action
		})
		
	})
	.then(response => response.json())
	.then(data => {
		if(data.success) {
			alert(`게시글 상태가 ${action}으로 변경됐습니다.`);
			location.reload();
		}
		else {
			alert("상태 변경 실패:" + data.message);
		}
		
	})
	.catch(error => console.error("Error:",error));
}

// 체크박스 전체 선택.
document.querySelector("#theadCheckAll").addEventListener("change", e => {
	const checkboxes = tbody.querySelectorAll(".checkbox");
	checkboxes.forEach(checkbox => {
		checkbox.checked = e.target.checked;
	});
});

// 체크박스 전체 선택 동기화.
tbody.addEventListener("change", e => {
	if(e.target.classList.contains("checkbox")) {
		const checkboxes = tbody.querySelectorAll(".checkbox");
		const allChecked = Array.from(checkboxes).every(cb => cb.checked);
		document.querySelector("#theadCheckAll").checked = allChecked;
	}
})

// 게시글 수정 페이지 이동.
const updateBoardBtn = document.querySelectorAll("button[class='updateBoardBtn']").forEach(button => {
	button.addEventListener("click", () => {
		
		const boardNo = button.getAttribute('data-board-id');

		const URLParams = new URLSearchParams(window.location.search);

		let key = URLParams.get('key');
		let search = URLParams.get('search');
		let cp = URLParams.get('cp');
		
		if(cp == null) {
			cp = 1;
		}
		if(key == null) {
			key = '';
		}
		if(search == null) {
			search = '';
		}
		
		
		window.location.href = `/adminBoard/boardDetail/${boardNo}?cp=${cp}&key=${key}&search=${search}`;
				
	})
});
