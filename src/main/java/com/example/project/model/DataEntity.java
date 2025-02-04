package com.example.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime = LocalDateTime.now();

    private Long userId; // Зарезервировано, остается пустым

    private Integer cost;

    private String kassa; // Зарезервировано, остается пустым
}
