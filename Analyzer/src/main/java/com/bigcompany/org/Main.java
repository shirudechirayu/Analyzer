package com.bigcompany.org;

import com.bigcompany.org.analysis.OrgAnalyzer;
import com.bigcompany.org.io.EmployeeCsvReader;
import com.bigcompany.org.model.Employee;

import java.nio.file.Path;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java -jar org-analyzer.jar <employees.csv>");
            System.exit(1);
        }
        Path csv = Path.of(args[0]);
        EmployeeCsvReader reader = new EmployeeCsvReader();
        Employee ceo = reader.buildHierarchy(reader.read(csv));

        OrgAnalyzer analyzer = new OrgAnalyzer();

        System.out.println("Managers earning LESS than allowed (actual, expectedMin..expectedMax):");
        analyzer.findManagersEarningLessThanAllowed(ceo)
                .stream()
                .sorted(Comparator.comparing(sd -> sd.manager.getId()))
                .forEach(sd -> System.out.printf("- %s (%d): %d, %d..%d%n",
                        sd.manager.getFullName(), sd.manager.getId(), sd.actual, sd.expectedMin, sd.expectedMax));

        System.out.println();
        System.out.println("Managers earning MORE than allowed (actual, expectedMin..expectedMax):");
        analyzer.findManagersEarningMoreThanAllowed(ceo)
                .stream()
                .sorted(Comparator.comparing(sd -> sd.manager.getId()))
                .forEach(sd -> System.out.printf("- %s (%d): %d, %d..%d%n",
                        sd.manager.getFullName(), sd.manager.getId(), sd.actual, sd.expectedMin, sd.expectedMax));

        System.out.println();
        System.out.println("Employees with too many managers between them and CEO (depth):");
        analyzer.findEmployeesWithTooManyManagersBetweenThemAndCeo(ceo)
                .stream()
                .sorted(Comparator.comparing(dr -> dr.employee.getId()))
                .forEach(dr -> System.out.printf("- %s (%d): %d%n",
                        dr.employee.getFullName(), dr.employee.getId(), dr.depthFromCeo));
    }
}

