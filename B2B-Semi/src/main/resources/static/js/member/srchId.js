// 인증번호 받기 버튼
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");

// 인증번호 입력 input
const authKey = document.querySelector("#authKey");

// 인증번호 입력 후 확인 버튼
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn");

// 인증번호 관련 메시지 출력 span
const authKeyMessage = document.querySelector("#authKeyMessage");

let authTimer; // 타이머 역할을 할 setInterval을 저장할 변수

const initMin = 4; // 타이머 초기값 (분)
const initSec = 59; // 타이머 초기값 (초)
const initTime = "05:00";

// 실제 줄어드는 시간을 저장할 변수
let min = initMin;
let sec = initSec;

//---------------------------------------------------

/* 이메일 유효성 검사 */

// 1) 이메일 유효성 검사에 사용될 요소 얻어오기
const memberEmail = document.querySelector("#inputEmail");

// 인증번호 받기 버튼 클릭 시 
sendAuthKeyBtn.addEventListener("click", () => {
	
	// (알맞은 형태로 작성했는지 검사)
	const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	
	// (알맞은 이메일 형태가 아닌 경우)
	if (!regExp.test(memberEmail.value)) {
		alert("알맞은 이메일 형태를 입력해주세요.");
		memberEmail.focus();
		return;
	}

	authKeyMessage.innerText = "";

	// 클릭 시 타이머 숫자 초기화
	min = initMin;
	sec = initSec;

	// 이전 동작중인 인터벌 클리어(없애기)
	clearInterval(authTimer);

	// 비동기로 서버에서 메일보내기 
	fetch("/email/signup", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: memberEmail.value
	})
		.then(resp => resp.text())
		.then(result => {
			if (result == 1) {
				console.log("인증 번호 발송 성공");
			} else {
				console.log("인증 번호 발송 실패!!!!");
			}
		});


	// *************************************

	// 메일은 비동기로 서버에서 보내라고 하고
	// 화면에서는 타이머 시작하기

	authKeyMessage.innerText = initTime; // 05:00 세팅
	authKeyMessage.classList.remove("confirm", "error"); // 검정 글씨

	alert("인증번호가 발송되었습니다.");

	// 인증 시간 출력(1초 마다 동작)
	authTimer = setInterval(() => {

		authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;

		// 0분 0초인 경우 ("00:00" 출력 후)
		if (min == 0 && sec == 0) {
			clearInterval(authTimer); // interval 멈춤
			authKeyMessage.classList.add('error');
			authKeyMessage.classList.remove('confirm');
			return;
		}

		// 0초인 경우(0초를 출력한 후)
		if (sec == 0) {
			sec = 60;
			min--;
		}

		sec--; // 1초 감소

	}, 1000); // 1초 지연시간

});


// 전달 받은 숫자가 10 미만인 경우(한자리) 앞에 0 붙여서 반환
function addZero(number) {
	if (number < 10) return "0" + number;
	else return number;
}


// -------------------------------------------------------------

// 인증하기 버튼 클릭 시
// 입력된 인증번호를 비동기로 서버에 전달
// -> 입력된 인증번호와 발급된 인증번호가 같은지 비교
//   같으면 1, 아니면 0반환
// 단, 타이머가 00:00초가 아닐 경우에만 수행

checkAuthKeyBtn.addEventListener("click", () => {

	if (min === 0 && sec === 0) { // 타이머가 00:00인 경우
		alert("인증번호 입력 제한시간을 초과하였습니다.");
		return;
	}

	if (authKey.value.length < 6) { // 인증번호가 제대로 입력 안된 경우(길이가 6미만인 경우)
		alert("인증번호를 정확히 입력해 주세요.");
		return;
	}

	// 문제 없는 경우(제한시간, 인증번호 길이 유효 시)
	// 입력받은 이메일, 인증번호로 객체 생성
	const obj = {
		"email": memberEmail.value,
		"authKey": authKey.value
	};

	// 인증번호 확인용 비동기 요청 보냄
	fetch("/email/checkAuthKey", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(obj) // obj JS 객체를 JSON 으로 변경
	})
		.then(resp => resp.text())
		.then(result => {
			// 1 or 0

			if (result == 0) {
				alert("인증번호가 일치하지 않습니다!");
				return;
			}

			// 일치할 때
			clearInterval(authTimer); // 타이머 멈춤
			
			// 회원 아이디 출력 함수
			srchUserId();


		});

});

// 회원 아이디 출력 함수
function srchUserId () {
	
	const obj = {
		"memberEmail" : memberEmail.value
	}
	
	// 인증번호 확인용 비동기 요청 보냄
	fetch("/member/srchId", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(obj)
	})
	.then(resp => resp.text())
	.then(result => {
		
		document.getElementById('srchIdForm').style.display = 'none';
		document.getElementById('printSrchId').style.display = 'block';
		
		if(result != '') {
			let html = `
				<div class="input-group">
					<h1 class="signUp-title">회원님의 아이디는</h1>
					<p class="signUp-title" id="srchId" style="font-size: 24px; color: #4f46e5; ">${result}</p>
					<p class="signUp-title" style="font-size: 24px;">&nbsp; 입니다</p>
				</div>
				<div class="button-group">
					<a href="/member/login" class="submit-button">로그인 화면으로 이동</a>
				</div>
			`
			
			document.getElementById('printSrchId').innerHTML = html;
		} else {
			let html = `
				<div class="input-group">
					<p class="signUp-title" id="srchId" style="font-size: 20px; ">해당되는 이메일의 회원이 없습니다</p>
				</div>
				<div class="button-group">
					<a href="/member/submit" class="submit-button">회원가입 화면으로 이동</a>
				</div>
			`
			
			document.getElementById('printSrchId').innerHTML = html;
		}

		// 일치할 때
		clearInterval(authTimer); // 타이머 멈춤
		
	});
	
}
