package uaic.fii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uaic.fii.model.Properties;

public interface PropertiesRepository extends JpaRepository<Properties, Integer> {
    Properties findByUserId(Integer id);
}
