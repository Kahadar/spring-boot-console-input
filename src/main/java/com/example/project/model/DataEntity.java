package com.example.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "data_table")
public class DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rowNumber;

    @Column(nullable = false)
    private String value;

    public DataEntity() {}

    public DataEntity(Integer rowNumber, String value) {
        this.rowNumber = rowNumber;
        this.value = value;
    }

    public Long getId() { return id; }
    public Integer getRowNumber() { return rowNumber; }
    public void setRowNumber(Integer rowNumber) { this.rowNumber = rowNumber; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
