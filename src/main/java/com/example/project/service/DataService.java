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

    public void saveData(Long userId, Integer cost) {
        DataEntity dataEntity = new DataEntity();
        dataEntity.setDateTime(LocalDateTime.now());
        dataEntity.setUserId(userId);
        dataEntity.setCost(cost);
        dataEntity.setKassa(null);

        dataRepository.save(dataEntity);
    }

    public int getTotalSum() {
        return dataRepository.findAll().stream()
                .mapToInt(DataEntity::getCost)
                .sum();
    }

    public int getUserSum(Long userId) {
        return dataRepository.findAllByUserId(userId).stream()
                .mapToInt(DataEntity::getCost)
                .sum();
    }
}
