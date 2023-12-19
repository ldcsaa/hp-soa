package io.github.hpsocket.soa.framework.util.ssl;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

import java.io.*;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/** <b>SSL 工具类<b> */
public class SSLUtil
{
    public static final String SSL_CONTEXT_TLS_VERSION = "TLSv1.3";

    public static SSLSocketFactory getSingleSocketFactory(final String caCrtFile) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        
        CertificateFactory cf  = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = null;
        
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(caCrtFile)))
        {
            caCert = (X509Certificate)cf.generateCertificate(bis);
        }
        
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("cert-certificate", caCert);
        
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);
        
        SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_TLS_VERSION);
        sslContext.init(null, tmf.getTrustManagers(), null);
        
        return sslContext.getSocketFactory();
    }


    public static SSLSocketFactory getSocketFactory(final String caCrtFile, final String crtFile, 
                                                    final String keyFile, final String password) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        CertificateFactory cf  = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = null;

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(caCrtFile)))
        {
            caCert = (X509Certificate)cf.generateCertificate(bis);
        }
        
        // load client certificate
        X509Certificate cert = null;

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(crtFile)))
        {
            cert = (X509Certificate)cf.generateCertificate(bis);
        }
        
        // load client private key
        KeyPair key = null;
        
        try(PEMParser pemParser = new PEMParser(new FileReader(keyFile)))
        {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            key = converter.getKeyPair((PEMKeyPair)object);
        }

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(caKs);

        // client key and certificates are sent to server, so it can authenticate
        String keyPassword = GeneralHelper.safeString(password);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", cert);
        ks.setKeyEntry("private-key", key.getPrivate(), keyPassword.toCharArray(), new java.security.cert.Certificate[]{cert});
        
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keyPassword.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance(SSL_CONTEXT_TLS_VERSION);
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }
}
