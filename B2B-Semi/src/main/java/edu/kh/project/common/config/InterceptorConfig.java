package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.project.common.interceptor.AdminInterceptor;
import edu.kh.project.common.interceptor.BoardTypeInterceptor;
import edu.kh.project.common.interceptor.MyPageInterceptor;
import lombok.RequiredArgsConstructor;

// 인터셉터가 어떤 요청을 가로챌지 설정하는 클래스

@Configuration // 서버가 켜지면 내부 메서드를 모두 수행
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
	
	private final AdminInterceptor adminInterceptor;
	
	private final MyPageInterceptor myPageInterceptor;
	// 인터셉터 클래스 Bean 등록
	@Bean // 개발자가 만들어서 반환하는 객체를 Bean 등록 - 관리는 Spring Container가 수행
	public BoardTypeInterceptor boardTypeInterceptor() {
		return new BoardTypeInterceptor();
	}
	
	
	// 동작할 인터셉터 객체를 추가하는 메서드
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// Bean으로 등록된 BoardTypeInterceptor를 얻어와 매개변수로 전달
		registry.addInterceptor(boardTypeInterceptor())
				.addPathPatterns("/**") // 가로챌 요청 주소 지정 /** : / 이하 모든 요청 주소
				.excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico"); // 가로채지 않을 주소
		
		
		registry.addInterceptor(adminInterceptor)
				.addPathPatterns("/adminBoard/**")
				.excludePathPatterns("/login", "/error", "/accessDenied");
		
		
		registry.addInterceptor(myPageInterceptor)
		.addPathPatterns("/myPage/**")
		.excludePathPatterns("/login", "/error", "/accessDenied2");
		
	}
	
	
}
