package Codicis_Nocturni.connec_escola.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// DTO (Caixinha) para RECEBER dados do formulário de evento do frontend
public class EventoRequest {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    @NotNull(message = "A data de início é obrigatória")
    @Future(message = "A data de início deve ser no futuro")
    private LocalDateTime dataInicio;

    @NotNull(message = "A data de término é obrigatória")
    @Future(message = "A data de término deve ser no futuro")
    private LocalDateTime dataFim;

    @NotBlank(message = "O nome do local é obrigatório")
    private String localNome; // Recebemos o NOME do local (ex: "Auditório")

    private String description;
    private String mapLink;

    // Getters e Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getLocalNome() { return localNome; }
    public void setLocalNome(String localNome) { this.localNome = localNome; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMapLink() { return mapLink; }
    public void setMapLink(String mapLink) { this.mapLink = mapLink; }
}