package com.example.project.service;

import com.example.project.model.DataEntity;
import com.example.project.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository dataRepository;

    public void saveData(Integer cost) {
        DataEntity dataEntity = new DataEntity();
        dataEntity.setDateTime(LocalDateTime.now());
        dataEntity.setCost(cost);
        dataEntity.setUserId(null);
        dataEntity.setKassa(null);

        dataRepository.save(dataEntity);
    }
}
