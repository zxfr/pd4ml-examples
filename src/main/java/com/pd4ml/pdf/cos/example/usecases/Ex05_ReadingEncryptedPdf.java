package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.examples.support.SamplePdf;

/** 05 -- Reading an encrypted PDF: COSParser decrypts transparently, given the right password. */
public final class Ex05_ReadingEncryptedPdf {

    public static void main(String[] args) throws Exception {
        // com.pd4ml.pdf.cos.security only reads encrypted PDFs (no write support of its own);
        // PdfEncryptor (used by com.pd4ml.pdf.merge/optimizer for output encryption) builds
        // this example's encrypted fixture.
        COSDocument plain = COSParser.load(SamplePdf.onePage());
        PdfEncryptor.encrypt(plain, new PdfEncryptor.Options()
                .userPassword("open")
                .ownerPassword("owner")
                .algorithm(PdfEncryptor.Algorithm.AES_256));
        byte[] encrypted = COSDocumentWriter.write(plain);

        COSDocument noPassword = COSParser.load(encrypted);
        System.out.println("No password: isEncrypted=" + noPassword.isEncrypted()
                + ", encryptionError=" + (noPassword.getEncryptionError() != null));

        COSDocument withPassword = COSParser.load(encrypted, "open");
        System.out.println("With password: isEncrypted=" + withPassword.isEncrypted()
                + ", encryptionError=" + (withPassword.getEncryptionError() != null)
                + ", pages=" + PageTree.countPages(withPassword.getCatalog()));
    }
}
