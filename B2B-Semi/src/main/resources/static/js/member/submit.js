const btnSecond = document.querySelector('.btn-secondary');

btnSecond.addEventListener('click', function() {
	if(confirm("회원가입을 취소하고 로그인 페이지로 이동 하시겠습니까?")) {
		window.location.href = '/member/login';
	} 
});
