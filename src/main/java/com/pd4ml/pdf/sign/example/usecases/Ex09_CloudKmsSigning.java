package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.examples.support.SelfSignedPki;
import com.pd4ml.pdf.sign.CertificateUtils;
import com.pd4ml.pdf.sign.PdfSigner;
import com.pd4ml.pdf.sign.RemoteSigner;
import com.pd4ml.pdf.sign.SigningOptions;

import java.security.Signature;

/**
 * 09 -- The {@link RemoteSigner} seam for a cloud KMS (AWS KMS, Azure Key
 * Vault, GCP Cloud KMS, ...).
 * <p>
 * This example simulates the KMS with a local, in-process {@code PrivateKey}
 * standing in for "a key that never leaves the remote service", purely so
 * the example is runnable without a real cloud account. A real integration
 * delegates {@link RemoteSigner#sign} to that vendor's SDK instead -- see
 * the comment block below for the AWS KMS call shape.
 */
public final class Ex09_CloudKmsSigning {

    public static void main(String[] args) throws Exception {
        final SelfSignedPki.Identity generated = SelfSignedPki.generate("KMS-backed Signer");

        // Stand-in for a real KMS client: never touches generated.privateKey outside this one
        // method, exactly as a real RemoteSigner never exposes the KMS's own key material.
        RemoteSigner simulatedKms = new RemoteSigner() {
            public byte[] sign(byte[] dataToSign, String jcaSignatureAlgorithm) throws Exception {
                Signature sig = Signature.getInstance(jcaSignatureAlgorithm);
                sig.initSign(generated.privateKey);
                sig.update(dataToSign);
                return sig.sign();
            }
        };

        CertificateUtils.SigningIdentity identity = CertificateUtils.forRemoteKey(generated.chain(), simulatedKms);
        SigningOptions options = new SigningOptions().setSignerName("KMS-backed Signer").setReason("Cloud KMS demo");

        byte[] signedPdf = new PdfSigner().sign(SamplePdf.onePage(), identity, options);
        System.out.println("Signed via the RemoteSigner seam (simulated KMS): " + signedPdf.length + " bytes.");

        /*
         * Real AWS KMS shape (pseudocode -- bring your own AWS SDK dependency):
         *
         * RemoteSigner awsKmsSigner = (dataToSign, jcaSignatureAlgorithm) -> {
         *     SignRequest request = new SignRequest()
         *             .withKeyId(keyId)
         *             .withMessage(ByteBuffer.wrap(dataToSign))
         *             .withMessageType(MessageType.RAW)
         *             .withSigningAlgorithm(mapToKmsAlgorithm(jcaSignatureAlgorithm));
         *     return kmsClient.sign(request).getSignature().array();
         * };
         */
    }
}
