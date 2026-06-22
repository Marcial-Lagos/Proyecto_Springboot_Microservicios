package cl.duoc.apigateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates Bearer JWTs before Gateway MVC forwards a request and propagates
 * the authenticated identity to downstream services.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/swagger-ui",
            "/v3/api-docs",
            "/webjars",
            "/actuator");

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith)
                || path.endsWith("/v3/api-docs")
                || path.contains("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token Bearer requerido");
            return;
        }

        String token = authorization.substring(7);
        if (!jwtUtil.isValid(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token inválido o expirado");
            return;
        }

        String email = jwtUtil.getEmail(token);
        String rol = jwtUtil.getRol(token);
        filterChain.doFilter(new IdentityHeaderRequestWrapper(request, email, rol), response);
    }

    private static final class IdentityHeaderRequestWrapper extends HttpServletRequestWrapper {
        private final String email;
        private final String rol;

        private IdentityHeaderRequestWrapper(HttpServletRequest request, String email, String rol) {
            super(request);
            this.email = email;
            this.rol = rol;
        }

        @Override
        public String getHeader(String name) {
            if ("X-User-Email".equalsIgnoreCase(name)) {
                return email;
            }
            if ("X-User-Rol".equalsIgnoreCase(name)) {
                return rol;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String value = getHeader(name);
            return value == null
                    ? Collections.emptyEnumeration()
                    : Collections.enumeration(List.of(value));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>();
            Enumeration<String> originals = super.getHeaderNames();
            while (originals.hasMoreElements()) {
                names.add(originals.nextElement());
            }
            names.add("X-User-Email");
            names.add("X-User-Rol");
            return Collections.enumeration(names);
        }
    }
}
