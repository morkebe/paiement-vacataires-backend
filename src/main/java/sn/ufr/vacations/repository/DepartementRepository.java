package sn.ufr.vacations.repository;

import org.springframework.data.jpa.repository.Query;
import sn.ufr.vacations.model.entity.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
    Optional<Departement> findByCode(String code);
    Boolean existsByCode(String code);
    @Query("""
   SELECT DISTINCT d
   FROM Departement d
   LEFT JOIN FETCH d.filieres
    """)
    List<Departement> findAllWithFilieres();

}