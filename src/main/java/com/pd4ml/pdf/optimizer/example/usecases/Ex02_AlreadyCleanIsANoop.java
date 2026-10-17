package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.optimizer.PdfOptimizeResult;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

/** 02 -- Optimizing an already-clean, from-scratch document reports zero removed objects -- it's a report, not an assumption. */
public final class Ex02_AlreadyCleanIsANoop {

    public static void main(String[] args) throws Exception {
        byte[] clean = SamplePdf.nPages(2);

        PdfOptimizeResult result = PdfOptimizer.optimize(clean);
        System.out.println("Removed: " + result.getRemovedObjectCount() + " (expected 0 -- nothing was orphaned).");
        System.out.println("Object count unchanged: " + result.getInputObjectCount() + " -> " + result.getOutputObjectCount());
    }
}
