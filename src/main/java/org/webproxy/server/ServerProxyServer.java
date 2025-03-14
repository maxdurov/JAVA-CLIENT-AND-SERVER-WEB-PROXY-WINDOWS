package org.webproxy.server;

import org.webproxy.PROTOCOL;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerProxyServer {
    ExecutorService threadPool;
    ServerSocketChannel serverSocket;

    public ServerProxyServer(InetSocketAddress local_address, int max_threads) throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(local_address);
        PROTOCOL.debugOutPut("SERVER START ON " + local_address.getPort() + " port");
        threadPool = Executors.newFixedThreadPool(max_threads);

        threadPool.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println("ACTIVE THREADS: " + ((ThreadPoolExecutor) threadPool).getActiveCount() + "/" + ((ThreadPoolExecutor) threadPool).getMaximumPoolSize());
                    System.out.println("IN LINE: " + ((ThreadPoolExecutor) threadPool).getQueue().size());
                    System.out.println("LOCAL PORT: " + local_address.getPort());
                } catch (Exception e) {

                }

            }
        });

        while (true) {
            SocketChannel socketChannel = serverSocket.accept();
            PROTOCOL.debugOutPut("NEW CONNECT");
            threadPool.execute(() -> {
                PROTOCOL.debugOutPut("RUN THREAD");
                try {
                    new ServerProxyHandler(socketChannel);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        socketChannel.close();
                    } catch (IOException e) {

                    }
                }
                PROTOCOL.debugOutPut("STOP THREAD");
            });
        }
    }
}
