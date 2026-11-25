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
import java.util.List;
import java.util.Set;

/**
 * Validates Bearer JWTs before Gateway MVC forwards a request and propagates
 * the authenticated identity to downstream services.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String API = "/api/v1/";

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("POST".equals(request.getMethod()))
            return path.equals(API + "auth/login") || path.equals(API + "auth/register");
        if (!"GET".equals(request.getMethod()) && !"HEAD".equals(request.getMethod())) return false;
        return path.matches("/api/v1/productos(?:/[0-9]+|/categoria/[^/;]+)?")
                || path.equals("/actuator/health") || path.equals("/doc/swagger-ui.html")
                || path.startsWith("/swagger-ui/") || path.startsWith("/doc/swagger-ui/")
                || path.startsWith("/webjars/") || path.equals("/v3/api-docs")
                || path.equals("/v3/api-docs/swagger-config")
                || path.matches("/(auth|usuarios|productos|pedidos|pagos|envios|inventario|notificaciones|resenas|cupones)/v3/api-docs");
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
        Long id = jwtUtil.getUsuarioId(token);
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (id == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Vuelve a iniciar sesión");
            return;
        }
        if (!"ADMIN".equals(rol) && !permitidoParaCliente(method, path, id)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Acceso restringido");
            return;
        }
        filterChain.doFilter(new IdentityHeaderRequestWrapper(request, email, rol, id.toString()), response);
    }

    private boolean permitidoParaCliente(String method, String path, long id) {
        if (path.equals(API + "directos") || path.startsWith(API + "directos/")) return true;
        if (path.matches("/api/v1/voz/(general|ranked)(?:/estado|/senal)?")) return true;
        if (path.equals(API + "usuarios/" + id)) return method.equals("GET") || method.equals("PATCH");
        if (path.equals(API + "pedidos")) return method.equals("POST");
        if (!method.equals("GET")) return false;
        return path.equals(API + "pedidos/usuario/" + id)
                || path.equals(API + "notificaciones/usuario/" + id)
                || path.equals(API + "notificaciones/usuario/" + id + "/no-leidas")
                || path.equals(API + "resenas/usuario/" + id)
                || path.matches("/api/v1/resenas/producto/[0-9]+(?:/promedio)?")
                || path.matches("/api/v1/cupones/[^/;]+/validar");
    }

    private static final class IdentityHeaderRequestWrapper extends HttpServletRequestWrapper {
        private final String email;
        private final String rol;

        private final String userId;

        private IdentityHeaderRequestWrapper(HttpServletRequest request, String email, String rol, String userId) {
            super(request);
            this.email = email;
            this.rol = rol;
            this.userId = userId;
        }

        @Override
        public String getHeader(String name) {
            if ("X-User-Id".equalsIgnoreCase(name)) return userId;
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
            if (!name.equalsIgnoreCase("X-User-Id") && !name.equalsIgnoreCase("X-User-Email")
                    && !name.equalsIgnoreCase("X-User-Rol")) return super.getHeaders(name);
            String value = getHeader(name);
            return value == null
                    ? Collections.emptyEnumeration()
                    : Collections.enumeration(List.of(value));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            Enumeration<String> originals = super.getHeaderNames();
            while (originals.hasMoreElements()) {
                names.add(originals.nextElement());
            }
            names.add("X-User-Id");
            names.add("X-User-Email");
            names.add("X-User-Rol");
            return Collections.enumeration(names);
        }
    }
}
