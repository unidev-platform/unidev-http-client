package com.unidev.httpclient;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Manager which trust any X509 certificate.
 */
public class TrustAllX509TrustManager implements X509TrustManager {

    public static final TrustAllX509TrustManager INSTANCE = new TrustAllX509TrustManager();

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
