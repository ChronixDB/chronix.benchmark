package Client;

import de.qaware.chronix.client.benchmark.queryhandler.util.CsvImporter;
import de.qaware.chronix.database.TimeSeries;

import java.io.File;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mcqueen666 on 31.08.16.
 */
public class ReadCsvFilesTest {

    public static List<TimeSeries> readCsv(File directory){

        System.out.println("\n###### Client.ReadCsvFilesTest ######");

        // csv importer test
        CsvImporter csvImporter = new CsvImporter();
        List<TimeSeries> checktimeSeriesList = new LinkedList<>();
        if(directory == null){
            directory = new File("/Users/mcqueen666/Desktop/p1/air-lasttest");
        }

        if(directory.exists() && directory.isDirectory()) {
            File[] csvFileList = directory.listFiles();
            // read the csv files and generate times series
            checktimeSeriesList = csvImporter.getTimeSeriesFromFiles(csvFileList);
            if(!checktimeSeriesList.isEmpty()) {
                if(checktimeSeriesList.size() >= 3){
                    for(int i = 0; i <= 3; i++){
                        System.out.println("TimesSeries measurmentName: " + checktimeSeriesList.get(i).getMeasurementName());
                        System.out.println("TimeSeries: metricnName: " + checktimeSeriesList.get(i).getMetricName());
                        System.out.println("TimeSeries: start: " + Instant.ofEpochMilli(checktimeSeriesList.get(i).getStart()));
                        System.out.println("TimeSeries: end: " + Instant.ofEpochMilli(checktimeSeriesList.get(i).getEnd()));
                        checktimeSeriesList.get(i).getTagKey_tagValue().forEach((key, value) -> System.out.println("TimeSeries: tagkey: " + key + " with tagValue: " + value));
                        System.out.println("TimeSeries: points size: " + checktimeSeriesList.get(i).getPoints().size());
                        if (checktimeSeriesList.get(i).getPoints().size() >= 20) {
                            for (int j = 0; j <= 20; j++) {
                                System.out.println("TimeSeries: Date: " + Instant.ofEpochMilli(checktimeSeriesList.get(i).getPoints().get(j).getTimeStamp())
                                        + " Value: " + checktimeSeriesList.get(i).getPoints().get(j).getValue());
                            }
                        }
                    }
                } else {
                    for (TimeSeries ts : checktimeSeriesList) {
                        System.out.println("TimesSeries measurmentName: " + ts.getMeasurementName());
                        System.out.println("TimeSeries: metricnName: " + ts.getMetricName());
                        System.out.println("TimeSeries: start: " + Instant.ofEpochMilli(ts.getStart()));
                        System.out.println("TimeSeries: end: " + Instant.ofEpochMilli(ts.getEnd()));
                        ts.getTagKey_tagValue().forEach((key, value) -> System.out.println("TimeSeries: tagkey: " + key + " with tagValue: " + value));
                        System.out.println("TimeSeries: points size: " + ts.getPoints().size());
                        if (ts.getPoints().size() >= 20) {
                            for (int i = 0; i <= 20; i++) {
                                System.out.println("TimeSeries: Date: " + Instant.ofEpochMilli(ts.getPoints().get(i).getTimeStamp())
                                        + " Value: " + ts.getPoints().get(i).getValue());
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Read TimeSeries: " + checktimeSeriesList.size());
        return checktimeSeriesList;

    }

}