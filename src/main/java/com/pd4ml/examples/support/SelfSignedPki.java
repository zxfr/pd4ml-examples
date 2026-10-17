package com.pd4ml.examples.support;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Generates a throwaway RSA key pair plus a self-signed X.509 certificate,
 * entirely with real BouncyCastle/JCA calls -- used by the
 * {@code com.pd4ml.pdf.sign.example.usecases} examples so every one of them is a
 * genuinely runnable, end-to-end demonstration without needing an external
 * {@code .p12} file or a real CA-issued certificate. Not part of the public
 * signing API; a real caller has (or issues) their own certificate instead
 * -- see {@code CertificateUtils.loadFromPkcs12}/{@code loadFromPkcs11} for
 * how to load one.
 */
public final class SelfSignedPki {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private SelfSignedPki() {
    }

    /** A generated identity: the private key, plus a one-certificate chain (the self-signed certificate is its own issuer). */
    public static final class Identity {
        public final PrivateKey privateKey;
        public final X509Certificate certificate;

        Identity(PrivateKey privateKey, X509Certificate certificate) {
            this.privateKey = privateKey;
            this.certificate = certificate;
        }

        public Certificate[] chain() {
            return new Certificate[]{certificate};
        }

        /** Packs this identity into an in-memory PKCS#12 keystore under {@code alias}, protected by {@code password}. */
        public byte[] toPkcs12(String alias, char[] password) throws Exception {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(null, null);
            keystore.setKeyEntry(alias, privateKey, password, new Certificate[]{certificate});
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            keystore.store(out, password);
            return out.toByteArray();
        }

        public ByteArrayInputStream toPkcs12Stream(String alias, char[] password) throws Exception {
            return new ByteArrayInputStream(toPkcs12(alias, password));
        }
    }

    /** Generates a fresh 2048-bit RSA key pair and a self-signed certificate valid for one year, subject {@code CN=<commonName>}. */
    public static Identity generate(String commonName) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        X500Name subject = new X500Name("CN=" + commonName);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 24L * 3600 * 1000);
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 3600 * 1000);

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                subject, serial, notBefore, notAfter, subject, keyPair.getPublic());
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certHolder);

        return new Identity(keyPair.getPrivate(), certificate);
    }
}
