package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;
import com.pd4ml.pdf.sign.ltv.LtvOptions;
import com.pd4ml.pdf.sign.ltv.LtvUpdater;
import com.pd4ml.pdf.sign.pdf.SignResult;

import java.io.ByteArrayInputStream;

/** 13 -- Sign, then immediately add PAdES-LT validation info in the same process, using SignResult's raw CMS bytes. */
public final class Ex13_LtvImmediatelyAfterSigning {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        SignResult result = new PdfSigner().signForResult(SamplePdf.onePage(), identity,
                new SigningOptions().setSignerName("Jane Doe"));

        // A self-signed test certificate publishes no real OCSP/CRL endpoint, so this
        // legitimately comes back with warnings and no revocation evidence --
        // RevocationFetcher never throws, and LtvUpdater still embeds the certificate
        // chain itself either way.
        byte[] withLtv = LtvUpdater.addLtv(result.getSignedPdf(), result.getCmsSignature(),
                identity.getCertificateChain(), new LtvOptions());
        System.out.println("Signed + LTV: " + result.getSignedPdf().length + " -> " + withLtv.length + " bytes.");
    }
}
