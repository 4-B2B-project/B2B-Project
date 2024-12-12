const searchBookListBtn = document.querySelector("#searchBtn"); // 도서 관리 검색창
const tbody = document.querySelector("#tbody");
const input = document.querySelector("#searchInput");
const checkAll = document.querySelector("#theadCheckAll");

searchBookListBtn.addEventListener("click", () => {
	
	const key = document.querySelector("#searchKey").value;
	const query = input.value.trim();
	const delfl = document.querySelector("select[name='delfl']").value;
	
	const params = new URLSearchParams();

	if (key) params.append("key", key);
	if (query) params.append("search", query);
	if (delfl) params.append("delfl", delfl);

	params.append("cp", "1");

	const queryString = params.toString();

	if (!key && !query && !delfl) {
	    alert("검색 조건을 입력하거나 필터를 선택하세요.");
	    e.preventDefault();
	    return;
	}

	window.location.href = `/adminBoard/searchBook?${queryString}`;
})

input.addEventListener("keyup", e => {
	if(e.key == "Enter") {
		searchBookListBtn.click();
	}
})

const updateDelFlYBtn = document.querySelector("#updateDelFlY").addEventListener("click", () => updateBookStatus('삭제'));
const updateDelFlNBtn = document.querySelector("#updateDelFlN").addEventListener("click", () => updateBookStatus('활성'));

function updateBookStatus(action) {
	
	const checkboxes = tbody.querySelectorAll(".checkbox:checked");
	if (checkboxes.length === 0) {
		alert("선택된 도서가 없습니다.");
		return;
	}
	
	const bookList = Array.from(checkboxes).map(box => box.value);
	
	
	fetch("/adminBoard/updateBookStatus", {
		method : "POST",
		headers : {"Content-type" : "application/json"},
		body : JSON.stringify({
			bookList: bookList,
			action: action
		})
		
	})
	.then(response => response.json())
	.then(data => {
		if(data.success) {
			alert(`도서 삭제여부 상태가 ${action}으로 변경됐습니다.`);
			location.reload();
			
			const key = document.querySelector("#searchKey").value;
			const query = input.value.trim();
			const delfl = document.querySelector("select[name='delfl']").value;

			const params = new URLSearchParams();

			params.append("cp", "1");
			if (key) params.append("key", key);
			if (query) params.append("search", query);
			if (delfl) params.append("delfl", delfl);

			const queryString = params.toString();

			window.location.href = `/adminBoard/searchBook?${queryString}`;
			
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

const updateBookBtn = document.querySelectorAll("button[name='updateBookBtn']").forEach(button => {
	button.addEventListener("click", () => {
		const bookId = button.getAttribute('data-book-id');
		
		const URLParams = new URLSearchParams(window.location.search);

		let key = URLParams.get('key');
		let search = URLParams.get('search');
		let cp = URLParams.get('cp');
		let delfl = URLParams.get('delfl');
		
		if( key == null) key = '';
		if( search == null) search = '';
		if( delfl == null) delfl = '';
		if( cp == null) cp = 1;
		
		window.location.href = `/adminBoard/updateBook?cp=${cp}&key=${key}&search=${search}&delfl=${delfl}&bookId=${bookId}`;
	})
});

const addBookBtn = document.querySelector("#addBook").addEventListener("click", () => {
	window.location.href = "/insertBook/addBook";
});

function showTooltip(event, element) {
	
	let tooltip = document.getElementById('dynamic-tooltip');
	
	if(!tooltip) {
		tooltip = document.createElement('div');
		tooltip.id = 'dynamic-tooltip';
		tooltip.style.position = 'fixed';
		tooltip.style.backgroundColor = 'white';
		tooltip.style.padding = '5px';
		tooltip.style.border = '1px solid #ccc';
		tooltip.style.borderRadius = '4px';
		tooltip.style.boxShadow = '0 2px 5px rgba(0,0,0,0.2)';
		tooltip.style.zIndex = '1000';
		tooltip.style.display = 'none';
		document.body.appendChild(tooltip);
	}
	
	tooltip.textContent = element.getAttribute('data-full-text');
	tooltip.style.display = 'block';
	
	const offset = 5;
	tooltip.style.left = (event.pageX + offset) + 'px';
	tooltip.style.top = (event.clientY - offset * 14) + 'px';
	
	element.onmouseleave = function() {
		tooltip.style.display = 'none';
	};
	
}
