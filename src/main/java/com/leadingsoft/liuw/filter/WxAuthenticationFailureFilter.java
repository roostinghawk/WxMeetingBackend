package com.leadingsoft.liuw.filter;

import com.leadingsoft.liuw.base.ResultDTO;
import com.leadingsoft.liuw.base.ResultError;
import com.leadingsoft.liuw.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class WxAuthenticationFailureFilter implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(WxAuthenticationFailureFilter.class);

    @SuppressWarnings("rawtypes")
    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException exception) throws IOException, ServletException {
        ResultError error = null;
        if (!(exception instanceof UsernameNotFoundException) && !(exception instanceof BadCredentialsException)) {
            if (exception instanceof LockedException) {
                error = new ResultError("401", "帐户已锁定.", (String) null);
            } else if (exception instanceof DisabledException) {
                error = new ResultError("401", "帐户已禁用.", (String) null);
            } else if (exception instanceof AccountExpiredException) {
                error = new ResultError("401", "帐户已过期.", (String) null);
            }
        } else {
            error = new ResultError("401", "用户名或密码错误.", "username&password");
        }

        if (error != null) {
            if (WxAuthenticationFailureFilter.log.isInfoEnabled()) {
                WxAuthenticationFailureFilter.log.info("用户登录失败，" + error.getErrmsg());
            }

            final ResultDTO rs = ResultDTO.failure(new ResultError[] {error });
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            final PrintWriter writer = response.getWriter();
            writer.write(JsonUtils.pojoToJson(rs));
            writer.flush();// 51
            writer.close();
        } else {
            if (WxAuthenticationFailureFilter.log.isInfoEnabled()) {
                WxAuthenticationFailureFilter.log.info("用户登录失败", exception);
            }

            response.sendError(401, "认证失败: " + exception.getMessage());
        }

    }
}
