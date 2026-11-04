package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.optimizer.PdfOptimizeResult;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

/**
 * 08 -- Linearized ("Fast Web View") output (PD4ML v4.2.0): the first page's objects come first
 * and a hint table tells the viewer where every other page is, so a browser loading the file over
 * the network shows the first page, and then any page, before the whole file has arrived.
 * <p>
 * Linearization is combined with the usual cleanup. PDF 1.5+ output keeps its compact layout
 * (object streams); PDF/A-1 and encrypted output are written with a classic cross-reference table.
 */
public final class Ex08_LinearizeForFastWebView {

    public static void main(String[] args) throws Exception {
        byte[] input = SamplePdf.nPages(12);

        PdfOptimizeResult plain = PdfOptimizer.optimize(input);
        PdfOptimizeResult linearized = PdfOptimizer.optimize(input, new PdfOptimizer.Options().linearize(true));

        System.out.println("Input:      " + input.length + " bytes");
        System.out.println("Optimized:  " + plain.getOutputByteCount() + " bytes, linearized: " + isLinearized(plain.getOptimizedPdf()));
        System.out.println("Linearized: " + linearized.getOutputByteCount() + " bytes, linearized: "
                + isLinearized(linearized.getOptimizedPdf()));
    }

    /** A linearized PDF announces itself in its first object: a dictionary with /Linearized. */
    private static boolean isLinearized(byte[] pdf) {
        String head = new String(pdf, 0, Math.min(pdf.length, 1024), java.nio.charset.StandardCharsets.ISO_8859_1);
        return head.contains("/Linearized");
    }
}
