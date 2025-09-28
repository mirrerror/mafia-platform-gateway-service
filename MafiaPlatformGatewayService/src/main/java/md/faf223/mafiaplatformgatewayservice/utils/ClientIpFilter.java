package md.faf223.mafiaplatformgatewayservice.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClientIpFilter extends OncePerRequestFilter {

    private static final ThreadLocal<String> CLIENT_IP = new ThreadLocal<>();

    public static String currentIp() {
        return CLIENT_IP.get();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    FilterChain chain)
            throws ServletException, IOException {
        try {
            CLIENT_IP.set(extractClientIp(req));
            chain.doFilter(req, resp);
        } finally {
            CLIENT_IP.remove();
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        header = request.getHeader("X-Real-IP");
        return header != null && !header.isBlank()
                ? header
                : request.getRemoteAddr();
    }

}

