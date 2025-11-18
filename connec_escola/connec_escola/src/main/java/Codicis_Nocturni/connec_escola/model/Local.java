// Pacote meio estranho esse nome, mas ok
package Codicis_Nocturni.connec_escola.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

/**
 * Entidade pra guardar o Local.
 * tipo, "Auditório", "Sala 101", "Pátio"
 * Coisa simples.
 */
@Entity
@Table(name = "locais") // Tabela no banco vai chamar 'locais'
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Deixa o Postgres cuidar do ID
    private Long id;

    @NotBlank(message = "O nome do local é obrigatório") // Nao pode ser vazio
    @Column(nullable = false, unique = true) // Nao pode repetir nome
    private String nome;

    @Positive(message = "A capacidade deve ser um número positivo")
    private Integer capacidade; // Quantas pessoas cabem. Pode ser nulo se ninguem souber.


    // O JPA precisa de um construtor vazio pra funcionar.
    // Nao apagar!!
    public Local() {
    }

    // --- NOVO ---
    // O DataLoader precisa desse construtor pra criar
    // o "Pátio Principal" e o "Auditório" quando o app liga.
    public Local(String nome, Integer capacidade) {
        // System.out.println("DEBUG: Criando local: " + nome); // debug
        this.nome = nome;
        this.capacidade = capacidade;
    }


    // --- Getters e Setters basicos ---
    // (Gerado pela IDE, nem mexi)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        // System.out.println("Novo nome do local: " + nome); // debug
        this.nome = nome;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }


    // --- Equals e HashCode ---
    // Importante pro JPA e pra Coleções.
    // Fiz o rapido, só pelo ID.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Local local = (Local) o;
        return Objects.equals(id, local.id); // Se o ID for igual, é o mesmo local
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash só pelo ID
    }
}