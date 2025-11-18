package Codicis_Nocturni.connec_escola.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet; // Nova importação
import java.util.List;
import java.util.Objects;
import java.util.Set; // Nova importação

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Email(message = "Formato de email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_responsaveis",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "responsavel_id")
    )
    private Set<Responsavel> responsaveis = new HashSet<>();

    // --- NOVO RELACIONAMENTO ---
    /**
     * Lista de eventos que este usuário confirmou presença.
     * mappedBy = "attendeesList" -> Diz que o 'Evento' é o dono deste relacionamento
     */
    @ManyToMany(mappedBy = "attendeesList")
    private Set<Evento> attendedEvents = new HashSet<>();

    // Construtor vazio
    public Usuario() {
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Set<Responsavel> getResponsaveis() { return responsaveis; }
    public void setResponsaveis(Set<Responsavel> responsaveis) { this.responsaveis = responsaveis; }

    // Getter/Setter para o novo relacionamento
    public Set<Evento> getAttendedEvents() { return attendedEvents; }
    public void setAttendedEvents(Set<Evento> attendedEvents) { this.attendedEvents = attendedEvents; }


    // --- Métodos do UserDetails (Spring Security) ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override
    public String getPassword() { return this.senha; }
    @Override
    public String getUsername() { return this.email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // --- Equals e HashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}