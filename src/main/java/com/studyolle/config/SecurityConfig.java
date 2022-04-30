package com.studyolle.config;


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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .mvcMatchers("/","/login","/sign-up","/check-email-token", "/email-login","/check-email-login","/login-link").permitAll()
            .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll() //get 만 허용하겟다
            .anyRequest().authenticated();

        http.formLogin().loginPage("/login").permitAll();

        http.logout().logoutSuccessUrl("/"); //로그아웃시 이동할 페이지 설정
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .mvcMatchers("/node_modules/**")
            .mvcMatchers("/static/images/**")
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


}
