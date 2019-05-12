package uaic.fii.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.model.Properties;
import uaic.fii.model.User;
import uaic.fii.repository.PropertiesRepository;
import uaic.fii.repository.UserRepository;

@Service
public class PropertiesService {

    private PropertiesRepository propertiesRepository;

    @Autowired
    private UserRepository userRepository;


    public PropertiesService(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    public Properties getPropertiesByUserId(String userName) {
        User user = userRepository.findByName(userName);
        Properties properties = propertiesRepository.findByUserId(user.getId());
        return properties;
    }
}
