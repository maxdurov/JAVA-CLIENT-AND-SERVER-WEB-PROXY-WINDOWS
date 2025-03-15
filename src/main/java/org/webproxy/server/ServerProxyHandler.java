package org.webproxy.server;

import org.webproxy.PROTOCOL;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ServerProxyHandler {

    SocketChannel clientSocket;

    SocketChannel siteSocket;

    public ServerProxyHandler(SocketChannel clientSocket) throws IOException, InterruptedException {
        this.clientSocket = clientSocket;

        this.clientSocket.configureBlocking(false);
        requestHandler();
    }

    public void requestHandler() throws IOException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 5);
        int bytesRead;

        int wait = 0;

        while (true) {

            bytesRead = clientSocket.read(buffer);
            if (buffer.position() >= 4){
                if (buffer.get(buffer.position()-1) == 10 & buffer.get(buffer.position()-2) == 13 & buffer.get(buffer.position()-3) == 10 & buffer.get(buffer.position()-4) == 13){
                    break;
                }
            }

            if (wait > 5000) break;
            wait = wait+1;
            Thread.sleep(1);
        }
            if (bytesRead > 0) {
                buffer.flip();
                String resp = new String(buffer.array());
                PROTOCOL.debugOutPut(resp + "\nGET: " + buffer.remaining() + " byte");

                String[] header = resp.split("\r\n");

                PROTOCOL.debugOutPut(Arrays.toString(header));

                if (header[0].startsWith("CONNECT")) {

                    PROTOCOL.debugOutPut("DETECT CONNECT");
                    String[] addr = (header[0].split(" ")[1]).replace(" ", "").split(":");

                    if (connectToSite(new InetSocketAddress(addr[0], Integer.parseInt(addr[1])))) {
                        clientSocket.write(ByteBuffer.wrap("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes()));

                        connectToSiteTunnel();
                    } else {
                        clientSocket.close();
                    }
                } else if (Arrays.stream(header).anyMatch(s -> s.toUpperCase().contains("HOST"))) {
                    PROTOCOL.debugOutPut("DETECT");

                    int index = (IntStream.range(0, header.length)
                            .filter(i -> header[i].toUpperCase().contains("HOST"))
                            .findFirst()).getAsInt();

                    String[] addr = header[index].split(":");

                    InetSocketAddress address;
                    if (addr.length < 3) {
                        address = new InetSocketAddress(addr[1].replace(" ", ""), 80);
                    } else {
                        address = new InetSocketAddress(addr[1].replace(" ", ""), Integer.parseInt(addr[2]));
                    }

                    if (connectToSite(address)) {
                        siteSocket.write(buffer);
                        connectToSiteTunnel();
                    }
                } else {
                    System.out.println("ERR METHOD: " + Arrays.toString(header));
                }



        }
        clientSocket.close();
    }


    public boolean connectToSite(InetSocketAddress address) throws IOException {
        siteSocket = SocketChannel.open();

        try {
            siteSocket.connect(address);
            siteSocket.configureBlocking(false);
        } catch (IOException e) {
            PROTOCOL.debugOutPut("CONNECTION FAIL");
            return false;
        }

        return siteSocket.isConnected();
    }

    public void connectToSiteTunnel() throws IOException, InterruptedException {

        ByteBuffer site_buffer = ByteBuffer.allocateDirect(PROTOCOL.BUFFER_SIZE_SERVER);
        ByteBuffer client_buffer = ByteBuffer.allocateDirect(PROTOCOL.BUFFER_SIZE_SERVER);
        int siteBytesRead;
        int clientBytesRead;

        PROTOCOL.debugOutPut("CONNECT TO SITE-TUNNEL");

        while (true) {
            try {

                siteBytesRead = siteSocket.read(site_buffer);

                clientBytesRead = clientSocket.read(client_buffer);

                if (siteBytesRead == -1) break;

                if (siteBytesRead > 0) {
                    site_buffer.flip();

                    while (site_buffer.hasRemaining()) {
                        clientSocket.write(site_buffer);
                    }
                    site_buffer.clear();
                }


                if (clientBytesRead == -1) break;

                if (clientBytesRead > 0) {
                    client_buffer.flip();

                    while (client_buffer.hasRemaining()) {
                        siteSocket.write(client_buffer);
                    }
                    client_buffer.clear();
                }
                Thread.sleep(1);
            } catch (Exception e) {
                break;
            }

        }
        closeConnections();

        PROTOCOL.debugOutPut("CONNECT IS CLOSED");
    }

    public void closeConnections() throws IOException {
        siteSocket.shutdownInput();
        siteSocket.shutdownOutput();
        siteSocket.close();

        clientSocket.shutdownOutput();
        clientSocket.shutdownInput();
        clientSocket.close();
    }

}
