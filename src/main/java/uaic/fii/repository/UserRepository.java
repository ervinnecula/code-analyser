package uaic.fii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uaic.fii.model.UserDAO;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, Integer> {
    UserDAO findByName(String name);
}
