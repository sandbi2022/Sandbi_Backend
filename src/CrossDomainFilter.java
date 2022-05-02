

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class CrossDomainFilter
 */
@WebFilter("/CrossDomainFilter")
public class CrossDomainFilter implements Filter{
    public void init(FilterConfig filterConfig) throws ServletException {}
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    	HttpServletResponse resp = (HttpServletResponse)servletResponse;
        resp.setHeader("Access-Control-Allow-Origin", "https://sandbi.us:3000");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(servletRequest,servletResponse);
    }
    public void destroy() {}
}

