package cl.duoc.msauth.service;

import cl.duoc.msauth.client.UsuarioSyncClient;
import cl.duoc.msauth.dto.*;
import cl.duoc.msauth.config.JwtUtil;
import cl.duoc.msauth.model.AuthUser;
import cl.duoc.msauth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor

public class AuthService {
    private final AuthUserRepository repo;
    private final JwtUtil jwtUtil;
    private final UsuarioSyncClient usuarioSyncClient;
    private final BCryptPasswordEncoder encoder;

    public AuthResponse login(LoginRequest req) {
        AuthUser user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        return autenticar(user, user.getEmail().split("@")[0], req.getPassword());
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (repo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        AuthUser user = AuthUser.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .build();
        repo.save(user);

        return autenticar(user, req.getNombre(), req.getPassword());
    }

    private AuthResponse autenticar(AuthUser user, String nombre, String password) {
        Long id;
        try {
            id = usuarioSyncClient.sincronizarUsuario(nombre, user.getEmail(), password);
        } catch (RuntimeException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de perfiles no está disponible", error);
        }
        String rol = user.getRol().name();
        return new AuthResponse(jwtUtil.generateToken(user.getEmail(), rol, id), "Bearer", user.getEmail(), rol, id);
    }
}
