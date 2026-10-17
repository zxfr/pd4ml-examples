package com.pd4ml.pdf.sign.example;

import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.io.File;

/**
 * The simplest possible reference: sign a PDF file with a PKCS#12 key and an
 * optional RFC 3161 timestamp, from the command line.
 * <pre>
 * java -cp pdfsign-api-1.0.0-with-dependencies.jar com.pd4ml.pdf.sign.example.SignPdfExample \
 *      input.pdf signed.pdf signer.p12 keystorePassword [tsaUrl]
 * </pre>
 * For anything more (visible signatures, certification, LTV, PKCS#11, cloud
 * KMS, ...) see {@code com.pd4ml.pdf.sign.example.usecases} -- 18 focused
 * examples, one per feature, each fully self-contained (they build their
 * own sample PDF and test certificate, so they need no external files at
 * all). This class is the one exception: it expects real files, the same
 * way {@code pdfsigncli sign} does.
 */
public final class SignPdfExample {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: SignPdfExample <input.pdf> <output.pdf> <keystore.p12> <password> [tsaUrl]");
            System.exit(1);
            return;
        }
        File input = new File(args[0]);
        File output = new File(args[1]);
        File keystore = new File(args[2]);
        char[] password = args[3].toCharArray();

        CertificateUtils.SigningIdentity identity = CertificateUtils.loadFromPkcs12(keystore, password);

        SigningOptions options = new SigningOptions()
                .setReason("Document approval")
                .setSignerName(identity.getSigningCertificate().getSubjectX500Principal().getName());
        if (args.length >= 5) {
            options.setTsaUrl(args[4]);
        }

        new PdfSigner().sign(input, output, identity, options);
        System.out.println("Signed " + input + " -> " + output);
    }
}
