package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.pdf.cos.COSArray;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.COSString;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.SigningOptions;
import com.pd4ml.pdf.sign.ltv.LtvOptions;
import com.pd4ml.pdf.sign.ltv.LtvUpdater;
import com.pd4ml.pdf.sign.ltv.RevocationFetcher;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 14 -- Adding LTV to a PDF signed earlier or elsewhere, with no original
 * {@code SigningIdentity}/{@code SignResult} in hand: the CMS bytes come
 * from the target signature's own {@code /Contents}, and the certificate
 * chain is recovered from that CMS's own {@code SignedData} -- exactly what
 * {@code pdfsigncli ltv} does internally
 * ({@code PdfSignCli.reconstructCertificateChain}).
 */
public final class Ex14_LtvOnExistingSignedPdf {

    public static void main(String[] args) throws Exception {
        // Stand in for "a PDF signed earlier or elsewhere": produce one now, then work with
        // only its bytes from here on, as if the identity/SignResult that made it were long gone.
        SelfSignedPki.Identity generated = SelfSignedPki.generate("Jane Doe");
        byte[] p12 = generated.toPkcs12("signer", "changeit".toCharArray());
        CertificateUtils.SigningIdentity identity =
                CertificateUtils.loadFromPkcs12(new ByteArrayInputStream(p12), "changeit".toCharArray(), "signer");
        byte[] signedElsewhere = new PdfSigner().sign(SamplePdf.onePage(), identity,
                new SigningOptions().setSignerName("Jane Doe"));

        byte[] cmsBytes = recoverCmsBytes(signedElsewhere);
        Certificate[] chain = reconstructCertificateChain(cmsBytes);

        RevocationFetcher.Result revocation = RevocationFetcher.fetch(chain, new LtvOptions());
        for (String warning : revocation.getWarnings()) {
            System.out.println("warning: " + warning);
        }
        byte[] withLtv = LtvUpdater.embedValidationInformation(signedElsewhere, cmsBytes, chain, revocation);
        System.out.println("Recovered CMS bytes + a " + chain.length + "-certificate chain with no original "
                + "identity in hand; LTV added: " + withLtv.length + " bytes.");
    }

    private static byte[] recoverCmsBytes(byte[] signedPdf) throws Exception {
        COSDocument document = COSParser.load(signedPdf);
        COSDictionary acroForm = document.getCatalog().getDictionary("AcroForm");
        COSArray fields = acroForm.getArray("Fields");
        COSDictionary field = (COSDictionary) fields.get(0).resolve();
        COSDictionary sigDict = (COSDictionary) field.getItem("V").resolve();
        return ((COSString) sigDict.getItem("Contents")).getBytes();
    }

    /** Same recovery technique as {@code PdfSignCli}'s {@code ltv} subcommand. */
    private static Certificate[] reconstructCertificateChain(byte[] cmsBytes) throws Exception {
        CMSSignedData signedData = new CMSSignedData(cmsBytes);
        SignerInformation signerInfo = signedData.getSignerInfos().getSigners().iterator().next();

        @SuppressWarnings("unchecked")
        Collection<X509CertificateHolder> holders = signedData.getCertificates().getMatches(null);

        X509CertificateHolder signer = null;
        for (X509CertificateHolder h : holders) {
            if (signerInfo.getSID().match(h)) {
                signer = h;
                break;
            }
        }
        if (signer == null) {
            throw new IllegalStateException("Could not find the signer's own certificate inside the CMS SignedData");
        }

        List<X509CertificateHolder> chain = new ArrayList<X509CertificateHolder>();
        Set<X509CertificateHolder> used = new HashSet<X509CertificateHolder>();
        chain.add(signer);
        used.add(signer);
        X509CertificateHolder current = signer;
        while (!current.getSubject().equals(current.getIssuer())) {
            X509CertificateHolder issuer = null;
            for (X509CertificateHolder h : holders) {
                if (!used.contains(h) && h.getSubject().equals(current.getIssuer())) {
                    issuer = h;
                    break;
                }
            }
            if (issuer == null) {
                break; // issuer not embedded in the CMS; chain ends here (still usable for LTV)
            }
            chain.add(issuer);
            used.add(issuer);
            current = issuer;
        }

        JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
        Certificate[] result = new Certificate[chain.size()];
        for (int i = 0; i < chain.size(); i++) {
            result[i] = converter.getCertificate(chain.get(i));
        }
        return result;
    }
}
