package com.bigcompany.org.io;

import com.bigcompany.org.model.Employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeCsvReader {
    public List<Employee> read(Path csvFile) throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) return employees;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    throw new IOException("Invalid CSV line: " + line);
                }
                int id = Integer.parseInt(parts[0].trim());
                String firstName = parts[1].trim();
                String lastName = parts[2].trim();
                long salary = Long.parseLong(parts[3].trim());
                String managerToken = parts[4].trim();
                Integer managerId = managerToken.isEmpty() ? null : Integer.valueOf(managerToken);
                employees.add(new Employee(id, firstName, lastName, salary, managerId));
            }
        }
        return employees;
    }

    public Employee buildHierarchy(List<Employee> employees) {
        Map<Integer, Employee> idToEmployee = new HashMap<>();
        for (Employee e : employees) {
            idToEmployee.put(e.getId(), e);
        }
        Employee ceo = null;
        for (Employee e : employees) {
            Integer managerId = e.getManagerId();
            if (managerId == null) {
                ceo = e;
                continue;
            }
            Employee manager = idToEmployee.get(managerId);
            if (manager == null) {
                throw new IllegalStateException("Manager with id " + managerId + " not found for employee " + e.getId());
            }
            manager.addDirectReport(e);
        }
        if (ceo == null) {
            throw new IllegalStateException("No CEO (employee without managerId) found");
        }
        return ceo;
    }
}

