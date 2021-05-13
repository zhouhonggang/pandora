package com.zhou.pandora.configuration.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhou.pandora.modules.system.user.entity.SysUser;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Program: pandora
 * @Created: 2021/5/12 15:56
 * @Author: ZhouHongGang
 * @Description: 重写登陆过滤器
 */
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        SysUser user = new ObjectMapper().readValue(request.getInputStream(), SysUser.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPass()));
    }

}
