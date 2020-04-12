package uaic.fii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uaic.fii.model.PropertiesDAO;

@Repository
public interface PropertiesRepository extends JpaRepository<PropertiesDAO, Integer> {
    PropertiesDAO findByUserId(Integer id);
}
