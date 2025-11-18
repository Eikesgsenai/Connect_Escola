package Codicis_Nocturni.connec_escola.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet; // Nova importação
import java.util.Objects;
import java.util.Set; // Nova importação

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    @NotNull
    @Future(message = "Data de início deve ser no futuro")
    private LocalDateTime dataInicio;

    @NotNull
    @Future(message = "Data de término deve ser no futuro")
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status;

    // --- CAMPO REMOVIDO ---
    // @Column(nullable = false)
    // private Integer attendees = 0; // Não precisamos mais disso

    // --- NOVO RELACIONAMENTO ---
    /**
     * A lista de Usuários que confirmaram presença neste evento.
     * Esta é a tabela de junção (Many-to-Many).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "evento_attendees", // Nome da nova tabela no banco
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> attendeesList = new HashSet<>();

    @Column
    private String mapLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id", nullable = false)
    @NotNull
    private Usuario organizador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    @NotNull
    private Local local;

    // Construtor vazio (JPA)
    public Evento() {
    }

    // --- Getters e Setters ---
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
    public String getMapLink() { return mapLink; }
    public void setMapLink(String mapLink) { this.mapLink = mapLink; }

    // Get/Set para o novo relacionamento
    public Set<Usuario> getAttendeesList() { return attendeesList; }
    public void setAttendeesList(Set<Usuario> attendeesList) { this.attendeesList = attendeesList; }

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