package Codicis_Nocturni.connec_escola.service;

import Codicis_Nocturni.connec_escola.controller.dto.EventoRequest;
import Codicis_Nocturni.connec_escola.controller.dto.EventoResponse;
import Codicis_Nocturni.connec_escola.controller.dto.UserResponse; // Novo
import Codicis_Nocturni.connec_escola.model.Evento;
import Codicis_Nocturni.connec_escola.model.Local;
import Codicis_Nocturni.connec_escola.model.StatusEvento;
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.repository.EventoRepository;
import Codicis_Nocturni.connec_escola.repository.LocalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final LocalRepository localRepository;

    public EventoService(EventoRepository eventoRepository, LocalRepository localRepository) {
        this.eventoRepository = eventoRepository;
        this.localRepository = localRepository;
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> findAllEvents() {
        List<Evento> eventos = eventoRepository.findAll();
        return eventos.stream()
                .map(EventoResponse::new)
                .collect(Collectors.toList());
    }

    // --- MÉTODO ATUALIZADO (RSVP) ---
    /**
     * Adiciona um Usuário à lista de confirmados (attendees) de um evento.
     */
    @Transactional
    public Evento addRsvp(Long eventId, Usuario usuario) {
        Evento evento = eventoRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventId));

        // Adiciona o usuário à lista de presença do evento
        evento.getAttendeesList().add(usuario);

        return eventoRepository.save(evento);
    }

    // --- NOVO MÉTODO ---
    /**
     * Busca a lista de nomes de quem confirmou presença.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAttendees(Long eventId) {
        Evento evento = eventoRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventId));

        // Converte a lista de Usuario para UserResponse (sem senha)
        return evento.getAttendeesList().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Evento createEvent(EventoRequest request, Usuario organizador) {
        Local local = localRepository.findByNome(request.getLocalNome())
                .orElseGet(() -> {
                    Local novoLocal = new Local(request.getLocalNome(), null);
                    return localRepository.save(novoLocal);
                });

        Evento evento = new Evento();
        evento.setTitulo(request.getTitle());
        evento.setDescricao(request.getDescription());
        evento.setDataInicio(request.getDataInicio());
        evento.setDataFim(request.getDataFim());
        evento.setMapLink(request.getMapLink());
        evento.setLocal(local);
        evento.setOrganizador(organizador);
        evento.setStatus(StatusEvento.AGENDADO);
        // Não definimos mais 'attendees = 0', pois a lista 'attendeesList' já começa vazia.

        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento updateEvent(Long eventId, EventoRequest request) {
        Evento evento = eventoRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventId));

        Local local = localRepository.findByNome(request.getLocalNome())
                .orElseGet(() -> {
                    Local novoLocal = new Local(request.getLocalNome(), null);
                    return localRepository.save(novoLocal);
                });

        evento.setTitulo(request.getTitle());
        evento.setDescricao(request.getDescription());
        evento.setDataInicio(request.getDataInicio());
        evento.setDataFim(request.getDataFim());
        evento.setMapLink(request.getMapLink());
        evento.setLocal(local);

        return eventoRepository.save(evento);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventoRepository.existsById(eventId)) {
            throw new RuntimeException("Evento não encontrado com ID: " + eventId);
        }
        eventoRepository.deleteById(eventId);
    }
}