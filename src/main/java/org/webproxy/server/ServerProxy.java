package org.webproxy.server;

import org.webproxy.PROTOCOL;

import java.net.InetSocketAddress;

public class ServerProxy {

    public static void main(String[] args) throws Exception {
        argumentParsing(args);
    }

    public static void argumentParsing(String[] args) throws Exception {
        InetSocketAddress local_address = PROTOCOL.SERVER_ADDRESS;
        int max_threads = PROTOCOL.MAX_THREADS_POOL;

        int index = -1;
        boolean nextSkip = false;
        for (String str : args){

            index++;
            if (!nextSkip) {
                switch (str) {

                    case "--port" -> {
                        local_address = new InetSocketAddress(Integer.parseInt(args[index + 1]));
                        nextSkip = true;
                    }
                    case "--max-threads-pool" -> {
                        max_threads = Integer.parseInt(args[index+1]);
                        nextSkip = true;
                    }
                    case "--help" -> {
                        System.out.println(
                                "--port [port] - port for running this proxy (Default: " + PROTOCOL.SERVER_ADDRESS.getPort() +")\n" +
                                        "--max-threads-pool [count] - maximum threads (Default: " + PROTOCOL.MAX_THREADS_POOL +")\n" +
                                        "--help - show this message"
                        );
                        return;
                    }
                    default -> {
                        System.out.println("INVALID ARGUMENT -> " + str);
                        System.out.println(
                                "--port [port] - port for running this proxy (Default: " + PROTOCOL.SERVER_ADDRESS.getPort() +")\n" +
                                        "--max-threads-pool [count] - maximum threads (Default: " + PROTOCOL.MAX_THREADS_POOL +")\n" +
                                        "--help - show this message"
                        );
                        return;
                    }

                }
            }
            else{
                nextSkip = false;
            }

        }

        new ServerProxyServer(local_address, max_threads);
    }
}
