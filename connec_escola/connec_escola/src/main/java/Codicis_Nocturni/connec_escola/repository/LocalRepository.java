package Codicis_Nocturni.connec_escola.repository;

import Codicis_Nocturni.connec_escola.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
    // Padrão por enquanto.
}