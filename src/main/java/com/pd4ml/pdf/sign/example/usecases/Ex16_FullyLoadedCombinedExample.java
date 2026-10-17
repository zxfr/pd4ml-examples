package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.PdfSigningException;
import com.pd4ml.pdf.sign.SigningOptions;
import com.pd4ml.pdf.sign.VisibleSignatureOptions;
import com.pd4ml.pdf.sign.ltv.LtvOptions;
import com.pd4ml.pdf.sign.ltv.LtvUpdater;
import com.pd4ml.pdf.sign.pdf.SignResult;

import java.io.ByteArrayInputStream;

/**
 * 16 -- Every major option combined: visible + certify + timestamp + LTV.
 * The timestamp step is a live network call (see {@link Ex05_RfcTimestamping});
 * a failure there is reported, not thrown out of {@code main}.
 */
public final class Ex16_FullyLoadedCombinedExample {

    public static void main(String[] args) throws Exception {
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");

        VisibleSignatureOptions visible = new VisibleSignatureOptions()
                .setPage(0)
                .setRectangle(36, 36, 220, 70)
                .setVisibleText("Digitally signed and certified by Jane Doe");

        SigningOptions options = new SigningOptions()
                .setReason("Final approval")
                .setLocation("Berlin, DE")
                .setSignerName("Jane Doe")
                .setCertificationLevel(SigningOptions.CertificationLevel.NO_CHANGES_ALLOWED)
                .setVisibleSignatureOptions(visible)
                .setTsaUrl("http://timestamp.digicert.com")
                .setSignaturePlaceholderSize(32768);

        try {
            SignResult result = new PdfSigner().signForResult(SamplePdf.onePage(), identity, options);
            byte[] withLtv = LtvUpdater.addLtv(result.getSignedPdf(), result.getCmsSignature(),
                    identity.getCertificateChain(), new LtvOptions());
            System.out.println("Visible + certified + timestamped + LTV: " + withLtv.length + " bytes.");
        } catch (PdfSigningException e) {
            System.out.println("Timestamping step failed (no/blocked network access in this environment?): " + e.getMessage());
        }
    }
}
