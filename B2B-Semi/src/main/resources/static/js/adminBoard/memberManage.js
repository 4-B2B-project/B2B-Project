const searchMemberListBtn = document.querySelector("#searchBtn");
const tbody = document.querySelector("#tbody");
const input = document.querySelector("#searchInput");
const checkAll = document.querySelector("#theadCheckAll");

searchMemberListBtn.addEventListener("click", () => {

	if(searchMemberListBtn) {
		const key = document.querySelector("#searchKey").value;
		const query = input.value.trim();
	
		window.location.href = `/adminBoard/searchMember?key=${key}&search=${query}`;
	}

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
	
	console.log(memberNos);
	
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
		
		const key = URLParams.get('key');
		const search = URLParams.get('search');
		let cp = URLParams.get('cp');
		
		if(cp == null) {
			cp = 1;
		}
		
		window.location.href = `/adminBoard/updateMember?cp=${cp}&key=${key}&search=${search}&memberNo=${memberNo}`;
	})
});