package org.webproxy.client;

import org.webproxy.PROTOCOL;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientProxyHandler {

    SocketChannel clientSocket;

    SocketChannel serverSocket;

    InetSocketAddress proxy_server_address;

    public ClientProxyHandler(SocketChannel clientSocket, InetSocketAddress proxy_server_address) throws IOException, InterruptedException {
        this.clientSocket = clientSocket;
        this.clientSocket.configureBlocking(false);
        this.proxy_server_address = proxy_server_address;

        if (openConnectToServer()) {
            connectToTunnelServer();
        } else {
            sendError();
            closeAllConnection();
        }
    }

    public boolean openConnectToServer() throws IOException {
        serverSocket = SocketChannel.open();

        try {
            serverSocket.connect(proxy_server_address);
            serverSocket.configureBlocking(false);
        } catch (Exception e) {
            PROTOCOL.debugOutPut("CONNECTION FAIL");
            return false;
        }

        return serverSocket.isConnected();
    }

    public void connectToTunnelServer() throws IOException, InterruptedException {

        ByteBuffer server_buffer = ByteBuffer.allocateDirect(PROTOCOL.BUFFER_SIZE_CLIENT_SERVER);
        ByteBuffer client_buffer = ByteBuffer.allocateDirect(PROTOCOL.BUFFER_SIZE_CLIENT_SERVER);
        int bytesRead;

        int serverBytesRead;
        int clientBytesRead;

        PROTOCOL.debugOutPut("CONNECT TO TUNNEL");

        while (true) {
            try {

                serverBytesRead = serverSocket.read(server_buffer);
                Thread.sleep(1);
                clientBytesRead = clientSocket.read(client_buffer);


                if (serverBytesRead == -1) break;

                if (serverBytesRead > 0) {
                    server_buffer.flip();
                    while (server_buffer.hasRemaining()) {
                        clientSocket.write(server_buffer);
                    }
                    server_buffer.clear();
                }

                if (clientBytesRead == -1) break;

                if (clientBytesRead > 0) {
                    client_buffer.flip();
                    while (client_buffer.hasRemaining()) {
                        serverSocket.write(client_buffer);
                    }
                    client_buffer.clear();

                }

            } catch (Exception e) {
                break;
            }
        }

        PROTOCOL.debugOutPut("CONNECTION IS CLOSED");
        closeAllConnection();
    }

    public void sendError() {
        try {
            ByteBuffer bb = ByteBuffer.wrap("HTTP/1.1 500 Internal Server Error\r\n\r\n".getBytes());
            while (bb.hasRemaining()) {
                clientSocket.write(bb);
            }

        } catch (IOException e) {

        }
    }

    public void closeAllConnection() {

        try {
            clientSocket.shutdownInput();
            clientSocket.shutdownOutput();

            serverSocket.shutdownInput();
            serverSocket.shutdownOutput();

            serverSocket.close();
            clientSocket.close();
        } catch (Exception e) {

        }
    }
}
