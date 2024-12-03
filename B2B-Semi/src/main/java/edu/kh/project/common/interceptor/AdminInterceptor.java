package edu.kh.project.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession();
		Member loginMember = (Member) session.getAttribute("loginMember");
		
		// 로그인하지 않았거나, 관리자 권한이 아닌 유저라면 접근 차단.
		if(loginMember == null || loginMember.getMemberAuth() != 2) {
			
			response.sendRedirect("/accessDenied");
			return false;
		}

		// 관리자 권한이면 true
		return true;
		
	}
	
}
