package cl.duoc.msusuarios.service;

import cl.duoc.msusuarios.dto.UsuarioDTO;
import cl.duoc.msusuarios.dto.UsuarioRequestDTO;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias puras: UsuarioService es real; repositorio y encoder son mocks.
 * La estructura de cada caso es Given - When - Then.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UsuarioService service;

    // Faker genera datos de entrada válidos que no participan en una regla específica.
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarMapeaUsuariosADto() {
        // Given
        when(repository.findAll()).thenReturn(List.of(usuario(1L)));

        // When
        List<UsuarioDTO> response = service.listar();

        // Then
        assertEquals(1, response.size());
        assertEquals("cliente@foodexpress.cl", response.getFirst().getEmail());
    }

    @Test
    void buscarPorIdDevuelveUsuarioExistente() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(usuario(1L)));

        // When
        UsuarioDTO response = service.buscarPorId(1L);

        // Then
        assertEquals(1L, response.getId());
        assertEquals(Usuario.Rol.CLIENTE, response.getRol());
    }

    @Test
    void crearUsuarioEncriptaContrasenaAntesDeGuardar() {
        // Given
        UsuarioRequestDTO request = usuarioRequest("nuevo@foodexpress.cl");
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("hash-seguro");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(2L);
            return usuario;
        });

        // When
        UsuarioDTO response = service.crear(request);

        // Then: se captura exactamente el objeto que se intentó persistir.
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertEquals("hash-seguro", captor.getValue().getPassword());
        assertEquals(Usuario.Rol.CLIENTE, response.getRol());
    }

    @Test
    void crearUsuarioConEmailDuplicadoNoGuarda() {
        // Given: el email se mantiene fijo porque es la condición de negocio probada.
        UsuarioRequestDTO request = usuarioRequest("duplicado@foodexpress.cl");
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        // When + Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.crear(request));

        assertEquals("Email ya registrado", exception.getMessage());
        verify(repository, never()).save(any(Usuario.class));
        verifyNoInteractions(encoder);
    }

    @Test
    void eliminarUsuarioExistenteLlamaAlRepositorio() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        service.eliminar(1L);

        // Then
        verify(repository).deleteById(1L);
    }

    private Usuario usuario(Long id) {
        return Usuario.builder()
                .id(id)
                .nombre(faker.name().fullName())
                .email("cliente@foodexpress.cl")
                .password("hash")
                .rol(Usuario.Rol.CLIENTE)
                .telefono(faker.phoneNumber().cellPhone())
                .direccion(faker.address().streetAddress())
                .build();
    }

    private UsuarioRequestDTO usuarioRequest(String email) {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setNombre(faker.name().fullName());
        request.setEmail(email);
        request.setPassword("secreto123");
        request.setTelefono(faker.phoneNumber().cellPhone());
        request.setDireccion(faker.address().streetAddress());
        return request;
    }
}