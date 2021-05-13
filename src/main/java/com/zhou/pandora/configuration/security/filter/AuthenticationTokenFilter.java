package com.zhou.pandora.configuration.security.filter;

import com.zhou.pandora.component.commons.RedisComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Program: pandora
 * @Created: 2021/5/12 16:32
 * @Author: ZhouHongGang
 * @Description: TODO
 */
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${token.tokenHead}")
    String tokenHead = "Bearer ";
    @Value("${token.tokenHeader}")
    String tokenHeader = "Authorization";
    @Value("${token.redisToken}")
    String redisToken = "user:token:";

    @Resource
    RedisComponent redisComponent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //获取请求header中token信息
        String tokenHeader = request.getHeader(this.tokenHeader);
        //验证token是否符合规范
        if (StringUtils.hasLength(tokenHeader) && tokenHeader.startsWith(tokenHead))
        {
            //解析获取完整的token值
            final String authToken = tokenHeader.substring(tokenHead.length());
            //判断Redis中是否存在token
            if(StringUtils.hasLength(authToken) && redisComponent.exists(redisToken+authToken))
            {
                //从Redis中获取登陆账号
                String username = (String)redisComponent.hget(redisToken+authToken, "username");
                if (StringUtils.hasLength(username) && SecurityContextHolder.getContext().getAuthentication() == null)
                {
                    String tokenKey = redisToken+authToken;
                    //从Redis中读取用户权限列表
                    List<String> authorities =  (List<String>)redisComponent.hget(tokenKey, "authorities");
                    //封装用户权限到认证列表中
                    Collection<GrantedAuthority> authoritiesList = authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, authoritiesList));
                    //更新用户过期时间
                    long seconds = redisComponent.getExpire(tokenKey);
                    //获取上次更新时间
                    int expire = (Integer)redisComponent.hget(tokenKey, "seconds");
                    //更新日期
                    long updateTime = seconds + expire;
                    redisComponent.expire(tokenKey, updateTime+60*60);
                    redisComponent.hset(tokenKey, "seconds", updateTime);
                }
            }
        }
        chain.doFilter(request, response);
    }

}
