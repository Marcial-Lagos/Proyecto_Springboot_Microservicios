package cl.duoc.msauth.service;

import cl.duoc.msauth.client.UsuarioSyncClient;
import cl.duoc.msauth.config.JwtUtil;
import cl.duoc.msauth.dto.AuthResponse;
import cl.duoc.msauth.dto.LoginRequest;
import cl.duoc.msauth.dto.RegisterRequest;
import cl.duoc.msauth.model.AuthUser;
import cl.duoc.msauth.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthUserRepository repository;
    @Mock private JwtUtil jwtUtil;
    @Mock private UsuarioSyncClient usuarioSyncClient;
    @Mock private BCryptPasswordEncoder encoder;
    @InjectMocks private AuthService service;

    @Test
    void loginExitosoEntregaTokenYDatosDelUsuario() {
        LoginRequest request = loginRequest("cliente@foodexpress.cl", "secreto123");
        AuthUser user = AuthUser.builder().id(1L).email(request.getEmail()).password("hash").rol(AuthUser.Rol.CLIENTE).build();
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail(), user.getRol().name())).thenReturn("jwt-valido");

        AuthResponse response = service.login(request);

        assertEquals("jwt-valido", response.getToken());
        assertEquals("cliente@foodexpress.cl", response.getEmail());
        assertEquals("CLIENTE", response.getRol());
    }

    @Test
    void loginConCorreoInexistenteLanzaErrorDeCredenciales() {
        LoginRequest request = loginRequest("inexistente@foodexpress.cl", "secreto123");
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.login(request));

        assertEquals("Credenciales inválidas", exception.getMessage());
        verifyNoInteractions(encoder, jwtUtil);
    }

    @Test
    void loginConContrasenaIncorrectaLanzaErrorDeCredenciales() {
        LoginRequest request = loginRequest("cliente@foodexpress.cl", "incorrecta");
        AuthUser user = AuthUser.builder().id(1L).email(request.getEmail()).password("hash").rol(AuthUser.Rol.CLIENTE).build();
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.login(request));

        assertEquals("Credenciales inválidas", exception.getMessage());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void registroGuardaCredencialesEncriptadasYSincronizaUsuario() {
        RegisterRequest request = registerRequest();
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("hash-seguro");
        when(repository.save(any(AuthUser.class))).thenAnswer(invocation -> {
            AuthUser user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });
        when(jwtUtil.generateToken(request.getEmail(), AuthUser.Rol.CLIENTE.name())).thenReturn("jwt-nuevo");

        AuthResponse response = service.register(request);

        ArgumentCaptor<AuthUser> captor = ArgumentCaptor.forClass(AuthUser.class);
        verify(repository).save(captor.capture());
        assertEquals("hash-seguro", captor.getValue().getPassword());
        assertEquals(AuthUser.Rol.CLIENTE, captor.getValue().getRol());
        verify(usuarioSyncClient).sincronizarUsuario(request.getNombre(), request.getEmail(), request.getPassword());
        assertEquals("jwt-nuevo", response.getToken());
    }

    @Test
    void registroConEmailDuplicadoNoGuardaNiSincroniza() {
        RegisterRequest request = registerRequest();
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.register(request));

        assertEquals("Email ya registrado", exception.getMessage());
        verify(repository, never()).save(any(AuthUser.class));
        verifyNoInteractions(usuarioSyncClient, encoder, jwtUtil);
    }

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    private RegisterRequest registerRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Marcial Lagos");
        request.setEmail("marcial@foodexpress.cl");
        request.setPassword("secreto123");
        return request;
    }
}
