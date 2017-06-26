package com.leadingsoft.liuw.config;

import com.leadingsoft.liuw.base.Http401UnauthorizedEntryPoint;
import com.leadingsoft.liuw.filter.DefaultLogoutHandler;
import com.leadingsoft.liuw.filter.WxAppAuthenticationFilter;
import com.leadingsoft.liuw.filter.WxAppTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by liuw on 2017/6/19.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public WxAppAuthenticationFilter wxAppAuthenticationFilter() {
        final WxAppAuthenticationFilter filter = new WxAppAuthenticationFilter();
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Bean
    public DefaultLogoutHandler defaultLogoutHandler() {
        final DefaultLogoutHandler defaultLogoutHandler = new DefaultLogoutHandler();
        return defaultLogoutHandler;
    }

    @Bean
    public WxAppTokenFilter wxAppTokenFilter() {
        final WxAppTokenFilter wxAppTokenFilter = new WxAppTokenFilter();
        return wxAppTokenFilter;
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/bower_components/**")
                .antMatchers("/i18n/**")
                .antMatchers("/content/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/v2/api-docs")
                .antMatchers("/test/**")
                .antMatchers("/h2-console/**");
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        // @formatter:off
        http.authorizeRequests().antMatchers("/w/weather").permitAll()// 请求动态密码（POST）
                .anyRequest().fullyAuthenticated().and().httpBasic().disable()
                .addFilterBefore( this.wxAppAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore( this.wxAppTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout()
                .permitAll()
                .addLogoutHandler(this.defaultLogoutHandler()).and().exceptionHandling()
                .authenticationEntryPoint(new Http401UnauthorizedEntryPoint()).and().rememberMe().disable().csrf()
                .disable() // cross site request forgery checks are not possible
                        // with static pages
                .headers().frameOptions().disable(); // H2 Console uses frames

        // @formatter:on
    }
}
