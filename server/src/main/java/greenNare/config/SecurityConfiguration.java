package greenNare.config;

import greenNare.auth.filter.JwtAuthenticationFilter;
import greenNare.auth.filter.JwtVerificationFilter;
import greenNare.auth.handler.MemberAuthenticationFailureHandler;
import greenNare.auth.handler.MemberAuthenticationSuccessHandler;
import greenNare.auth.jwt.JwtTokenizer;
import greenNare.auth.utils.CustomAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {


    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource()) // CORS 설정 추가
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeRequests(authorize -> authorize
                        .antMatchers("**").permitAll()
                        .antMatchers("/swagger-ui/**").permitAll()
                        .antMatchers("/h2/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/*/").permitAll()
                        .antMatchers(HttpMethod.PATCH, "/*/").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/*/").hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll()
                );

        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://codestates-seb.github.io/")); // 클라이언트 애플리케이션이 호스팅되는 도메인
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "HEAD", "OPTIONS")); // 지원하는 HTTP 메서드
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Refresh")); // 노출할 응답 헤더 (필요한 경우에만 노출)
        configuration.setAllowCredentials(true); // 인증 정보를 포함하지 않도록 설정 (보안상의 이유로 기본적으로는 false)
        configuration.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Refresh")); // 허용되는 요청 헤더 (필요한 경우에만 추가)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,jwtTokenizer);

            jwtAuthenticationFilter.setFilterProcessesUrl("/user/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);

        }
    }
}






