package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMerger;

/** 01 -- The minimal call: select a page range from two sources and merge them into one output. Start here. */
public final class Ex01_BasicTwoSourceMerge {

    public static void main(String[] args) throws Exception {
        byte[] pdfA = SamplePdf.nPages(5);
        byte[] pdfB = SamplePdf.nPages(3);

        PdfMerger merger = new PdfMerger();
        merger.addSource(pdfA).selectPages("2-4");
        merger.addSource(pdfB).selectPages("1,3");
        byte[] merged = merger.mergeToBytes();

        System.out.println("Merged " + pdfA.length + " + " + pdfB.length + " -> " + merged.length
                + " bytes (5 pages: A's 2,3,4 then B's 1,3, object numbers renumbered so they never collide).");
    }
}
