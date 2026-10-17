package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.optimizer.PdfOptimizeResult;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

/**
 * 01 -- The minimal call: clean up a PDF that accumulated unreferenced
 * objects through a real incremental-update edit (an orphaned second page).
 * Start here.
 */
public final class Ex01_BasicCleanup {

    public static void main(String[] args) throws Exception {
        byte[] withOrphan = SamplePdf.withOrphanedSecondPage(SamplePdf.nPages(2));

        PdfOptimizeResult result = PdfOptimizer.optimize(withOrphan);

        System.out.println("Input: " + result.getInputByteCount() + " bytes, " + result.getInputObjectCount() + " objects");
        System.out.println("Output: " + result.getOutputByteCount() + " bytes, " + result.getOutputObjectCount() + " objects");
        System.out.println("Removed: " + result.getRemovedObjectCount() + " unreferenced object(s)");
    }
}
