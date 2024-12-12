package com.project.VisitBusan.config;

import com.project.VisitBusan.security.Custom403Handler;
import com.project.VisitBusan.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨.
@Log4j2
@RequiredArgsConstructor
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  // 바뀌기전 어노테이션 (언제 사라질지 모름)
//@EnableGlobalMethodSecurity가 @EnableMethodSecurity로 바뀜  Global만 빼면됨
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)  // 어노테이션 권한 설정
// 사용자 지정 로그인 설정
public class CustomSecurityConfig {

    // 4-1 자동 로그인에 필요한 객체 설정
    private final DataSource dataSource;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // SecurityFilterChain의 filterChain() 모든 사용자가 모든 경로에 접근 할 수 있게 설정

        log.info("==> SecurityFilterChain() 호출");

        // 1. CSRF요청 비활성화: 개발 테스트 비활성화
//        http.csrf(c->c.disable());  // 권환 설정 비활성화

        // 4-2 자동 로그인에 필요한 설정

        http.rememberMe(rememberMe->
                rememberMe
                        .key("12345678")
                        .tokenRepository(persistentTokenRepository())  // rememberMe 쿠키의 값을 인코딩하기 위한 키(key), 필요한 정보를 저장하는 tokenRepository를 지정
                        .userDetailsService(customUserDetailsService)
//                        .tokenValiditySeconds(10)  // 10초
                        .tokenValiditySeconds(60*60*24*30)  // 30일 유효(초*분*시간*일)
//                        .rememberMeParameter("remember-me")  // 생략시 기본파라미터 명은 "remember-me", <input type='checkbox' name='파라미터명'>
                        //.alwaysRemember(true)  // 리멤버 미 기능이 활성화되지 않아도 항상 실행
        );

        // 2. 인증 과정 처리

        // 2.1 로그인 관련 설정 => UserDetailsService 인터페이스 구현 후 설정 할 것
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(login -> {
                    login.loginPage("/login")             // 로그인 처리할 url 설정
                            .defaultSuccessUrl("/visitbusan/", true)// 로그인 성공시 url 설정
                            .usernameParameter("userId")                        // 웹의 username의  매개변수이름 설정
                            .passwordParameter("password")                      // 웹의 password의  매개변수이름 설정
                            .loginProcessingUrl("/login")                       // 웹 로그인창의 form action 값 설정
                            .failureUrl("/visitbusan/login/error")       // 로그인 실패시 url 설정

                            // 성공 또는 실패할 경우 핸들러 사용해서 원하는 것을 실행 할 경우 적용
                            // defaultSuccessUrl(),failureUrl() 중복될 경우 핸들러가 우선으로 수행됨.
                            .successHandler(new AuthenticationSuccessHandler() {
                                @Override
                                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                    log.info("==> authentication: "+authentication.getName());
                                    response.sendRedirect("/visitbusan/");
                                }
                            })
                            .failureHandler(new AuthenticationFailureHandler(){
                                @Override
                                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                    log.info("==> exception: " + exception.getMessage());
                                    response.sendRedirect("/visitbusan/login/error");
                                }
                            });
                });

        // 3. 접근 권한 설정
        // SpringBoot 3v 변경된 코드 확인
        // 변경전 : authorizeRequests() → 변경후 : authorizeHttpRequests()
        // http.authorizeRequests().anyRequest().authenticated();
        // -> http.authorizeHttpRequests().anyRequest().authenticated();

        // 정적 자원 경로 접근 제한되어 있는 static폴더가 인식안됨.
        //  1. WebSecurityCustomizer Bean 등록 (아래부분)
        //  2. WebMvcConfigurer 인터페이스를 구현한 config 폴더의 CustomServletConfig 클래스에서 경로를 재설정 시킴
        // 설정 잘못하면 불러온 페이지가 권한을 무한히 요청 할 수도 있음. 로그인 페이지로 무한히 안내.

        http.authorizeHttpRequests( auth -> {
            // 사용자 인증없이 접근할 수 있도록 설정
//            auth.requestMatchers( "/login/**", "/signup/**", "/test/**", "/api/**", "/h2-console/**").permitAll();

            // Role이 ADMIN 경우에만 접근
//            auth.requestMatchers("/admin/**").hasRole("ADMIN");
            // Role이 ADMIN, USER 경우에만 접근
//            auth.requestMatchers("/board/modify", "/board/remove").hasAnyRole("ADMIN","USER");

            // 설정해준 경로를 제외한 나머지 경로들은 모두 인증을 요구하도록 설정
//            auth.anyRequest().authenticated();
            // 설정해준 경로를 제외한 나머지 경로들은 모두 접근 할 수 있도록 설정
            auth.anyRequest().permitAll();
        });

        // ---------------------------------------------------------------------- //
        // 2. h2설정 => H2 웹콘솔의 iframe이 정상적으로 작동하려면 Origin에 대해 허용하도록 설정
        // ---------------------------------------------------------------------- //
        /*
          - Spring Security는 CsrfFilter를 기본적으로 Default Filter로 설정하고 있다.
          - CsrfFilter가 H2 웹콘솔 관련 경로를 무시하도록 설정
          - Spring Security는 기본적으로 HeaderWriterFilter를 활성화.
            HeaderWriterFilter는 XFrameOptionsHeaderWriter를 이용해 설정에 따라
            X-Frame-Options헤더에 DENY, SAMEORIGIN 등의 값을 설정
        */
//        http.csrf(csrf -> csrf
//                    .ignoringRequestMatchers("/api/**","/members/**","/board/**")  // "/api/**" 경로의 CSRF 보호 비활성화
//                    .ignoringRequestMatchers(PathRequest.toH2Console())  // H2 콘솔 경로의 CSRF 보호 비활성화
//            )
//            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));



        /* RoleController 테스트 */   // 수정필요
//        http.authorizeHttpRequests(  httpReq ->
//            httpReq.requestMatchers("/role/test1").permitAll()
//                  .requestMatchers("/role/test2").authenticated()
//                  .requestMatchers("/role/test3").hasRole("USER")
//                  .requestMatchers("/role/test4").hasRole("ADMIN")
//                  .anyRequest().permitAll()
//        );

        //----------------------------------- //
        // 4. 로그아웃 관련 설정
        //----------------------------------- //
        // 로그아웃을 기본으로 설정 =>  url: "/logout" 로그아웃 수행
//        http.logout(Customizer.withDefaults());
        http.logout(logout -> {
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true); // 세션값 삭제로 로그아웃
        });

        // 5 접근 권한에 맞지 않은 요청시 403에러 핸들러 처리  // 이것만 주석 처리시 에러 유도 가능
        http.exceptionHandling(e-> {
            e.accessDeniedHandler(accessDeniedHandler());
        });

        return http.build(); //반환
//        return null; // 권한 문제로 로그인 창이 뜨지만 로그인이 안됨

    }

    // 아래 식을 사용하지 않음 => SecurityFilterChain 사용
//  @Bean
//  public void configureGlobal(AuthenticationManagerBuilder auth)
//      throws Exception {
//    auth
//        .inMemoryAuthentication()
//        .withUser("user").password(passwordEncoder().encode("password")).roles("USER")
//        .and()
//        .withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
//  }

    // 4-3 자동 로그인: 토큰
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {

        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);

        return repo;
    }

    // 5-1 접근 권한에 맞지 않은 요청시 403에러 핸들러 처리
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    // 정적 자원이 시큐리티에서 제외되었을 때 (css, js, images ...)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("==> web configure: WebSecurityCustomizer");

        return (web) -> web.ignoring()
                .requestMatchers(
                        PathRequest
                                .toStaticResources()
                                .atCommonLocations());

    } // end WebSecurityCustomizer

    // ---------------------------------------------------------------------- //
    // 3. h2설정 => Spring Security를 통과하지 않도록 하기
    // ---------------------------------------------------------------------- //
    /*
    @ConditionalOnProperty를 통해 스프링 설정에 h2-console이 enable되어 있을때만 작동하도록 설정
    H2 Console에 대한 요청은 시큐리티 필터를 지나지 않으므로 H2 Console을 자유롭게 이용
    개발 환경이나 운영 환경에서는 spring.h2.console.enabled를 사용하지 않거나 false로 설정 할 겨우
    해당 빈이 생성되지 않아 h2에 대한 흔적을 지울 수 있다.
    */
    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled",havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }

}

/*
 * 기본 Security 설정
 * Spring Boot 2.x 이상에서는 Spring Security의 기본 설정이 자동으로 적용됩니다.
 * 기본 사용자명은 user이며, 애플리케이션 시작 시 콘솔에 출력되는 암호를 사용합니다.
 */

/*
 * 인증이 필요 없는 경우 : 상품 상세 페이지 조회
 * 인증이 필요한 경우 : 상품 주문
 * 관리자 권한 필요한 경우 : 상품 등록
 *
 * 인증(Authentication) : 신원 확인 개념
 * 인가(Authorization) : 허가나 권한 개념
 *
 * 인증(Authentication)과 username
 * - 사용자의 아이디만으로 사용자의 정보를 로딩
 * - 로딩된 사용자의 정보를 이용해서 패스워드 검증
 *
 * UserDetailsService 인터페이스 : 인증을 처리하는 인터페이스 구현체
 * - loadUserByUsername() 1개의 메서드만 존재 : 실제 인증을 처리할 때 수행되는 메서드
 *
 http.authorizeRequests()
                // image 폴더를 login 없이 허용
                .antMatchers("/images/**").permitAll()
                // css 폴더를 login 없이 허용
                .antMatchers("/css/**").permitAll()
                // 어떤 요청이든 '인증'
                .anyRequest().authenticated()
                .and()
                    // 로그인 기능 허용
                    .formLogin()
                    .loginPage("/user/login")
                    .defaultSuccessUrl("/")
                    .failureUrl("/user/login?error")
                    .permitAll()
                .and()
                    // 로그아웃 기능 허용
                    .logout()
                    .permitAll();
 *
 */


/* 어노테이션 권한 설정
@EnableGlobalMethodSecurity(
        securedEnabled = true,  // @Secured어노테이션 확성화 여부
        prePostEnabled = true   // @PreAuthorized, @PostAuthorized 어노테이션 활성화 여부
        )
 */


/*
 * 자동 로그인 설정
 * 자동 로그인 : 데이터베이스 적용=> 테이블 이름은 "persistent_logins" 으로 사용해야함.
 *
 CREATE TABLE persistent_logins (
 	username VARCHAR(64) NOT NULL,
 	series 	VARCHAR(64) PRIMARY KEY,
 	token 	VARCHAR(64) NOT NULL ,
 	last_used TIMESTAMP 	NOT NULL
 );
 *
 */

/*

Vary:	Origin
Vary:	Access-Control-Request-Method
Vary:	Access-Control-Request-Headers
X-Content-Type-Options:	nosniff
X-XSS-Protection:	0
Cache-Control:
no-cache, no-store, max-age=0, must-revalidate
Pragma:	no-cache
Expires:	0
X-Frame-Options:	DENY
Content-Type:	application/json
Transfer-Encoding:	chunked
Date:
Wed, 28 Aug 2024 08:01:18 GMT
Keep-Alive:	timeout=60
Connection:	keep-alive

*/