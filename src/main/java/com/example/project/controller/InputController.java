package com.example.project.controller;

import com.example.project.service.DataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class InputController implements CommandLineRunner {
    private final DataService dataService;

    public InputController(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите значения для столбца (пустая строка для выхода):");

        int rowNumber = 1;
        while (true) {
            System.out.print("Значение: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {  // Заменили isBlank() на trim().isEmpty()
                break;
            }
            dataService.saveData(input, rowNumber++);
        }

        System.out.println("Все данные сохранены.");
    }
}
