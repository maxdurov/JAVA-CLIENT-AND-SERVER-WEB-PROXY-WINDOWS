package org.webproxy;

import java.net.InetSocketAddress;

public class PROTOCOL {

    public final static int MAX_THREADS_POOL = 60; // 60
    public final static int BUFFER_SIZE_CLIENT_SERVER = 1024 * 13;
    public final static int BUFFER_SIZE_SERVER = 1024 * 13;
    public final static InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(8080);
    public final static InetSocketAddress CLIENT_SERVER_ADDRESS = new InetSocketAddress(3000);

    public static void debugOutPut(String out) {
        // System.out.println(out);
    }



}
