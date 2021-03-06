package de.qaware.chronix.server.benchmark.configurator;


import de.qaware.chronix.database.BenchmarkDataSource;
import de.qaware.chronix.server.util.ChronixBoolean;
import de.qaware.chronix.server.util.DockerCommandLineUtil;
import de.qaware.chronix.server.util.ServerSystemUtil;
import de.qaware.chronix.common.ServerConfig.TSDBInterfaceHandler;
import de.qaware.chronix.common.dockerUtil.DockerBuildOptions;
import de.qaware.chronix.common.dockerUtil.DockerRunOptions;
import de.qaware.chronix.common.ServerConfig.ServerConfigAccessor;
import de.qaware.chronix.common.ServerConfig.ServerConfigRecord;
import org.apache.commons.compress.utils.IOUtils;
//import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
//import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mcqueen666 on 15.06.16.
 */
@Path("/configurator")
@Produces(MediaType.APPLICATION_JSON)
public class BenchmarkConfiguratorResource {

    private ServerConfigAccessor serverConfigAccessor = ServerConfigAccessor.getInstance();
    private final Logger logger = LoggerFactory.getLogger(BenchmarkConfiguratorResource.class);


    // JUST FOR TESTING
    @GET
    @Path("test")
    public String test(@QueryParam("name") String name) {
        return "Sent info: " + name;
    }


    //Test json
    @GET
    @Path("getjson")
    @Produces(MediaType.APPLICATION_JSON)
    public DockerRunOptions getjson(){
        DockerRunOptions op = new DockerRunOptions("chronix",8983,8983,"");
        return op;
    }

    //TEST
    @GET
    @Path("booleanTest")
    public Response test(@QueryParam("value") ChronixBoolean chronixBoolean) {
        String[] result = {"Result is " + chronixBoolean.getValue()};

        return Response.ok().entity(result).build();
    }

    //Obsolete
    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok().build();
    }

    @GET
    @Path("which")
    public Response which(){
        //String[] command = {"which docker"};
        //String[] lcom = ServerSystemUtil.getOsSpecificCommand(command);
        String[] result = {DockerCommandLineUtil.getDockerInstallPath()};

        return Response.ok().entity(result).build();
    }



    @POST
    @Path("upload/config")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadServerConfig(LinkedList<ServerConfigRecord> serverConfigRecords) {
        for(ServerConfigRecord serverConfigRecord : serverConfigRecords){
            serverConfigRecord.getTsdbDockerFilesDirectoryMap().clear();
        }
        serverConfigAccessor.setServerConfigRecords(serverConfigRecords);

        return Response.ok().build();
    }


    @GET
    @Path("docker/running")
    public Response isRunning(@QueryParam("containerName") String containerName){
        List<String> result = new LinkedList<String>();
        if(DockerCommandLineUtil.isDockerInstalled()){
            Boolean containerRunning = DockerCommandLineUtil.isDockerContainerRunning(containerName);
            if(containerRunning){
                result.add("true");
            } else {
                result.add("false");
            }

        } else {
            result.add("Docker not installed or running.");
        }
        return Response.ok().entity(result.toArray()).build();
    }


    @GET
    @Path("interface/running")
    public Response isInterfaceRunning(@QueryParam("tsdbName") String tsdbName){
        TSDBInterfaceHandler interfaceHandler = TSDBInterfaceHandler.getInstance();
        BenchmarkDataSource impl = interfaceHandler.getTSDBInstance(tsdbName);
        if(impl != null) {
            return Response.ok().entity(tsdbName + " interface is up. storage directory is: " + impl.getStorageDirectoryPath()).build();
        }

        logger.info(tsdbName + " interface not responding");
        return Response.serverError().entity(tsdbName + " interface not responding").build();

    }

    @POST
    @Path("upload/jar")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadJarFile(@QueryParam("tsdbName") String tsdbName,
                                      @FormDataParam("file")InputStream fileInputStream,
                                      @FormDataParam("file")FormDataContentDisposition fileMetaData){
        TSDBInterfaceHandler interfaceHandler = TSDBInterfaceHandler.getInstance();
        if(interfaceHandler.copyTSDBInterface(fileInputStream, tsdbName)){
            BenchmarkDataSource<Object> tsdb = interfaceHandler.getTSDBInstance(tsdbName);
            if(tsdb != null){
                //create external measurement directory if impl needs one. (done by impl)
                tsdb.ableToMeasureExternalDirectory();
            }
            return Response.ok().entity("copy successful").build();
        }
        logger.info(tsdbName + ": copy error");
        return Response.serverError().entity(tsdbName + ": copy error").build();
    }


    @POST
    @Path("docker/upload/{name}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadDockerFiles(@PathParam("name") String name,
                                      @FormDataParam("file")InputStream fileInputStream,
                                      @FormDataParam("file")FormDataContentDisposition fileMetaData) {

        String path = ServerSystemUtil.getBenchmarkDockerDirectory();
        if(path == null){
            return Response.serverError().entity("Server OS Unknown").build();
        }

        // construct directory path from name
        String[] paths = name.split("-");
        String reconstructedFilePath = "";
        for(String p : paths){
            reconstructedFilePath = reconstructedFilePath + p + File.separator;
        }


        String dirPath = path + reconstructedFilePath;
        new File(dirPath).mkdirs();
            String filename = fileMetaData.getFileName();
            String filePath = dirPath + filename;

            try {
                File newFile = new File(filePath);
                FileOutputStream outputStream = new FileOutputStream(newFile);

                IOUtils.copy(fileInputStream, outputStream);

                outputStream.close();

            } catch (IOException e) {
                logger.error("Could not write file <" + reconstructedFilePath + filename + ">");
                return Response.serverError().entity("Server could not write file <" + reconstructedFilePath + filename + ">" ).build();
            }


       return Response.ok("Upload file <" + reconstructedFilePath + filename + "> successfull!").build();

    }

    /**
     * Starts the docker specified in dockerRunOptoins.
     *
     * @param dockerRunOptions DockerRunOptions JSON
     * @return the response from the server and the cli output (e.g. statusCode + String[])
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("docker/start")
    public Response startDockerContainer(DockerRunOptions dockerRunOptions){
        if(DockerCommandLineUtil.isDockerInstalled()){
            String containerName = dockerRunOptions.getContainerName();
            File directory = new File(ServerSystemUtil.getBenchmarkDockerDirectory() + containerName);
            if(directory.exists()){
                if(!DockerCommandLineUtil.isDockerContainerRunning(containerName)) {

                    // first try restarting docker container
                    String restartCommand = dockerRunOptions.getValidRunCommand(true);
                    String[] prepareRestartCommand = {DockerCommandLineUtil.getDockerInstallPath() + restartCommand};
                    String[] specificRestartCommand = ServerSystemUtil.getOsSpecificCommand(prepareRestartCommand);
                    List<String> restartResult = ServerSystemUtil.executeCommand(specificRestartCommand);

                    if(!DockerCommandLineUtil.isDockerContainerRunning(containerName)) {
                        // container was not started before, so run with initial command.
                        String command = dockerRunOptions.getValidRunCommand(false);
                        //TODO further check command for safety reasons
                        if (command != null) {
                            String[] prepareCommand = {DockerCommandLineUtil.getDockerInstallPath() + command};
                            String[] specificCommand = ServerSystemUtil.getOsSpecificCommand(prepareCommand);
                            List<String> startResult = ServerSystemUtil.executeCommand(specificCommand);
                            if (DockerCommandLineUtil.isDockerContainerRunning(containerName)) {
                                // all went good
                                startResult.add("Docker container " + containerName + " is running");
                                return Response.ok().entity(startResult.toArray()).build();
                            }
                            startResult.add("Docker container " + containerName + " is not running");
                            logger.info("Docker container " + containerName + " is not running");
                            return Response.serverError().entity(startResult.toArray()).build();
                        }
                        String[] response = {"Wrong docker command."};
                        logger.info("Wrong docker command.");
                        return Response.serverError().entity(response).build();
                    }
                    // restart successful.
                    String[] response = {"Docker container " + containerName + " restarted."};
                    logger.info("Docker container " + containerName + " restarted.");
                    return Response.ok().entity(response).build();
                }
                String[] response = {"docker container " + containerName + " already running."};
                return Response.ok().entity(response).build();

            }
            String[] response = {"docker files missing", "directory = " + ServerSystemUtil.getBenchmarkDockerDirectory() + containerName};
            logger.error("docker files missing", "directory = " + ServerSystemUtil.getBenchmarkDockerDirectory() + containerName);
            return Response.serverError().entity(response).build();
        }
        String[] response = {"docker not installed or daemon not running"};
        logger.error("docker not installed or daemon not running");
        return Response.serverError().entity(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("docker/build")
    public Response buildDockerContainer(DockerBuildOptions dockerBuildOptions){
        logger.info("Building {} ... ", dockerBuildOptions.getContainerName());
        if(DockerCommandLineUtil.isDockerInstalled()){
            String containerName = dockerBuildOptions.getContainerName();
            File directory = new File(ServerSystemUtil.getBenchmarkDockerDirectory() + containerName);
            if(directory.exists()){

                    String command = dockerBuildOptions.getValidBuildCommand();

                    //TODO further check command for safety reasons
                    if(command != null){
                        String[] prepareCommand = {DockerCommandLineUtil.getDockerInstallPath()
                                                            + command.replace(".", directory.getPath())};
                        String[] specificCommand = ServerSystemUtil.getOsSpecificCommand(prepareCommand);
                        List<String> buildResult = ServerSystemUtil.executeCommand(specificCommand);
                            // all went good
                            return Response.ok().entity(buildResult.toArray()).build();
                    }
                    String[] response = {"Wrong docker command."};
                    logger.error("Wrong docker command.");
                    return Response.serverError().entity(response).build();
            }
            String[] response = {"docker files missing", "directory = " + ServerSystemUtil.getBenchmarkDockerDirectory() + containerName};
            logger.error("docker files missing", "directory = " + ServerSystemUtil.getBenchmarkDockerDirectory() + containerName);
            return Response.serverError().entity(response).build();
        }
        String[] response = {"docker not installed or daemon not running"};
        logger.error("docker not installed or daemon not running");
        return Response.serverError().entity(response).build();
    }

    @GET
    @Path("docker/stop")
    public Response stopDockerContainer(@QueryParam("containerName") String containerName) {
        if (DockerCommandLineUtil.isDockerInstalled()) {
            List<String> stopResult = DockerCommandLineUtil.stopContainer(containerName);
            if (DockerCommandLineUtil.isDockerContainerRunning(containerName)) {
                stopResult.add("Docker container " + containerName + " is still running");
                logger.error("Docker container " + containerName + " is still running");
                return Response.serverError().entity(stopResult.toArray()).build();
            }
            // all went good
            stopResult.add("Docker container " + containerName + " stopped");
            return Response.ok().entity(stopResult.toArray()).build();
        }
        String[] result = {"Docker is not installed or running."};
        logger.error("Docker is not installed or running.");
        return Response.serverError().entity(result).build();
    }

    /**
     * Removes all docker containers related to imageName.
     * If removeFiles is selected, removes the image with imageName and the related files.
     *
     * @param imageName the image name
     * @param removeFiles "yes", "true" or "y" to delete image and all files.
     * @return the server response with server cli output in entity as String[].
     */
    @GET
    @Path("docker/remove")
    public Response removeDockerContainer(@QueryParam("imageName") String imageName,
                                      @QueryParam("removeFiles") ChronixBoolean removeFiles) {
        List<String> result = new LinkedList<String>();
        if(DockerCommandLineUtil.isDockerInstalled()){
            result.addAll(DockerCommandLineUtil.stopContainer(imageName));
            List<String> containerIDs = DockerCommandLineUtil.getAllContainerIds(imageName);
            result.addAll(DockerCommandLineUtil.deleteContainer(containerIDs));
        }

        if (removeFiles.getValue() == true) {
            File directory = new File(ServerSystemUtil.getBenchmarkDockerDirectory() + imageName);
            if (directory.exists()) {
                if(DockerCommandLineUtil.isDockerInstalled()){
                    String[] commandLine = {"docker rmi -f " + imageName};
                    String[] command = ServerSystemUtil.getOsSpecificCommand(commandLine);
                    result.addAll(ServerSystemUtil.executeCommand(command));
                }

                // delete directory
                result.add(ServerSystemUtil.deleteDirectory(directory.toPath()));

            } else {
                result.add("No directory named " + imageName + " found.");
                logger.warn("No directory named " + imageName + " found.");
            }
        }

        return Response.ok().entity(result.toArray()).build();
    }


}
