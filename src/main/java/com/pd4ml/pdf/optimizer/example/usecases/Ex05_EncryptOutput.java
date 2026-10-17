package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.optimizer.PdfOptimizeResult;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

/** 05 -- Cleaning a PDF and re-encrypting the output in the same call, via {@code Options.encryptOutput}. */
public final class Ex05_EncryptOutput {

    public static void main(String[] args) throws Exception {
        byte[] withOrphan = SamplePdf.withOrphanedSecondPage(SamplePdf.nPages(2));

        PdfOptimizer.Options options = new PdfOptimizer.Options()
                .encryptOutput("openPassword", "ownerPassword");
        PdfOptimizeResult result = PdfOptimizer.optimize(withOrphan, options);

        COSDocument locked = COSParser.load(result.getOptimizedPdf());
        System.out.println("Output isEncrypted: " + locked.isEncrypted());

        COSDocument opened = COSParser.load(result.getOptimizedPdf(), "openPassword");
        System.out.println("Cleaned + encrypted: " + result.getRemovedObjectCount() + " object(s) removed, "
                + PageTree.countPages(opened.getCatalog()) + " page(s) remain.");
    }
}
