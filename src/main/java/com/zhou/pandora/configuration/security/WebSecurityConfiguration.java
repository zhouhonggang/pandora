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
     * 注入Redis操作接口
     */
    @Resource
    RedisComponent redisComponent;
    /**
     * 重写用户登陆方法
     * loadUserByUsername(String username)
     */
    @Resource
    SysUserService userDetailsService;

    @Resource
    private AuthenticationTokenFilter authenticationTokenFilter;

    /**
     * Hash密码加密实现
     * @return PasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    /**
     * 重新定义校验相关信息
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
         * 在内存中模拟登陆账号信息
         * 账号: admin
         * 密码: 123456
         * 角色: ROLE_ADMIN
         * 示例:
         *   authentication.inMemoryAuthentication()
         *       .withUser("admin").password("$2a$10$2LwvPmOs0OXdgmSoFeguYujkbb1IRTwwidFKoa1G8X1yuaCgMKrpO").roles("ADMIN")
         *       .and()
         *       .withUser("user").password("$2a$10$QXGE42rLFH9RxPYYWuFdJePplUAHI1FJbTPWDsYOg3p0IpTJ50hgC").roles("USER")
         *       .and()
         *       .withUser("guest").password("$2a$10$hvSSYiOS6wf/FVAwfVF9j.wRVvj2S/FC9SDNRnRftvX03rxUqeMf2").roles("GUEST");
         */

        /*
         * 直接查询数据库账号信息
         * GeneralAuthenticationProvider: 普通登陆(账号密码匹配)
         */
        authentication.authenticationProvider(authenticationProvider());

        /*
         * 直接查询数据库账号信息
         * SMSAuthenticationProvider: 短信验证码登陆(手机号验证码匹配)
         * 示例:
         *  authentication.authenticationProvider(new SMSAuthenticationProvider(userDetailsService));
         */
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭csrf跨站点攻击
        http
            .csrf().disable();

        /* 配置服务器端Session生成方案
         *  Spring Security会话管理对象
         *  SessionCreationPolicy.ALWAYS        服务器一直创建Session对象
         *  SessionCreationPolicy.NEVER         不主动创建Session对象, 但Session存在则使用
         *  SessionCreationPolicy.IF_REQUIRED   只在需要的场景下动态创建Session对象
         *  SessionCreationPolicy.STATELESS     永不创建Session对象, 也不使用Session对象
         */
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 处理权限异常
        http
            .exceptionHandling()
            // 用户未授权
            .accessDeniedHandler((request, response, exception) -> {
                ResultData.response(response, ResultData.failure(ResultCode.USER_ACCESS_DENIED));
            })
            // 用户未登录
            .authenticationEntryPoint((request, response, exception) -> {
                ResultData.response(response, ResultData.failure(ResultCode.USER_NOT_LOGIN));
            });

        // 退出操作
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

        // 绑定过滤器
        http
            // 登陆过滤
            .addFilterAt(loginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            // 权限过滤
            .addFilterAfter(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    LoginAuthenticationFilter loginAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter();
        //登陆成功
        loginAuthenticationFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            //生成唯一token
            String token = UUID.randomUUID().toString();
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            //封装Redis用户信息(username: 账号, authorities: 权限集合, seconds: 续期时间)
            Map<String, Object> userInfo = Map.of(
                "username", authentication.getName(),
                    "authorities", authorities,
                    "seconds", 0);
            //保存用户信息到Redis Hash类型中
            redisComponent.hmset("user:token:"+token, userInfo, 60*60);
            //返回登陆成功信息
            ResultData.response(response, ResultData.success(ResultCode.USER_LOGIN_SUCCESS, token));
        });
        //登陆失败
        loginAuthenticationFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            //准备登陆相关异常信息
            ResultData result = ResultData.failure(ResultCode.USER_LOGIN_OTHER_ERROR);
            if(exception instanceof UsernameNotFoundException)
            {
                //用户名称无法找到异常
                result = ResultData.failure(ResultCode.USER_ACCOUNT_NOT_FOUND);
            }
            else if(exception instanceof BadCredentialsException)
            {
                //密码输入失败异常
                result = ResultData.failure(ResultCode.USER_PASS_INPUT_FAILURE);
            }
            else if(exception instanceof InternalAuthenticationServiceException)
            {
                //账号被锁定异常
                result = ResultData.failure(ResultCode.USER_ACCOUNT_LOCKED);
            }
            //返回JSON数据到客户端
            ResultData.response(response, result);
        });
        loginAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return loginAuthenticationFilter;
    }

}
