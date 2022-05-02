package com.studyolle.config;


import com.studyolle.account.AccountService;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .mvcMatchers("/","/login","/sign-up","/check-email-token", "/email-login","/check-email-login","/login-link").permitAll()
            .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll() //get 만 허용하겟다
            .anyRequest().authenticated();

        http.formLogin().loginPage("/login").permitAll();

        http.logout().logoutSuccessUrl("/"); //로그아웃시 이동할 페이지 설정

        http.rememberMe()
            .userDetailsService(accountService)
            .tokenRepository(tokenRepository());

    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .mvcMatchers("/node_modules/**")
            .mvcMatchers("/static/images/**")
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


}
