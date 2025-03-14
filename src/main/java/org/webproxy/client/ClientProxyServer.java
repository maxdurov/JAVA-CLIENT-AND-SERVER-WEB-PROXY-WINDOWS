package org.webproxy.client;

import org.webproxy.PROTOCOL;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientProxyServer {

    ExecutorService threadPool;
    ServerSocketChannel serverSocket;

    public ClientProxyServer(InetSocketAddress local_port, InetSocketAddress proxy_server_address, int max_threads) throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(local_port);
        PROTOCOL.debugOutPut("CLIENT SERVER START ON " + local_port.getPort() + " port");

        threadPool = Executors.newFixedThreadPool(max_threads);

        threadPool.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println("ACTIVE THREADS: " + ((ThreadPoolExecutor) threadPool).getActiveCount() + "/" + ((ThreadPoolExecutor) threadPool).getMaximumPoolSize());
                    System.out.println("IN LINE: " + ((ThreadPoolExecutor) threadPool).getQueue().size());
                    System.out.println("LOCAL PORT: " + local_port.getPort());
                    System.out.println("REMOTE ADDRESS: " +proxy_server_address.toString());
                } catch (Exception e) {

                }
            }
        });

        while (true) {
            SocketChannel socketChannel = serverSocket.accept();
            PROTOCOL.debugOutPut("NEW CLIENT");
            threadPool.execute(() -> {
                PROTOCOL.debugOutPut("RUN THREAD");
                try {
                    new ClientProxyHandler(socketChannel, proxy_server_address);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
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
