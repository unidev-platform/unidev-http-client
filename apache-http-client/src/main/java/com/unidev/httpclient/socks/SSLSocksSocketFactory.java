package com.unidev.httpclient.socks;

import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class SSLSocksSocketFactory implements LayeredConnectionSocketFactory {

    public static final String TLS = "TLS";
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";

    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER
            = new AllowAllHostnameVerifier();
    // volatile is needed to guarantee thread-safety of the setter/getter methods under all usage scenarios
    private volatile X509HostnameVerifier hostnameVerifier = ALLOW_ALL_HOSTNAME_VERIFIER;
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
            = new BrowserCompatHostnameVerifier();
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER
            = new StrictHostnameVerifier();
    private final SSLContext sslcontext;
    private final SSLSocketFactory socketfactory;
    private final HostNameResolver nameResolver;
    /**
     * Creates the default SSL socket factory.
     * This constructor is used exclusively to instantiate the factory for
     */
    String socks_ip = "";
    int socks_port = 0;

    public SSLSocksSocketFactory(String ip, int port) {
        super();
        this.socks_ip = ip;
        this.socks_port = port;
        this.sslcontext = null;
        this.socketfactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        this.nameResolver = null;
    }

    private static KeyManager[] createKeyManagers(final KeyStore keystore, final String password)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, password != null ? password.toCharArray() : null);
        return kmfactory.getKeyManagers();
    }

    private static TrustManager[] createTrustManagers(final KeyStore keystore)
            throws KeyStoreException, NoSuchAlgorithmException {
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(keystore);
        return tmfactory.getTrustManagers();
    }


    // non-javadoc, see interface org.apache.http.conn.SocketFactory
    @SuppressWarnings("cast")
    public Socket createSocket()
            throws IOException {

        // the cast makes sure that the factory is working as expected
        //System.out.println("Create...\n\n\n");
        // return (SSLSocket) this.socketfactory.createSocket();

        String proxyHost = (String) socks_ip;
        Integer proxyPort = (Integer) socks_port;
        InetSocketAddress socksaddr = new InetSocketAddress(proxyHost, proxyPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        Socket p_sock = new Socket(proxy);
        return p_sock;
    }


    // non-javadoc, see interface org.apache.http.conn.SocketFactory
    public Socket connectSocket(
            final Socket sock,
            final String host,
            final int port,
            final InetAddress localAddress,
            int localPort,
            final HttpParams params
    ) throws IOException {
        //System.out.println("connectSocket...\n\n\n");
        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }

        String proxyHost = (String) socks_ip;
        Integer proxyPort = (Integer) socks_port;
        SSLSocket sslsock = null;
        if (sock == null) {
            InetSocketAddress socksaddr = new InetSocketAddress(proxyHost, proxyPort);
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            Socket p_sock = new Socket(proxy);
            sslsock = (SSLSocket) createSocket(p_sock, proxyHost, localPort, true);
        } else
            sslsock = (SSLSocket) sock;
        
       /* SSLSocket sslsock = (SSLSocket)
            ((sock != null) ? sock : createSocket());*/

        if ((localAddress != null) || (localPort > 0)) {

            // we need to bind explicitly
            if (localPort < 0)
                localPort = 0; // indicates "any"

            InetSocketAddress isa =
                    new InetSocketAddress(localAddress, localPort);
            sslsock.bind(isa);
        }

        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress;
        if (this.nameResolver != null) {
            remoteAddress = new InetSocketAddress(this.nameResolver.resolve(host), port);
        } else {
            remoteAddress = new InetSocketAddress(host, port);
        }

        try {
            sslsock.connect(remoteAddress, connTimeout);
        } catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
        }
        sslsock.setSoTimeout(soTimeout);
        try {
            hostnameVerifier.verify(host, sslsock);
            // verifyHostName() didn't blowup - good!
        } catch (IOException iox) {
            // close the socket before re-throwing the exception
            try {
                sslsock.close();
            } catch (Exception x) { /*ignore*/ }
            throw iox;
        }

        return sslsock;
    }


    /**
     * Checks whether a socket connection is secure.
     * This factory creates TLS/SSL socket connections
     * which, by default, are considered secure.
     * <br/>
     * Derived classes may override this method to perform
     * runtime checks, for example based on the cypher suite.
     *
     * @param sock the connected socket
     * @return <code>true</code>
     * @throws IllegalArgumentException if the argument is invalid
     */
    public boolean isSecure(Socket sock)
            throws IllegalArgumentException {

        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }
        // This instanceof check is in line with createSocket() above.
        if (!(sock instanceof SSLSocket)) {
            throw new IllegalArgumentException
                    ("Socket not created by this factory.");
        }
        // This check is performed last since it calls the argument object.
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed.");
        }

        return true;

    } // isSecure


    // non-javadoc, see interface LayeredSocketFactory
    public Socket createSocket(
            final Socket socket,
            final String host,
            final int port,
            final boolean autoClose
    ) throws IOException, UnknownHostException {
        //System.out.println("create 2..\n " + socket + " " + host + "  " + port);

        String proxyHost = (String) socks_ip;
        Integer proxyPort = (Integer) socks_port;
        InetSocketAddress socksaddr = new InetSocketAddress(proxyHost, proxyPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        Socket p_sock = new Socket(proxy);
        return p_sock;
        //p_sock.connect(new InetSocketAddress(host, port));
        /*SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(
                p_sock,
              host,
              port,
              autoClose
        );
        //hostnameVerifier.verify(host, sslSocket);
        // verifyHostName() didn't blowup - good!
        return sslSocket;*/
    }

    public X509HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        if (hostnameVerifier == null) {
            throw new IllegalArgumentException("Hostname verifier may not be null");
        }
        this.hostnameVerifier = hostnameVerifier;
    }

    // ------------------------------------------------------------------------------------------------------

    @Override
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException, UnknownHostException {
        return this.socketfactory.createSocket(socket, target, port, true);
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        InetSocketAddress socksaddr = new InetSocketAddress(socks_ip, socks_port);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        Socket proxySock = new Socket(proxy);
        return proxySock;
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket sock, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        String proxyHost = (String) socks_ip;
        Integer proxyPort = (Integer) socks_port;
        SSLSocket sslsock = null;
        if (sock instanceof SSLSocket)
            sslsock = (SSLSocket) sock;
        else {
            if (!sock.isConnected()) {
                sock.connect(remoteAddress);
            }
            sslsock = (SSLSocket) socketfactory.createSocket(sock, remoteAddress.getHostName(), remoteAddress.getPort(), true);
        }

        if (localAddress != null) {
            sslsock.bind(localAddress);
        }
        sslsock.setSoTimeout(connectTimeout);
        try {
            hostnameVerifier.verify(remoteAddress.getAddress().getHostName(), sslsock);
            // verifyHostName() didn't blowup - good!
        } catch (IOException iox) {
            // close the socket before re-throwing the exception
            try {
                sslsock.close();
            } catch (Exception x) { /*ignore*/ }
            throw iox;
        }
        return sslsock;
    }
}
