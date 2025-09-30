package com.bigcompany.org;

import com.bigcompany.org.analysis.OrgAnalyzer;
import com.bigcompany.org.io.EmployeeCsvReader;
import com.bigcompany.org.model.Employee;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class OrgAnalyzerTest {
    private Path writeSampleCsv() throws IOException {
        String csv = String.join(System.lineSeparator(),
                "id,firstName,lastName,salary,managerId",
                "123,Joe,Doe,60000,",
                "124,Martin,Chekov,45000,123",
                "125,Bob,Ronstad,47000,123",
                "300,Alice,Hasacat,50000,124",
                "305,Brett,Hardleaf,34000,300");
        Path temp = Files.createTempFile("employees", ".csv");
        Files.writeString(temp, csv);
        return temp;
    }

    @org.junit.Test
    public void testParsingAndHierarchy() throws Exception {
        Path csv = writeSampleCsv();
        EmployeeCsvReader reader = new EmployeeCsvReader();
        List<Employee> list = reader.read(csv);
        assertEquals(5, list.size());
        Employee ceo = reader.buildHierarchy(list);
        assertEquals(123, ceo.getId());
        assertEquals(2, ceo.getDirectReports().size());
    }

    @Test
    public void testAnalyzerFindings() throws Exception {
        Path csv = writeSampleCsv();
        EmployeeCsvReader reader = new EmployeeCsvReader();
        Employee ceo = reader.buildHierarchy(reader.read(csv));
        OrgAnalyzer analyzer = new OrgAnalyzer();

        assertTrue(analyzer.findManagersEarningLessThanAllowed(ceo).isEmpty());
        assertTrue(analyzer.findManagersEarningMoreThanAllowed(ceo).isEmpty());

        var deep = analyzer.findEmployeesWithTooManyManagersBetweenThemAndCeo(ceo);
        assertEquals(0, deep.size());
    }
}

