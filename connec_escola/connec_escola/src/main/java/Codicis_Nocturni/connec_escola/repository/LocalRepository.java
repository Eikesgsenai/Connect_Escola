package Codicis_Nocturni.connec_escola.repository;

import Codicis_Nocturni.connec_escola.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Precisa importar isso

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

    // O DataLoader precisa disso aqui
    Optional<Local> findByNome(String nome);
}