package cl.duoc.msauth.controller;

import cl.duoc.msauth.dto.AuthResponse;
import cl.duoc.msauth.dto.LoginRequest;
import cl.duoc.msauth.dto.RegisterRequest;
import cl.duoc.msauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor        

@Tag(name = "Autentificacion", description = "Operaciones de autentificacion y manejo de usuario")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    @Operation(summary = "Obtener el token JWT", description = "Entrega el token JWT validando user y pass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuario o pass son invalidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de login inválidos", content = @Content)
    })
    public ResponseEntity<EntityModel<AuthResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Estos son ejemplos de datos del request", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "username": "admin",
                      "password": "1234"
                    }
                    """))) @Valid @RequestBody LoginRequest req) {

        // Cambiado al formato HATEOAS del profesor usando tu servicio y DTO
        AuthResponse response = service.login(req);
        EntityModel<AuthResponse> recurso = EntityModel.of(response);

        recurso.add(linkTo(methodOn(AuthController.class).login(req)).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<EntityModel<AuthResponse>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Estos son ejemplos de datos del request", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "username": "nuevoUsuario",
                      "password": "password123"
                    }
                    """))) @Valid @RequestBody RegisterRequest req) {

        // Manteniendo tu endpoint de registro pero con la envoltura HATEOAS del
        // profe
        AuthResponse response = service.register(req);
        EntityModel<AuthResponse> recurso = EntityModel.of(response);

        recurso.add(linkTo(methodOn(AuthController.class).register(req)).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }
}