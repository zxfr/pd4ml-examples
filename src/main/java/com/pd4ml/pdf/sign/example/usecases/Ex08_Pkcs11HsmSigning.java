package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.PdfSigningException;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.File;

/**
 * 08 -- Signing with a PKCS#11 hardware token/HSM: the private key never
 * leaves the token, every RSA/EC operation is delegated to it via a
 * SunPKCS11 provider instantiated from a vendor-supplied config file.
 * <p>
 * This is the exact call shape a real deployment uses; there is no real
 * PKCS#11 token/module available in this environment, so this example
 * demonstrates the shape and reports the resulting (expected) failure
 * rather than a successful signature. On real hardware, pass your vendor's
 * SunPKCS11 config file and the token's real PIN as {@code args[0]}/{@code args[1]}.
 */
public final class Ex08_Pkcs11HsmSigning {

    public static void main(String[] args) throws Exception {
        File pkcs11Config = new File(args.length > 0 ? args[0] : "pkcs11-slot0.cfg");
        char[] pin = (args.length > 1 ? args[1] : "123456").toCharArray();

        try {
            CertificateUtils.SigningIdentity identity = CertificateUtils.loadFromPkcs11(pkcs11Config, pin);
            byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity,
                    new SigningOptions().setSignerName("HSM Signer"));
            System.out.println("Signed via PKCS#11: " + signedPdf.length + " bytes.");
        } catch (PdfSigningException e) {
            System.out.println("Expected in this environment (no real PKCS#11 token/module configured): " + e.getMessage());
        }
    }
}
