package com.example.project.service;

import com.example.project.model.DataEntity;
import com.example.project.repository.DataRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DataService {
    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void saveData(String value, int rowNumber) {
        DataEntity entity = new DataEntity(rowNumber, value);
        dataRepository.save(entity);
    }

    public List<DataEntity> getAllData() {
        return dataRepository.findAll();
    }
}
