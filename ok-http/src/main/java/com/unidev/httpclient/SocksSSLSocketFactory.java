package com.unidev.httpclient;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.*;

/**
 * Factory of SSL sockets created through socks proxy.
 */
public class SocksSSLSocketFactory extends SSLSocketFactory {

    private final String socksIp;

    private final int socksPort;

    private final SocksSocketFactory socksSocketFactory;

    public SocksSSLSocketFactory(String socksIp, int socksPort) {
        this.socksIp = socksIp;
        this.socksPort = socksPort;
        this.socksSocketFactory = new SocksSocketFactory(socksIp, socksPort);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
        SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        return defaultSSLSocketFactory.createSocket(socket, s, i, b);
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return socksSocketFactory.createSocket(s, i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return socksSocketFactory.createSocket(s, i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return socksSocketFactory.createSocket(inetAddress, i);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return socksSocketFactory.createSocket(inetAddress, i, inetAddress1, i1);
    }
}
