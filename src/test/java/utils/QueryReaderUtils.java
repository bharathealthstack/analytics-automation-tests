package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryReaderUtils {
    public static List<List<String>> queryReader(String csvFileName) {
        List<List<String>> records = new ArrayList<List<String>>();
        try {
            try (CSVReader csvReader = new CSVReader(new FileReader(csvFileName));) {
                String[] values = null;
                while ((values = csvReader.readNext()) != null) {
                    records.add(Arrays.asList(values));
                }
                System.out.println(":::Query reading complete:::");
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println("File Read Error" + e);
        }
        return records;
    }
}
