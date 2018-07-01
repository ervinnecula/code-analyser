package uaic.fii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uaic.fii.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);
}
