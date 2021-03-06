package de.qaware.chronix.client;


/**
 * DUMMY TEST CLASS
 * Created by mcqueen666 on 14.06.16.
 */
public class HelloClient {



    public static void main(String[] args){
       /* *//*if(args.length < 3){
            System.out.println("Usage: java -jar client-<version>-all.jar [absoulutPath] [http://<serverAddress>] [portNumber]");
            return;
        }

*//*


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("Client date: " + formatter.format(new Date(Instant.now().toEpochMilli())));
        Configurator configurator = Configurator.getInstance();
        QueryHandler queryHandler = QueryHandler.getInstance();
        String server = "localhost";

        // Server is up test

        if(configurator.isServerUp(server)){
            System.out.println("Server is up");
        } else {
            System.out.println("Server not responding");
        }


        // csv importer test
        CsvImporter csvImporter = new CsvImporter();
        List<TimeSeries> checktimeSeriesList = new LinkedList<>();
        File directory = new File("/Users/mcqueen666/Desktop/p1/air-lasttest");
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


        //json to file test (server record test)


            LinkedList<DockerBuildOptions> buildOptionses = new LinkedList<>();
            buildOptionses.add(new DockerBuildOptions("chronix", "-t"));
            //buildOptionses.add(new DockerBuildOptions("kairosdb", "-t"));

            LinkedList<DockerRunOptions> runOptionses = new LinkedList<>();
            runOptionses.add(new DockerRunOptions("chronix", 8983, 8983, ""));
            //runOptionses.add(new DockerRunOptions("kairos", 2003, 2003, "-v"));

            ServerConfigRecord serverConfigRecord = new ServerConfigRecord(server);
            serverConfigRecord.setTsdbBuildRecords(buildOptionses);
            serverConfigRecord.setTsdbRunRecords(runOptionses);
            LinkedList<String> tsdata = new LinkedList<>();
            tsdata.add("p1");
            tsdata.add("p2");
            //serverConfigRecord.setTimeSeriesDataFolders(tsdata);

*//*
            ServerConfigRecord serverConfigRecord2 = new ServerConfigRecord("www.fau.cs.de");
            serverConfigRecord2.setTsdbBuildRecords(buildOptionses);
            serverConfigRecord2.setTsdbRunRecords(runOptionses);
            LinkedList<String> tsdata2 = new LinkedList<>();
            tsdata2.add("p3");
            tsdata2.add("p4");
            serverConfigRecord2.setTimeSeriesDataFolders(tsdata2);
*//*
            LinkedList<ServerConfigRecord> records = new LinkedList<>();
            records.add(serverConfigRecord);
            //records.add(serverConfigRecord2);


            ServerConfigAccessor serverConfigAccessor = ServerConfigAccessor.getInstance();
            serverConfigAccessor.setServerConfigRecords(records);



       // jar interface test

            TSDBInterfaceHandler interfaceHandler = TSDBInterfaceHandler.getInstance();
            File jarFile = new File("/Users/mcqueen666/Documents/BA_workspace/chronix.benchmark/ChronixClient/build/libs/ChronixClient-1.0-SNAPSHOT-all.jar");
            if (jarFile.exists()) {
                String implName = "chronix";
                interfaceHandler.copyTSDBInterface(jarFile, implName);
                BenchmarkDataSource chronix = interfaceHandler.getTSDBInstance(implName);
                if(chronix != null){
                        System.out.println("Client: interface " + chronix.getClass().getName() +" is working");
                        System.out.println("Client: interface " + chronix.getClass().getName() +" storage directory is: " + chronix.getStorageDirectoryPath());

                    LinkedList<ServerConfigRecord> readRecord = serverConfigAccessor.getServerConfigRecords();
                    for(ServerConfigRecord configRecord : readRecord){
                        LinkedList<String> externalImpls = configRecord.getExternalTimeSeriesDataBaseImplementations();
                        externalImpls.add(implName);
                        configRecord.setExternalTimeSeriesDataBaseImplementations(externalImpls);
                    }
                    //write back to hd
                    serverConfigAccessor.setServerConfigRecords(readRecord);
                    // upload config to server
                    if(configurator.uploadServerConfig(server)){
                        System.out.println("Config upload to server successful");
                        // upload jarFile
                        String[] answer = configurator.uploadJarFile(server,jarFile,implName);
                        System.out.println("Server: " + answer[0]);
                        answer = configurator.checkInterfaceStatus(server,implName);
                        System.out.println("Server: " + answer[0]);


                    } else {
                        System.out.println("Error config upload");
                    }
                }

            } else {
                System.out.println("File not found!");
            }




        // show server record

        LinkedList<ServerConfigRecord> readRecord = serverConfigAccessor.getServerConfigRecords();
 *//*
        for(ServerConfigRecord r : readRecord){


            System.out.println("Serveraddress: " + r.getServerAddress());
            LinkedList<DockerRunOptions> newRunList = r.getTsdbRunRecords();
            LinkedList<DockerBuildOptions> newBuildList = r.getTsdbBuildRecords();
            LinkedList<String> tsfolders = r.getTimeSeriesDataFolders();
            LinkedList<String> externalImpls = r.getExternalTimeSeriesDataBaseImplementations();

            for (DockerRunOptions op : newRunList) {
                System.out.println(op.getValidRunCommand());
            }

            for (DockerBuildOptions op : newBuildList) {
                System.out.println(op.getValidBuildCommand());
            }

            for (String s : tsfolders){
                System.out.println("TS DataModels Folder: " + s);
            }

            for (String s : externalImpls){
                System.out.println("Client: " + s + " implemented");
                BenchmarkDataSource impl = interfaceHandler.getTSDBInstance(s);
                if(impl != null){
                    System.out.println("Client: " + s + " interface "+ impl.getClass().getName() +" is working");
                    //System.out.println("Query is: " + impl.getQueryForFunction(null,null,null,null,null,null,0.f,BenchmarkDataSource.QueryFunction.COUNT));
                } else {
                    System.out.println(s + " interface not available");
                }
            }
        }
 *//*

        // Test file upload

        // Uploader uploader = Uploader.getInstance();
        //List<String> responses = uploader.uploadDockerFiles("", System.getProperty("user.home") + "/Documents/BA_workspace/docker/chronix", server, "9003");
        //List<Response> responses = uploader.uploadDockerFiles("",args[0],args[1],args[2]);
        String[] uploadAnswers = configurator.uploadFiles(server,System.getProperty("user.home") + "/Documents/BA_workspace/docker/chronix");
        for(String answer : uploadAnswers){
            System.out.println(answer);
        }

        // start test
        DockerRunOptions chronix = new DockerRunOptions("chronix",8983,8983,"");
        String[] start = configurator.startDockerContainer(server,chronix);
        for(String s : start){
            System.out.println("Start: " + s + " started");
        }

        //running test
        String[] answers = null;
        boolean isDockerContainerRunning = configurator.isDockerContainerRunning(server,"chronix");
        if(isDockerContainerRunning){
            String[] s = {"container is running"};
            answers = s;
        } else {
            String[] s = {"container is not running"};
            answers = s;
        }
        for(String answer : answers){
            System.out.println(answer);
        }
        if(!isDockerContainerRunning){
            return;
        }

*//*
        // import test
        for(ServerConfigRecord r : readRecord){
            LinkedList<String> externalImpls = r.getExternalTimeSeriesDataBaseImplementations();
            for(String s : externalImpls){
                BenchmarkDataSource tsdb = interfaceHandler.getTSDBInstance(s);
                String ip = r.getServerAddress();
                String port = serverConfigAccessor.getHostPortForTSDB(ip, s);
                String queryID = "import_air-lasttest:1";

                ImportRecord importRecord = new ImportRecord(queryID,ip,port,s,checktimeSeriesList.subList(0,1));
                String[] results = queryHandler.doImportOnServer(ip,importRecord);
                Long latency = queryHandler.getLatencyForQueryID(queryID);
                if(latency != null){
                    System.out.println("QueryID: " + queryID);
                    System.out.println("Latency: " + latency + " milliseconds");
                    for(String result : results) {
                        System.out.println("Result: " + result);
                    }
                } else {
                    System.out.println("Error: " + results[0]);
                }
            }
        }
*//*



        // query test
        for(ServerConfigRecord r : readRecord){
            LinkedList<String> externalImpls = r.getExternalTimeSeriesDataBaseImplementations();
            for(String externalImpl : externalImpls){
                BenchmarkDataSource tsdb = interfaceHandler.getTSDBInstance(externalImpl);
                String ip = r.getServerAddress();
                String port = serverConfigAccessor.getHostPortForTSDB(ip, externalImpl);
                String queryID = "air-lasttest:sum:1";

                // make benchmarkquery list with entries
                List<BenchmarkQuery> querys = new LinkedList<>();
                // entry
                TimeSeriesMetaData timeSeriesMetaData = new TimeSeriesMetaData(checktimeSeriesList.get(0));
                querys.add(new BenchmarkQuery(timeSeriesMetaData, null, BenchmarkDataSource.QueryFunction.COUNT));

                // make queryRecord with the benchmarkquery list
                QueryRecord queryRecord = new QueryRecord(queryID,ip,port,externalImpl,querys);
                String[] results = queryHandler.doQueryOnServer(ip, queryRecord);
                Long latency = queryHandler.getLatencyForQueryID(Pair.of(queryID, queryRecord.getTsdbName()));
                if(latency != null){
                    System.out.println("QueryID: " + queryID);
                    System.out.println("Latency: " + latency + " milliseconds");
                    for(String result : results) {
                        System.out.println("Result: " + result);
                    }
                } else {
                    System.out.println("Error: " + results[0]);
                }


                *//*String[] measurements = queryHandler.getMeasurement(server);
                for(String m : measurements){
                    System.out.println("Measurement: " + m);
                }*//*

            }
        }



*//*
        System.out.println(System.getProperty("user.home"));
        System.out.println(sun.awt.OSInfo.getOSType());
        File dir = new File(System.getProperty("user.home") + "/Desktop/chronix");
        if(dir.isDirectory()){
            System.out.println("getPath() = " + dir.getPath());
        }
*//*



//*//*
        //test build container
        //String commandFileName = "chronix.build";
        //final Client client = ClientBuilder.newBuilder().build();
        //final WebTarget target = client.target("http://192.168.2.100:9003/configurator/test?name=chronix.jar");
        //final Response response = target.request().get();
        //System.out.println(response.readEntity(String.class));

       // final WebTarget target = client.target("http://192.168.2.100:9003/configurator/docker/build?containerName=chronix&commandFileName="+commandFileName);
        //final WebTarget target = client.target("http://192.168.2.100:9003/configurator/ping?nTimes=4");
        //final WebTarget target = client.target("http://192.168.2.100:9003/configurator/which");
        //final WebTarget target = client.target("http://192.168.2.100:9003/configurator/docker/stop?containerName=chronix");
        //final WebTarget target = client.target("http://localhost:9003/configurator/booleanTest?value=yes");
        //final WebTarget target = client.target("http://192.168.2.100:9003/configurator/docker/remove?imageName=chronix&removeFiles=yes");


        //final WebTarget target = client.target("http://192.168.2.100:9003/configurator/docker/start");
        //final Response response = target.request().post(Entity.json(chronix));

//*//*



/*//*//*


        //stop test
        //String[] answers = configurator.stopDockerContainer(server,"chronix");

*//*
        // build test
        DockerBuildOptions chronix = new DockerBuildOptions("chronix","-t");
        String[] answers = configurator.buildDockerContainer(server,chronix);

        //final WebTarget target = client.target("http://192.168.2.100:9003/configurator/docker/build");
        //final Response response = target.request().post(Entity.json(chronix));
*//*

        // upload test
        //String[] answers = configurator.uploadFiles("localhost",System.getProperty("user.home") + "/Documents/BA_workspace/docker/chronix");

        // remove test
        //String[] answers = configurator.removeDockerContainer("localhost","chronix",true);


   //     final Response response = target.request().get();

        //DockerRunOptions op = response.readEntity(DockerRunOptions.class);
        //String op = response.readEntity(String.class);
        //System.out.println(response.getStatus() + " : " + op);


//*//*
        //String[] answers = response.readEntity(String[].class);
        //System.out.println("Server status: " + response.getStatus());
        //System.out.println(response.readEntity(String.class));

/*//*//*



*//*
        // test start container
        String command = "docker run -d -p 8983:8983 chronix";
        final Client client = ClientBuilder.newBuilder().build();
        final WebTarget target = client.target("http://localhost:9003/configurator/docker/start?container=chronix&command="+command);
        final Response response = target.request().get();
        String[] answers = response.readEntity(String[].class);
        for(String answer : answers){
            System.out.println(answer);
        }
*//*


*//*
        // test exec
        List<String> result = new LinkedList<String>();
        String[] command = {"/bin/sh","-c","ping -c 4 google.com"};
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String curLine;
            while ((curLine = reader.readLine()) != null) {
                result.add(curLine);
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            result.add(e.getLocalizedMessage());
        }
        for(String line : result){
            System.out.println(line);
        }

*//*

*/

    }
}
