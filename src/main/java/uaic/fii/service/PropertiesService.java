package uaic.fii.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.model.PropertiesDAO;
import uaic.fii.model.UserDAO;
import uaic.fii.repository.PropertiesRepository;
import uaic.fii.repository.UserRepository;

@Service
public class PropertiesService {

    private PropertiesRepository propertiesRepository;

    private final UserRepository userRepository;

    @Autowired
    public PropertiesService(PropertiesRepository propertiesRepository, UserRepository userRepository) {
        this.propertiesRepository = propertiesRepository;
        this.userRepository = userRepository;
    }

    public PropertiesDAO getPropertiesByUserId(String userName) {
        UserDAO user = userRepository.findByName(userName);
        return propertiesRepository.findByUserId(user.getId());
    }
}
