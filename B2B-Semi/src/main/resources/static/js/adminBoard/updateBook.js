const book = /*[[${book}]]*/ null;

document.addEventListener('DOMContentLoaded', function() {
    
    const bookId = document.getElementById('selectBookId').value;
	const submitUpdate = document.getElementById("submitUpdate");
	const cancelUpdate = document.getElementById("cancelUpdate");

	const searchParams = new URLSearchParams(window.location.search);
    const key = searchParams.get("key") || "";
	const search = searchParams.get("search") || "";
	const cp = searchParams.get("cp") || 1;
	const delfl = searchParams.get("delfl") || "";
	
	submitUpdate.addEventListener("click", () => {
		
		const author = document.getElementById("authorInput").value;
		const publisher = document.getElementById("publisherInput").value;
		const description = document.getElementById("descriptionTextarea").value;
		
		const obj = {
			"bookId" : bookId,
			"author" : author,
			"publisher" : publisher,
			"description" : description,
			"cp" : cp,
			"key" : key,
			"search" : search
		}
		
		fetch("/adminBoard/updateBook", {
			method : "POST",
			headers : {"Content-Type" : "application/json"},
			body : JSON.stringify(obj)
		})
		.then(response => response.json())
		.then(data => {
			
			if(data.success) {
				alert("수정 완료.");
				window.location.href = `/adminBoard/bookManage?cp=${cp}&key=${key}&search=${search}&delfl=${delfl}`;
			}
			
			else {
				alert("수정 실패...");
				window.location.reload();
			}
			
		})
		.catch(error => {
			console.error("error : ", error);
		});
	});
	
	cancelUpdate.addEventListener("click", () => {
		alert("수정 취소. 목록으로 돌아갑니다.");
		
		const URLParams = new URLSearchParams(window.location.search);

		const key = URLParams.get('key') || '';
		const search = URLParams.get('search') || '';
		const cp = URLParams.get('cp') || '';
		const delfl = URLParams.get('delfl') || '';
		
		if(key == null) {
			window.location.href = `/adminBoard/bookManage?cp=${cp}`;
		}
		else {
			window.location.href = `/adminBoard/bookManage?cp=${cp}&key=${key}&search=${search}&delfl=${delfl}`;
		}
		
	});
	
});
