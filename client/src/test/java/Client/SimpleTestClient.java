package Client;

import Docker.*;
import Server.GenerateServerConfigRecord;
import Server.InterfaceAndConfigUploadTest;
import de.qaware.chronix.client.benchmark.benchmarkrunner.BenchmarkRunner;
import de.qaware.chronix.client.benchmark.benchmarkrunner.util.TimeSeriesCounter;
import de.qaware.chronix.client.benchmark.configurator.Configurator;
import de.qaware.chronix.client.benchmark.util.JsonTimeSeriesHandler;
import de.qaware.chronix.database.TimeSeriesMetaData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcqueen666 on 31.08.16.
 */
public class SimpleTestClient {

    public static void main(String[] args){

        Configurator configurator = Configurator.getInstance();
        JsonTimeSeriesHandler jsonTimeSeriesHandler = JsonTimeSeriesHandler.getInstance();

        long startMillis;
        long endMillis;
        String server = "192.168.2.115";

        try {
            if (configurator.isServerUp(server)) {
                System.out.println("Server is up");
            }
        } catch (Exception e){
            System.out.println("Server not responding. Error: " + e.getLocalizedMessage());
            return;
        }


        GenerateServerConfigRecord.main(new String[]{server});
        UploadDockerFiles.main(new String[]{server});
        InterfaceAndConfigUploadTest.main(new String[]{server});
        //BuildDockerContainer.main(new String[]{server,"chronix","influxdb","kairosdb", "opentsdb", "graphite"});
        StartDockerContainer.main(new String[]{server,"chronix","influxdb","kairosdb", "opentsdb", "graphite"});
        //RunningTestDockerContainer.main(new String[]{server,"chronix","influxdb","kairosdb", "opentsdb", "graphite"});

        //StopDockerContainer.main(new String[]{server,"chronix","influxdb","kairosdb", "opentsdb", "graphite"});



        // import test

        //ImportTest.importTimeSeriesHeavy(server);
        List<File> directories = new ArrayList<>();
        directories.add(new File("/Users/mcqueen666/chronixBenchmark/timeseries_records/air-lasttest"));
        //directories.add(new File("/Users/mcqueen666/chronixBenchmark/timeseries_records/shd"));
        //directories.add(new File("/Users/mcqueen666/chronixBenchmark/timeseries_records/promt"));
        // as if was not imported previously
        for(File directory : directories){
            jsonTimeSeriesHandler.deleteTimeSeriesMetaDataJsonFile(directory.getName());
        }
        startMillis = System.currentTimeMillis();
        List<TimeSeriesMetaData> importedTimeSeriesMetaData = ImportTest.importTimeSeriesFromDirectory(server, directories);
        endMillis = System.currentTimeMillis();
        System.out.println("\nImport test total time: " + (endMillis - startMillis) + "ms");



        // query test
        TimeSeriesCounter timeSeriesCounter = TimeSeriesCounter.getInstance();
        List<TimeSeriesMetaData> randomTimeSeries = timeSeriesCounter.getRandomTimeSeriesMetaData(1000);
        startMillis = System.currentTimeMillis();
        QueryTest.queryCount(randomTimeSeries, server);
        endMillis = System.currentTimeMillis();
        System.out.println("\nQuery test total time: " + (endMillis - startMillis) + "ms");


        //get benchmark query record test
        BenchmarkRunner benchmarkRunner = BenchmarkRunner.getInstance();
        System.out.println("Downloading benchmark records from server successful: " +  benchmarkRunner.getBenchmarkRecordsFromServer(server));

    }
}
