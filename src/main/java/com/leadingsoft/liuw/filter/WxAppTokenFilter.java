package com.leadingsoft.liuw.filter;

import com.leadingsoft.liuw.base.DefaultAuthenticationToken;
import com.leadingsoft.liuw.base.ResultDTO;
import com.leadingsoft.liuw.base.ResultError;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import com.leadingsoft.liuw.model.WxAppToken;
import com.leadingsoft.liuw.service.WxAppTokenRepository;
import com.leadingsoft.liuw.utils.JsonUtil;
import com.leadingsoft.liuw.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class WxAppTokenFilter extends GenericFilterBean {

	@Autowired
	private WxAppTokenRepository wxAppTokenRepository;

    @Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if(SecurityUtils.isAuthenticated()) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			try {
				HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
				String token = httpServletRequest.getHeader("token");
				log.info("！！！！！！！！！！！！！！token：" + token);
				if(StringUtils.isEmpty(token)) {
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				}

				WxAppToken wxAppToken = wxAppTokenRepository.findByValue(token);
				if(wxAppToken == null) {
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				}
				
				final DefaultAuthenticationToken auth = new DefaultAuthenticationToken();
				auth.setPrincipal(wxAppToken.getOpenId());
				auth.setAuthenticated(true);
				SecurityContextHolder.getContext().setAuthentication(auth);

				filterChain.doFilter(servletRequest, servletResponse);
			}
			catch (CustomRuntimeException ex) {
				//this.log.info("Security exception for user {} - {}", var7.getClaims().getSubject(), var7.getMessage());
				this.log.error("业务异常", ex);

				final HttpServletResponse response = ((HttpServletResponse)servletResponse);
				final ResultDTO rs = ResultDTO.failure(new ResultError(ex.getMessage(), "401"));
				response.setStatus(401);
				response.setContentType("application/json;charset=UTF-8");
				final PrintWriter writer = response.getWriter();
				writer.write(JsonUtil.pojoToJson(rs));
				writer.flush();// 51
				writer.close();
			}
			catch (Exception var7) {
				//this.log.info("Security exception for user {} - {}", var7.getClaims().getSubject(), var7.getMessage());
				this.log.error("异常", var7);

				final HttpServletResponse response = ((HttpServletResponse)servletResponse);
				final ResultDTO rs = ResultDTO.failure(new ResultError("系统异常", "500"));
				response.setStatus(401);
				response.setContentType("application/json;charset=UTF-8");
				final PrintWriter writer = response.getWriter();
				writer.write(JsonUtil.pojoToJson(rs));
				writer.flush();// 51
				writer.close();
			}
		}

	}
}
