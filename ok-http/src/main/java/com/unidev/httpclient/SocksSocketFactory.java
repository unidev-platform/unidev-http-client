package com.unidev.httpclient;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
        return null;
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return null;
    }
}
