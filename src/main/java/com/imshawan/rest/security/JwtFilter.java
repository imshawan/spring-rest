package com.imshawan.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.ApplicationContext;

import com.imshawan.rest.model.User;
import com.imshawan.rest.response.HTTPError;
import com.imshawan.rest.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ApplicationContext applicationContext;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public final List<String> UNAUTHENTICATED_ROUTES = List.of(
            "/api/users/register",
            "/api/users/signin",
            "/uploads/**"
            );

    private UserService getUserService() {
        return applicationContext.getBean(UserService.class);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        HTTPError HTTPError = new HTTPError(request, response);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            HTTPError.setMessage("Missing or invalid Authorization header");
            HTTPError.setStatus(HttpStatus.FORBIDDEN.value());

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(HTTPError.toJson());
            return;
        }

        try {
            String token = authorizationHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            Optional<User> userDetails = getUserService().getUserByUsername(username);

            if (username != null && jwtUtil.validateToken(token, username)) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails.get(), null, userDetails.get().getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                HTTPError.setMessage("Invalid or expired token");
                response.getWriter().write(HTTPError.toJson());
                return;
            }
        } catch (MalformedJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            HTTPError.setMessage("Malformed JWT");
            response.getWriter().write(HTTPError.toJson());
            return;
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            HTTPError.setMessage("Token expired");
            response.getWriter().write(HTTPError.toJson());
            return;
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            HTTPError.setMessage("Unexpected error: " + e.getMessage());
            response.getWriter().write(HTTPError.toJson());
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return UNAUTHENTICATED_ROUTES.stream().anyMatch(route -> pathMatcher.match(route, path));
    }

}
