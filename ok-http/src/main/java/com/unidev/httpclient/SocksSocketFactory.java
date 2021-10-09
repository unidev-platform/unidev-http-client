package com.unidev.httpclient;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.*;

/**
 * Socket factory which create Sockets through provided socks server.
 */
public class SocksSocketFactory extends SocketFactory {

    private final String socksIp;

    private final int socksPort;

    public SocksSocketFactory(String socksIp, int socksPort) {
        this.socksIp = socksIp;
        this.socksPort = socksPort;
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return createSocket(InetAddress.getByName(s), i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        Socket socket = createSocket(s, i);
        socket.bind(new InetSocketAddress(inetAddress, i1));
        return socket;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socksIp, socksPort));
        Socket socks = new Socket(proxy);
        socks.connect(new InetSocketAddress(inetAddress, i));
        return socks;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        Socket socket = createSocket(inetAddress, i);
        socket.bind(new InetSocketAddress(inetAddress, i1));
        return socket;
    }
}
