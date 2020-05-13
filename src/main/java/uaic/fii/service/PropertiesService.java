package uaic.fii.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.model.PropertiesDAO;
import uaic.fii.model.UserDAO;
import uaic.fii.repository.PropertiesRepository;
import uaic.fii.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class PropertiesService {

    private final PropertiesRepository propertiesRepository;

    private final UserRepository userRepository;

    private Map<String, Integer> propertiesMap;

    @Autowired
    public PropertiesService(PropertiesRepository propertiesRepository, UserRepository userRepository) {
        this.propertiesRepository = propertiesRepository;
        this.userRepository = userRepository;
    }

    public void loadPropertiesByUsername(String userName) {
        UserDAO user = userRepository.findByName(userName);
        PropertiesDAO propertiesDAO = propertiesRepository.findByUserId(user.getId());

        propertiesMap = new HashMap<>();
        propertiesMap.put("largeFileSize", propertiesDAO.getLargeFileSize());
        propertiesMap.put("hugeFileSize", propertiesDAO.getHugeFileSize());
        propertiesMap.put("manyCommitters", propertiesDAO.getManyCommittersSize());
        propertiesMap.put("fewCommitters", propertiesDAO.getFewCommittersSize());
        propertiesMap.put("mediumChangeSize", propertiesDAO.getMediumChangeSize());
        propertiesMap.put("majorChangeSize", propertiesDAO.getMajorChangeSize());
        propertiesMap.put("periodOfTime", propertiesDAO.getPeriodOfTime());
    }

    public Map<String, Integer> getPropertiesMap() {
        return propertiesMap;
    }

    public void updateProperties(String username, Integer largeFileSize, Integer hugeFileSize, Integer manyCommitters,
                                  Integer fewCommitters,  Integer mediumChangeSize, Integer majorChangeSize, Integer periodOfTime) {

        UserDAO user = userRepository.findByName(username);
        PropertiesDAO properties = propertiesRepository.findByUserId(user.getId());
        properties.setFewCommittersSize(fewCommitters);
        properties.setManyCommittersSize(manyCommitters);
        properties.setLargeFileSize(largeFileSize);
        properties.setHugeFileSize(hugeFileSize);
        properties.setMediumChangeSize(mediumChangeSize);
        properties.setMajorChangeSize(majorChangeSize);
        properties.setPeriodOfTime(periodOfTime);
        propertiesRepository.save(properties);
    }
}
