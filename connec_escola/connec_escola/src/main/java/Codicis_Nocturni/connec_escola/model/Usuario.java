package Codicis_Nocturni.connec_escola.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import Codicis_Nocturni.connec_escola.model.Role;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usuarios") // Nome da tabela no banco
public class Usuario implements UserDetails { // Implementa UserDetails para o Spring Security

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Email(message = "Formato de email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Column(unique = true, nullable = false) // Email deve ser único
    private String email;

    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    // A senha pode ser nula se o login for via Google
    private String senha;

    @Enumerated(EnumType.STRING) // Armazena o enum como "ALUNO", "PROFESSOR"
    @Column(nullable = false)
    private Role role;

    /**
     * Relacionamento Muitos-para-Muitos com Responsaveis.
     * Um Aluno (Usuario) pode ter vários Responsaveis.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_responsaveis", // Nome da tabela de ligação
            joinColumns = @JoinColumn(name = "usuario_id"), // Coluna que aponta para Usuario
            inverseJoinColumns = @JoinColumn(name = "responsavel_id") // Coluna que aponta para Responsavel
    )
    private Set<Responsavel> responsaveis = new HashSet<>();

    // Construtor vazio (obrigatório pelo JPA)
    public Usuario() {
    }

    // --- Getters e Setters ---
    // (Lembre-se de gerar todos na sua IDE: Alt+Insert -> Getters and Setters)
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


    // --- Métodos do UserDetails (Spring Security) ---
    // O Spring Security usa isso para saber como logar e quais permissões dar

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Define a "permissão". O "ROLE_" é uma convenção do Spring.
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.senha; // Retorna a senha
    }

    @Override
    public String getUsername() {
        return this.email; // Usaremos o email como "username" para login
    }

    // Métodos para controle de conta (deixamos como true por padrão)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // --- Equals e HashCode (Boa prática para o JPA) ---
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