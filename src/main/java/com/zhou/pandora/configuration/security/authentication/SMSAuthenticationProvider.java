package com.zhou.pandora.configuration.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Program: pandora
 * @Created: 2021/5/12 13:47
 * @Author: ZhouHongGang
 * @Description: 短信验证
 */
public class SMSAuthenticationProvider extends DaoAuthenticationProvider {

    public SMSAuthenticationProvider(UserDetailsService userDetailsService) {
        super();
        setUserDetailsService(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        /*
         *  自定义自己的短信验证流程
         */
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
