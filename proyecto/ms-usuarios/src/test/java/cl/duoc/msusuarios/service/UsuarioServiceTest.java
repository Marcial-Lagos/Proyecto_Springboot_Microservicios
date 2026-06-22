package cl.duoc.msusuarios.service;

import cl.duoc.msusuarios.dto.UsuarioDTO;
import cl.duoc.msusuarios.dto.UsuarioRequestDTO;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository repository;
    @Mock private BCryptPasswordEncoder encoder;
    @InjectMocks private UsuarioService service;

    @Test
    void listarMapeaUsuariosADto() {
        when(repository.findAll()).thenReturn(List.of(usuario(1L)));

        List<UsuarioDTO> response = service.listar();

        assertEquals(1, response.size());
        assertEquals("cliente@foodexpress.cl", response.get(0).getEmail());
    }

    @Test
    void buscarPorIdDevuelveUsuarioExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario(1L)));

        UsuarioDTO response = service.buscarPorId(1L);

        assertEquals(1L, response.getId());
        assertEquals(Usuario.Rol.CLIENTE, response.getRol());
    }

    @Test
    void crearUsuarioEncriptaContrasenaAntesDeGuardar() {
        UsuarioRequestDTO request = usuarioRequest();
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("hash-seguro");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        UsuarioDTO response = service.crear(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertEquals("hash-seguro", captor.getValue().getPassword());
        assertEquals(Usuario.Rol.CLIENTE, response.getRol());
    }

    @Test
    void crearUsuarioConEmailDuplicadoNoGuarda() {
        UsuarioRequestDTO request = usuarioRequest();
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.crear(request));

        assertEquals("Email ya registrado", exception.getMessage());
        verify(repository, never()).save(any(Usuario.class));
        verifyNoInteractions(encoder);
    }

    @Test
    void eliminarUsuarioExistenteLlamaAlRepositorio() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }

    private Usuario usuario(Long id) {
        return Usuario.builder().id(id).nombre("Cliente FoodExpress").email("cliente@foodexpress.cl")
                .password("hash").rol(Usuario.Rol.CLIENTE).telefono("912345678").direccion("Av. Principal 123").build();
    }

    private UsuarioRequestDTO usuarioRequest() {
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setNombre("Cliente FoodExpress");
        request.setEmail("cliente@foodexpress.cl");
        request.setPassword("secreto123");
        request.setTelefono("912345678");
        request.setDireccion("Av. Principal 123");
        return request;
    }
}
