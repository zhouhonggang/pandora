package com.zhou.pandora.configuration.security;

import com.zhou.pandora.component.commons.RedisComponent;
import com.zhou.pandora.component.result.ResultCode;
import com.zhou.pandora.component.result.ResultData;
import com.zhou.pandora.configuration.security.filter.AuthenticationTokenFilter;
import com.zhou.pandora.configuration.security.filter.LoginAuthenticationFilter;
import com.zhou.pandora.modules.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Program: pandora
 * @Created: 2021/5/12 10:50
 * @Author: ZhouHongGang
 * @Description: TODO
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${token.tokenHead}")
    String tokenHead = "Bearer ";
    @Value("${token.tokenHeader}")
    String tokenHeader = "Authorization";
    @Value("${token.redisToken}")
    String redisToken = "user:token:";

    /**
     * ??????Redis????????????
     */
    @Resource
    RedisComponent redisComponent;
    /**
     * ????????????????????????
     * loadUserByUsername(String username)
     */
    @Resource
    SysUserService userDetailsService;

    @Resource
    private AuthenticationTokenFilter authenticationTokenFilter;

    /**
     * Hash??????????????????
     * @return PasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    /**
     * ??????????????????????????????
     * @return DaoAuthenticationProvider
     */
    DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authentication) throws Exception {
        /*
         * ????????????????????????????????????
         * ??????: admin
         * ??????: 123456
         * ??????: ROLE_ADMIN
         * ??????:
         *   authentication.inMemoryAuthentication()
         *       .withUser("admin").password("$2a$10$2LwvPmOs0OXdgmSoFeguYujkbb1IRTwwidFKoa1G8X1yuaCgMKrpO").roles("ADMIN")
         *       .and()
         *       .withUser("user").password("$2a$10$QXGE42rLFH9RxPYYWuFdJePplUAHI1FJbTPWDsYOg3p0IpTJ50hgC").roles("USER")
         *       .and()
         *       .withUser("guest").password("$2a$10$hvSSYiOS6wf/FVAwfVF9j.wRVvj2S/FC9SDNRnRftvX03rxUqeMf2").roles("GUEST");
         */

        /*
         * ?????????????????????????????????
         * GeneralAuthenticationProvider: ????????????(??????????????????)
         */
        authentication.authenticationProvider(authenticationProvider());

        /*
         * ?????????????????????????????????
         * SMSAuthenticationProvider: ?????????????????????(????????????????????????)
         * ??????:
         *  authentication.authenticationProvider(new SMSAuthenticationProvider(userDetailsService));
         */
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ??????csrf???????????????
        http
            .csrf().disable();

        /* ??????????????????Session????????????
         *  Spring Security??????????????????
         *  SessionCreationPolicy.ALWAYS        ?????????????????????Session??????
         *  SessionCreationPolicy.NEVER         ???????????????Session??????, ???Session???????????????
         *  SessionCreationPolicy.IF_REQUIRED   ????????????????????????????????????Session??????
         *  SessionCreationPolicy.STATELESS     ????????????Session??????, ????????????Session??????
         */
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // ??????????????????
        http
            .exceptionHandling()
            // ???????????????
            .accessDeniedHandler((request, response, exception) -> {
                ResultData.response(response, ResultData.failure(ResultCode.USER_ACCESS_DENIED));
            })
            // ???????????????
            .authenticationEntryPoint((request, response, exception) -> {
                ResultData.response(response, ResultData.failure(ResultCode.USER_NOT_LOGIN));
            });

        // ????????????
        http
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler((request, response, authentication) -> {
                String tokenHeader = request.getHeader(this.tokenHeader);
                if (StringUtils.hasLength(tokenHeader) && tokenHeader.startsWith(tokenHead)) {
                    final String authToken = tokenHeader.substring(tokenHead.length());
                    redisComponent.del(authToken);
                }
                ResultData.response(response, ResultData.failure(ResultCode.USER_LOGOUT_SUCCESS));
            });

        // ???????????????
        http
            // ????????????
            .addFilterAt(loginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            // ????????????
            .addFilterAfter(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    LoginAuthenticationFilter loginAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter();
        //????????????
        loginAuthenticationFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            //????????????token
            String token = UUID.randomUUID().toString();
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            //??????Redis????????????(username: ??????, authorities: ????????????, seconds: ????????????)
            Map<String, Object> userInfo = Map.of(
                "username", authentication.getName(),
                    "authorities", authorities,
                    "seconds", 0);
            //?????????????????????Redis Hash?????????
            redisComponent.hmset("user:token:"+token, userInfo, 60*60);
            //????????????????????????
            ResultData.response(response, ResultData.success(ResultCode.USER_LOGIN_SUCCESS, token));
        });
        //????????????
        loginAuthenticationFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            //??????????????????????????????
            ResultData result = ResultData.failure(ResultCode.USER_LOGIN_OTHER_ERROR);
            if(exception instanceof UsernameNotFoundException)
            {
                //??????????????????????????????
                result = ResultData.failure(ResultCode.USER_ACCOUNT_NOT_FOUND);
            }
            else if(exception instanceof BadCredentialsException)
            {
                //????????????????????????
                result = ResultData.failure(ResultCode.USER_PASS_INPUT_FAILURE);
            }
            else if(exception instanceof InternalAuthenticationServiceException)
            {
                //?????????????????????
                result = ResultData.failure(ResultCode.USER_ACCOUNT_LOCKED);
            }
            //??????JSON??????????????????
            ResultData.response(response, result);
        });
        loginAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return loginAuthenticationFilter;
    }

}
