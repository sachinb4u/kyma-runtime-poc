package com.sap.banking.positivepay.filters;
import com.sap.banking.positivepay.controller.beans.AuthenticationContext;
import com.sap.banking.positivepay.controller.beans.AuthenticationResponse;
import com.sap.banking.positivepay.service.BankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String TOKEN_KEY_HEADER = "TokenKey";
    private static final String X_CSRF_TOKEN_HEADER = "x-csrf-token";
    private static final String OCB_URI = "http://ec2-54-179-27-249.ap-southeast-1.compute.amazonaws.com:8080/platform-web" +
            "/odata/ns/authenticationservice/SecureUsers";

    private final BankingService bankingService;

    public AuthenticationFilter(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(checkUserAuthentication((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean checkUserAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String tokenKey = request.getHeader(TOKEN_KEY_HEADER);
        String csrfToken = request.getHeader(X_CSRF_TOKEN_HEADER);

        if(isEmpty(tokenKey) || isEmpty(csrfToken)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TOKEN_KEY_HEADER, tokenKey);
        httpHeaders.add(X_CSRF_TOKEN_HEADER, csrfToken);
        try{
            HttpEntity<String> requestEntity = new HttpEntity(httpHeaders);

            AuthenticationResponse authenticationResponse = bankingService.getSecureUser(tokenKey,csrfToken); // restTemplate.exchange(OCB_URI, HttpMethod.GET, requestEntity, String.class);

            ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());

            AuthenticationContext authenticationContext = ctx.getBean(AuthenticationContext.class);
            authenticationContext.setAuthenticationToken(tokenKey);
            authenticationContext.setCsrfToken(csrfToken);

            if(!HttpStatus.OK.equals(authenticationResponse.getStatus())){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
        }catch (Exception ex){
            logger.error("Error in calling OCB Authentication API", ex);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
          //  throw new UnauthorizedAccessException("Invalid TokenKey and / or CsrfToken");
        }
        return true;
    }

    private boolean isEmpty(String str){
        return str == null || "".equals(str.trim());
    }
}
