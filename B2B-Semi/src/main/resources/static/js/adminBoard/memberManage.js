const searchMemberListBtn = document.querySelector("#searchBtn");
const tbody = document.querySelector("#tbody");
const input = document.querySelector("#searchInput");
const checkAll = document.querySelector("#theadCheckAll");

searchMemberListBtn.addEventListener("click", e => {

		const key = document.querySelector("#searchKey").value;
		const query = input.value.trim();
		const delfl = document.querySelector("select[name='delfl']").value;
		
		const params = new URLSearchParams();
		if (key) params.append("key", key);
		if (query) params.append("search", query);
		if (delfl) params.append("delfl", delfl);

		// 검색은 항상 첫 페이지(cp=1)부터 시작
		params.append("cp", "1");

		const queryString = params.toString();
		
		if (!key && !query && !delfl) {
		    alert("필터를 선택하거나 검색어를 입력해주세요.");
		    e.preventDefault();
		    return;
		}
		
		window.location.href = `/adminBoard/searchMember?${queryString}`;

});

input.addEventListener("keyup", e => {
	if(e.key == "Enter") {
		searchMemberListBtn.click();
	}
})

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
		
		console.log("allChecked : " + allChecked);
	}
});

const updateDelFlYBtn = document.querySelector("#updateDelFlY").addEventListener("click", () => updateMemberStatus('탈퇴'));
const updateDelFlNBtn = document.querySelector("#updateDelFlN").addEventListener("click", () => updateMemberStatus('활성'));

function updateMemberStatus(action) {
	
	const checkboxes = tbody.querySelectorAll(".checkbox:checked");
	if (checkboxes.length === 0) {
		alert("선택된 회원이 없습니다.");
		return;
	}
	
	const memberNos = Array.from(checkboxes).map(box => box.value);
	
	fetch("/adminBoard/updateStatus", {
		method : "POST",
		headers : {"Content-type" : "application/json"},
		body : JSON.stringify({
			memberNos: memberNos,
			action: action
		})
		
	})
	.then(response => response.json())
	.then(data => {
		if(data.success) {
			alert(`회원 상태가 ${action}으로 변경됐습니다.`);
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

			window.location.href = `/adminBoard/searchMember?${queryString}`;
			
		}
		else {
			alert("상태 변경 실패:" + data.message);
		}
	})
	.catch(error => console.error("Error:",error));
}

const updateMemberBtn = document.querySelectorAll("button[name='updateMemberBtn']").forEach(button => {
	button.addEventListener("click", () => {
		const memberNo = button.getAttribute('data-member-no');
		
		const URLParams = new URLSearchParams(window.location.search);
		
		let key = URLParams.get('key');
		let search = URLParams.get('search');
		let cp = URLParams.get('cp');
		let delfl = URLParams.get('delfl');
		
		if( key == null) key = '';
		if( search == null) search = '';
		if( delfl == null) delfl = '';
		if( cp == null) cp = 1;
		
		window.location.href = `/adminBoard/updateMember?cp=${cp}&key=${key}&search=${search}&delfl=${delfl}&memberNo=${memberNo}`;
	})
});