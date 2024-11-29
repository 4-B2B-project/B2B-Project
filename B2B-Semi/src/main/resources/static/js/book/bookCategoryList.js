// 툴팁 함수
// 제목이랑 작가 풀네임 툴팁으로 조회
function showTooltip(event, element) {
	let tooltip = document.getElementById('dynamic-tooltip');

	// tooltip이 없을때 임의로 생성
	if (!tooltip) {
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

	// 툴팁 내용 업데이트
	tooltip.textContent = element.getAttribute('data-full-text');
	tooltip.style.display = 'block';

	// 마우스 커서 위치에 따라 툴팁 위치 조정
	const offset = 5; // 커서로부터의 거리
	tooltip.style.left = (event.pageX + offset) + 'px';
	tooltip.style.top = (event.clientY - offset * 14) + 'px';

	// 요소에서 마우스가 벗어났을 때 툴팁 숨기기
	element.onmouseleave = function() {
		tooltip.style.display = 'none';
	};
}

