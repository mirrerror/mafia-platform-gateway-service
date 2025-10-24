package md.faf223.mafiaplatformgatewayservice.configs;//package md.mirrerror.languagegames.configs;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.stream.Collectors;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class RequestDebugFilter extends OncePerRequestFilter {
//    private static final Logger log = LoggerFactory.getLogger(RequestDebugFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());
//        log.info("Headers: {}", Collections.list(request.getHeaderNames())
//                .stream()
//                .map(name -> name + ": " + request.getHeader(name))
//                .collect(Collectors.joining(", ")));
//
//        filterChain.doFilter(request, response);
//
//        log.info("Response status: {}", response.getStatus());
//    }
//}