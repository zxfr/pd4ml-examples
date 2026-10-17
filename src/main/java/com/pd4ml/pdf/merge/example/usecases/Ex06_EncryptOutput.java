package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMerger;

/** 06 -- Encrypting the merged output, either with the AES-256 shortcut or full PdfEncryptor.Options control. */
public final class Ex06_EncryptOutput {

    public static void main(String[] args) throws Exception {
        byte[] pdfA = SamplePdf.nPages(2);
        byte[] pdfB = SamplePdf.nPages(2);

        PdfMerger merger = new PdfMerger();
        merger.addSource(pdfA).selectPages("1");
        merger.addSource(pdfB).selectPages("1");
        merger.encryptOutput("openPassword", "ownerPassword");
        byte[] merged = merger.mergeToBytes();

        System.out.println("isEncrypted (no password needed to check): " + COSParser.load(merged).isEncrypted());
        System.out.println("Pages (with password): " + PageTree.countPages(COSParser.load(merged, "openPassword").getCatalog()));

        // Full control: algorithm choice + raw /P permission bits.
        PdfMerger merger2 = new PdfMerger();
        merger2.addSource(pdfA).selectPages("1");
        merger2.encryptOutput(new PdfEncryptor.Options()
                .userPassword("openPassword")
                .algorithm(PdfEncryptor.Algorithm.AES_128)
                .permissions(-4));
        System.out.println("AES-128 output: " + merger2.mergeToBytes().length + " bytes.");
    }
}
