const book = /*[[${book}]]*/ null;

document.addEventListener('DOMContentLoaded', function() {
    
    const bookId = document.getElementById('selectBookId').value;
	const submitUpdate = document.getElementById("submitUpdate");
	const cancelUpdate = document.getElementById("cancelUpdate");

	const searchParams = new URLSearchParams(window.location.search);
    const key = searchParams.get("key") || "";
	const search = searchParams.get("search") || "";
	const cp = searchParams.get("cp") || 1;
	
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
		
		console.log(cp);
		console.log(key);
		console.log(search);
		
		fetch("/adminBoard/updateBook", {
			method : "POST",
			headers : {"Content-Type" : "application/json"},
			body : JSON.stringify(obj)
		})
		.then(response => response.json())
		.then(data => {
			
			if(data.success) {
				alert("수정 완료.");
				window.location.href = `/adminBoard/bookManage?cp=${cp}&key=${key}&search=${search}`;
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
		window.location.href = `/adminBoard/bookManage?cp=${cp}&key=${key}&search=${search}`;
	});
	
});
