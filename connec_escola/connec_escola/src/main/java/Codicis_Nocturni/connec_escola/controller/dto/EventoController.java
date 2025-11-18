package Codicis_Nocturni.connec_escola.controller;

import Codicis_Nocturni.connec_escola.controller.dto.EventoRequest;
import Codicis_Nocturni.connec_escola.controller.dto.EventoResponse;
import Codicis_Nocturni.connec_escola.controller.dto.UserResponse; // Novo
import Codicis_Nocturni.connec_escola.model.Evento;
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EventoResponse>> getAllEvents() {
        List<EventoResponse> eventos = eventoService.findAllEvents();
        return ResponseEntity.ok(eventos);
    }

    // --- ENDPOINT ATUALIZADO (RSVP) ---
    /**
     * Endpoint para o usuário LOGADO confirmar presença (RSVP)
     */
    @PostMapping("/{id}/rsvp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addRsvp(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) { // Pega o usuário logado
        try {
            Evento eventoAtualizado = eventoService.addRsvp(id, usuarioLogado); // Passa o usuário para o serviço
            return ResponseEntity.ok(new EventoResponse(eventoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- NOVO ENDPOINT (LISTA DE PRESENTES) ---
    /**
     * Endpoint para ver quem confirmou presença.
     * (Qualquer usuário logado pode ver)
     */
    @GetMapping("/{id}/attendees")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponse>> getAttendees(@PathVariable Long id) {
        try {
            List<UserResponse> attendees = eventoService.getAttendees(id);
            return ResponseEntity.ok(attendees);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    public ResponseEntity<EventoResponse> createEvent(
            @Valid @RequestBody EventoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Evento novoEvento = eventoService.createEvent(request, usuarioLogado);
        return ResponseEntity.ok(new EventoResponse(novoEvento));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventoRequest request) {
        try {
            Evento eventoAtualizado = eventoService.updateEvent(id, request);
            return ResponseEntity.ok(new EventoResponse(eventoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventoService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}