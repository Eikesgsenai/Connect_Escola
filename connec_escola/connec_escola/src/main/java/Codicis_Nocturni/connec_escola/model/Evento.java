package Codicis_Nocturni.connec_escola.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime; // Usamos LocalDateTime para data e hora
import java.util.Objects;

/**
 * A entidade principal do sistema.
 * Representa um agendamento no calendário.
 */
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000) // Uma descrição mais longa
    private String descricao;

    @NotNull(message = "A data de início é obrigatória")
    @Future(message = "A data de início deve ser no futuro")
    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @NotNull(message = "A data de término é obrigatória")
    @Future(message = "A data de término deve ser no futuro")
    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING) // Armazena o status como "AGENDADO", "CANCELADO", etc.
    @Column(nullable = false)
    private StatusEvento status;

    /*
     * --- RELACIONAMENTOS ---
     * Aqui é onde conectamos as entidades.
     */

    // Muitos Eventos podem ser criados por um Usuário (organizador).
    @ManyToOne(fetch = FetchType.LAZY) // LAZY: Só carrega o usuário quando formos usar
    @JoinColumn(name = "organizador_id", nullable = false) // Chave estrangeira no banco
    @NotNull
    private Usuario organizador;

    // Muitos Eventos podem ocorrer em um Local.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false) // Chave estrangeira no banco
    @NotNull
    private Local local;

    // Construtor padrão do JPA
    public Evento() {
    }

    // --- Getters e Setters ---
    // (Gere todos automaticamente na sua IDE)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }
    public Usuario getOrganizador() { return organizador; }
    public void setOrganizador(Usuario organizador) { this.organizador = organizador; }
    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }

    // --- Equals e HashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}