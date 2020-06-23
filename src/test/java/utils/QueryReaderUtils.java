package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryReaderUtils {
    public static List<List<String>> queryReader(String csvFileName) {
        List<List<String>> records = new ArrayList<List<String>>();
        try {
            final URL resource = QueryReaderUtils.class.getResource("/" + csvFileName);
            final URI uri = resource.toURI();
            try (CSVReader csvReader = new CSVReader(new FileReader(new File(uri)))) {
                String[] values = null;
                while ((values = csvReader.readNext()) != null) {
                    records.add(Arrays.asList(values));
                }
                System.out.println(":::Query reading complete:::");
            }
        } catch (IOException | CsvValidationException | URISyntaxException e) {
            System.out.println("File Read Error" + e);
        }
        return records;
    }
}
