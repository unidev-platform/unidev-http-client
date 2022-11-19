package com.unidev.httpclient.socks;

import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class PlainSocksSocketFactory implements ConnectionSocketFactory {
    private String socksIP;
    private int socksPort;

    public PlainSocksSocketFactory(String socksIP, int socksPort) {
        this.socksIP = socksIP;
        this.socksPort = socksPort;
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return new Socket();
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket sock, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {

        InetSocketAddress socksaddr = new InetSocketAddress(socksIP, socksPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        Socket socks = new Socket(proxy);

        socks.bind(localAddress);

        try {
            socks.connect(remoteAddress, connectTimeout);
        } catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress.getHostName() + "/" + remoteAddress.getAddress() + " timed out");
        }
        return socks;
    }
}
