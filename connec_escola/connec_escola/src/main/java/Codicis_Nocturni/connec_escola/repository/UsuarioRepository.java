package Codicis_Nocturni.connec_escola.repository;

import Codicis_Nocturni.connec_escola.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface para acessar os dados do Usuario no banco.
 * O Spring Data JPA vai criar a implementação disso sozinho.
 */
@Repository // Boa prática para identificar o bean
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // JpaRepository<Usuario, Long>
    // 1º Parâmetro: A entidade que ele gerencia (Usuario)
    // 2º Parâmetro: O tipo da Chave Primária (Id) do Usuario (Long)

    /**
     * Busca um usuário pelo seu endereço de email.
     * O Spring cria a query automaticamente só pelo nome do método.
     * Usaremos isso no login.
     *
     * @param email Email a ser buscado.
     * @return um Optional contendo o Usuario (se existir).
     */
    Optional<Usuario> findByEmail(String email);

}