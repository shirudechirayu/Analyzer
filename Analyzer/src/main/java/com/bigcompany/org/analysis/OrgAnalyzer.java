package com.bigcompany.org.analysis;

import com.bigcompany.org.model.Employee;

import java.util.*;

public class OrgAnalyzer {
    public static class SalaryDelta {
        public final Employee manager;
        public final long expectedMin;
        public final long expectedMax;
        public final long actual;

        public SalaryDelta(Employee manager, long expectedMin, long expectedMax, long actual) {
            this.manager = manager;
            this.expectedMin = expectedMin;
            this.expectedMax = expectedMax;
            this.actual = actual;
        }
    }

    public static class DeepReportingLine {
        public final Employee employee;
        public final int depthFromCeo; // number of managers between employee and CEO

        public DeepReportingLine(Employee employee, int depthFromCeo) {
            this.employee = employee;
            this.depthFromCeo = depthFromCeo;
        }
    }

    public List<SalaryDelta> findManagersEarningLessThanAllowed(Employee ceo) {
        List<SalaryDelta> result = new ArrayList<>();
        for (Employee manager : getAllManagers(ceo)) {
            if (manager.getDirectReports().isEmpty()) continue;
            long avg = averageSalary(manager.getDirectReports());
            long min = Math.round(avg * 1.2);
            long max = Math.round(avg * 1.5);
            if (manager.getSalary() < min) {
                result.add(new SalaryDelta(manager, min, max, manager.getSalary()));
            }
        }
        return result;
    }

    public List<SalaryDelta> findManagersEarningMoreThanAllowed(Employee ceo) {
        List<SalaryDelta> result = new ArrayList<>();
        for (Employee manager : getAllManagers(ceo)) {
            if (manager.getDirectReports().isEmpty()) continue;
            long avg = averageSalary(manager.getDirectReports());
            long min = Math.round(avg * 1.2);
            long max = Math.round(avg * 1.5);
            if (manager.getSalary() > max) {
                result.add(new SalaryDelta(manager, min, max, manager.getSalary()));
            }
        }
        return result;
    }

    public List<DeepReportingLine> findEmployeesWithTooManyManagersBetweenThemAndCeo(Employee ceo) {
        List<DeepReportingLine> result = new ArrayList<>();
        Map<Integer, Integer> depthById = new HashMap<>();
        Deque<Employee> queue = new ArrayDeque<>();
        queue.add(ceo);
        depthById.put(ceo.getId(), 0);
        while (!queue.isEmpty()) {
            Employee current = queue.removeFirst();
            int currentDepth = depthById.get(current.getId());
            for (Employee child : current.getDirectReports()) {
                int childDepth = currentDepth + 1;
                depthById.put(child.getId(), childDepth);
                queue.addLast(child);
                if (childDepth > 4) { // more than 4 managers between employee and CEO
                    result.add(new DeepReportingLine(child, childDepth));
                }
            }
        }
        return result;
    }

    private List<Employee> getAllManagers(Employee ceo) {
        List<Employee> managers = new ArrayList<>();
        Deque<Employee> stack = new ArrayDeque<>();
        stack.push(ceo);
        while (!stack.isEmpty()) {
            Employee e = stack.pop();
            if (!e.getDirectReports().isEmpty()) {
                managers.add(e);
            }
            for (Employee d : e.getDirectReports()) {
                stack.push(d);
            }
        }
        return managers;
    }

    private long averageSalary(List<Employee> employees) {
        long sum = 0;
        for (Employee e : employees) {
            sum += e.getSalary();
        }
        return employees.isEmpty() ? 0 : Math.round((double) sum / employees.size());
    }
}

