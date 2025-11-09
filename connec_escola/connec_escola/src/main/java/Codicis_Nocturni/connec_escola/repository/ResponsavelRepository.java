package Codicis_Nocturni.connec_escola.repository;

import Codicis_Nocturni.connec_escola.model.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    // Por enquanto, não precisamos de nada aqui.
    // Os métodos save(), findById(), findAll(), delete() já vêm de graça.
}