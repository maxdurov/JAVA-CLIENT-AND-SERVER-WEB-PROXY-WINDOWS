package org.webproxy.client;

import org.webproxy.PROTOCOL;

import java.net.InetSocketAddress;

public class ClientProxy {


    public static void main(String[] args) throws Exception {

        argumentParsing(args);

    }

    public static void argumentParsing(String[] args) throws Exception {
        InetSocketAddress proxy_server_address = PROTOCOL.SERVER_ADDRESS;
        InetSocketAddress local_address = PROTOCOL.CLIENT_SERVER_ADDRESS;
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
                    case "--remote-server" -> {
                        proxy_server_address = new InetSocketAddress((args[index + 1].split(":"))[0], Integer.parseInt((args[index + 1].split(":"))[1]));
                        nextSkip = true;
                    }
                    case "--max-threads-pool" -> {
                        max_threads = Integer.parseInt(args[index+1]);
                        nextSkip = true;
                    }
                    case "--help" -> {
                        System.out.println(
                                "--port [port] - port for running a local proxy (Default: " + PROTOCOL.CLIENT_SERVER_ADDRESS.getPort() + ")\n" +
                                        "--remote-server [host]:[port] - address to remote proxy server (Default: localhost:"+PROTOCOL.SERVER_ADDRESS.getPort() + ")\n" +
                                        "--max-threads-pool [count] - maximum threads (Default: " + PROTOCOL.MAX_THREADS_POOL +")\n" +
                                        "--help - show this message"
                        );
                        return;
                    }
                    default -> {
                        System.out.println("INVALID ARGUMENT -> " + str);
                        System.out.println(
                                "--port [port] - port for running a local proxy (Default: " + PROTOCOL.CLIENT_SERVER_ADDRESS.getPort() + ")\n" +
                                        "--remote-server [host]:[port] - address to remote proxy server (Default: localhost:"+PROTOCOL.SERVER_ADDRESS.getPort() + ")\n" +
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

        try {
            new ClientProxyServer(local_address, proxy_server_address, max_threads);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
