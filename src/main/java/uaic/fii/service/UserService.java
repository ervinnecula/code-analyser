package uaic.fii.service;

import org.springframework.stereotype.Service;
import uaic.fii.model.User;
import uaic.fii.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(int id) {
        return userRepository.findOne(id);
    }

}
