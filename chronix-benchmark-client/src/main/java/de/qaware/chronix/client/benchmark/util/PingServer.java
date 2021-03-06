package de.qaware.chronix.client.benchmark.util;

import de.qaware.chronix.client.benchmark.configurator.Configurator;

/**
 * Created by mcqueen666 on 07.10.16.
 */
public class PingServer {
    public static void main(String[] args){
        if(args.length < 1){
            printPingUsage();
            return;
        }
        String server = args[0];
        /*ServerConfigRecord serverConfigRecord = ServerConfigAccessor.getInstance().getServerConfigRecord(server);
        if(serverConfigRecord == null){
            System.err.println("Server: " + server + " was not configured!");
            return;
        }*/

        try {
            if(Configurator.getInstance().isServerUp(server)){
                System.out.println("Server is up");
            } else {
                System.out.println("Server not responding");
            }
        } catch (Exception e){
            System.err.println("Server not reachable. Reason: " + e.getLocalizedMessage());
        }

    }

    private static void printPingUsage(){
        System.out.println("Print usage: print [server]");
    }
}
