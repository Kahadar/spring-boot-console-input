package com.example.project.repository;

import com.example.project.model.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<DataEntity, Long> {
    List<DataEntity> findAllByUserId(Long userId);  // Добавляем метод
}
