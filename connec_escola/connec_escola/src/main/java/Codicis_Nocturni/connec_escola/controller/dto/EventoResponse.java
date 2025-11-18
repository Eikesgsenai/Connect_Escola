package Codicis_Nocturni.connec_escola.controller.dto;

import Codicis_Nocturni.connec_escola.model.Evento;
import java.time.format.DateTimeFormatter;

// DTO (Caixinha) para enviar os dados do Evento ao frontend
public class EventoResponse {

    private Long id;
    private String title;
    private String date; // Data formatada (ex: "2025-10-05")
    private String time; // Hora formatada (ex: "19:00")
    private String timeFim; // --- NOVO CAMPO --- Hora de Fim (ex: "21:00")
    private String local; // Apenas o nome do local
    private String description;
    private String mapLink;
    private int attendeeCount; // MUDANÇA AQUI
    private String organizerName;

    // Construtor que "traduz" a Entidade complexa para este DTO simples
    public EventoResponse(Evento evento) {
        this.id = evento.getId();
        this.title = evento.getTitulo();

        // Formata o LocalDateTime em duas Strings separadas
        this.date = evento.getDataInicio().format(DateTimeFormatter.ISO_LOCAL_DATE); // yyyy-MM-dd
        this.time = evento.getDataInicio().format(DateTimeFormatter.ofPattern("HH:mm")); // HH:mm
        this.timeFim = evento.getDataFim().format(DateTimeFormatter.ofPattern("HH:mm")); // --- NOVO CAMPO ---

        this.local = evento.getLocal().getNome(); // Pega só o nome do Local
        this.description = evento.getDescricao();
        this.mapLink = evento.getMapLink();

        this.attendeeCount = evento.getAttendeesList().size();

        this.organizerName = evento.getOrganizador().getNome();
    }

    // Getters (O frontend precisa disso para ler os dados)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getTimeFim() { return timeFim; } // --- NOVO GETTER ---
    public String getLocal() { return local; }
    public String getDescription() { return description; }
    public String getMapLink() { return mapLink; }
    public int getAttendeeCount() { return attendeeCount; } // --- GETTER ATUALIZADO ---
    public String getOrganizerName() { return organizerName; }
}