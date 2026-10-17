package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.xfdf.XfdfExporter;
import com.pd4ml.pdf.xfdf.XfdfImporter;

/**
 * 07 -- {@code Options.optimizeOutput(boolean)} runs {@code com.pd4ml.pdf.optimizer.PdfOptimizer}
 * (reachability cleanup + duplicate-content merging) on the imported PDF
 * before returning it; {@code Options.encryptOutput(...)} locks the result.
 * Combined, optimization always runs first and encryption is applied to
 * <em>its</em> result -- the optimizer's own reachability walk never has to
 * deal with ciphertext.
 */
public final class Ex07_PostImportOptimizeAndEncrypt {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.onePageWithAnnotations();
        byte[] xfdf = XfdfExporter.export(pdf); // round-trip its own annotations back in, just to have something to import

        XfdfImporter.Options options = new XfdfImporter.Options()
                .updateIfExists(true)
                .optimizeOutput(true)
                .encryptOutput("open", "owner");
        XfdfImporter.Result result = XfdfImporter.importInto(pdf, xfdf, options);

        System.out.println("isEncrypted (no password needed to check): " + COSParser.load(result.getPdf()).isEncrypted());
        System.out.println("Opens with the right password: "
                + (COSParser.load(result.getPdf(), "open").getEncryptionError() == null));
        System.out.println("Output size: " + result.getPdf().length + " bytes.");
    }
}
