package Codicis_Nocturni.connec_escola.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa um responsável (pai, mãe, etc.) por um ou mais alunos.
 */
@Entity // <-- ESSA É A ANOTAÇÃO QUE FALTAVA
@Table(name = "responsaveis") // E essa também é importante
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do responsável é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Email(message = "Email de contato inválido")
    private String emailContato; // Email para contato (não para login)

    private String telefoneContato;

    // Campo para guardar o parentesco, ex: "Mãe", "Pai", "Avó"
    private String parentesco;

    /**
     * Relacionamento ManyToMany (Lado "Inverso").
     * "mappedBy = 'responsaveis'" diz ao JPA:
     * "A configuração desta relação já foi feita lá na classe Usuario,
     * no campo chamado 'responsaveis'. Apenas use aquela configuração."
     */
    @ManyToMany(mappedBy = "responsaveis")
    private Set<Usuario> alunos = new HashSet<>();

    // Construtor padrão (obrigatório pelo JPA)
    public Responsavel() {
    }

    // --- Getters e Setters ---
    // (Gere todos automaticamente na sua IDE: Alt+Insert -> Getters and Setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmailContato() { return emailContato; }
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; }
    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }
    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }
    public Set<Usuario> getAlunos() { return alunos; }
    public void setAlunos(Set<Usuario> alunos) { this.alunos = alunos; }

    // --- Equals e HashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Responsavel that = (Responsavel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}