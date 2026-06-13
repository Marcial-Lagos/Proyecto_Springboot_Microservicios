package cl.duoc.msauth.service;

import cl.duoc.msauth.client.UsuarioSyncClient;
import cl.duoc.msauth.dto.*;
import cl.duoc.msauth.config.JwtUtil;
import cl.duoc.msauth.model.AuthUser;
import cl.duoc.msauth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class AuthService {
    private final AuthUserRepository repo;
    private final JwtUtil jwtUtil;
    private final UsuarioSyncClient usuarioSyncClient;
    private final BCryptPasswordEncoder encoder;

    public AuthResponse login(LoginRequest req) {
        AuthUser user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Credenciales inválidas");
        return new AuthResponse(jwtUtil.generateToken(user.getEmail(), user.getRol().name()),
                user.getEmail(),
                user.getRol().name());
    }

    public AuthResponse register(RegisterRequest req) {
        if (repo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email ya registrado");
        AuthUser user = AuthUser.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .build();
        repo.save(user);
        
        // Sincroniza con ms-usuarios (best-effort, no bloquea si falla)
        usuarioSyncClient.sincronizarUsuario(req.getNombre(), req.getEmail(), req.getPassword());
        return new AuthResponse(

                jwtUtil.generateToken(user.getEmail(), user.getRol().name()),
                user.getEmail(),
                user.getRol().name());
    }
}
