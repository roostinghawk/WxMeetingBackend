package com.leadingsoft.liuw.base;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultUrlRedirectStrategy implements RedirectStrategy {

    @Override
    public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, String redirectUrl)
            throws IOException {
        if (!StringUtils.hasText(redirectUrl)) {
            redirectUrl = request.getParameter("redirect");
        }
        if (StringUtils.hasText(redirectUrl)) {
            redirectUrl = response.encodeRedirectURL(redirectUrl);
            if (DefaultUrlRedirectStrategy.log.isDebugEnabled()) {
                DefaultUrlRedirectStrategy.log.debug("Redirecting to '" + redirectUrl + "'");
            }
            response.sendRedirect(redirectUrl);
        }
    }
}
