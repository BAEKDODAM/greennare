package greenNare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://codestates-seb.github.io/") // 클라이언트 애플리케이션이 호스팅되는 도메인
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "HEAD", "OPTIONS") // 지원하는 HTTP 메서드
                .exposedHeaders("Authorization") // 노출할 응답 헤더 (필요한 경우에만 노출)
                .allowCredentials(false) // 인증 정보를 포함하지 않도록 설정 (보안상의 이유로 기본적으로는 false)
                .allowedHeaders("Content-Type"); // 허용되는 요청 헤더 (필요한 경우에만 추가)
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("file:///home/ssm-user/seb44_main_026/images/");
    }
}
