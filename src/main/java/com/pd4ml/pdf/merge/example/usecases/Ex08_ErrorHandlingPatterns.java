package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMergeException;
import com.pd4ml.pdf.merge.PdfMerger;

/** 08 -- Every {@link PdfMergeException} failure mode worth knowing by name. */
public final class Ex08_ErrorHandlingPatterns {

    public static void main(String[] args) throws Exception {
        try {
            new PdfMerger().mergeToBytes();
        } catch (PdfMergeException e) {
            System.out.println("no sources: " + e.getMessage());
        }

        try {
            PdfMerger merger = new PdfMerger();
            merger.addSource(SamplePdf.onePage()); // no selectPages -- fine alone, ambiguous with company
            merger.addSource(SamplePdf.onePage()).selectPages("1");
            merger.mergeToBytes();
        } catch (PdfMergeException e) {
            System.out.println("missing selectPages with 2+ sources: " + e.getMessage());
        }

        try {
            new PdfMerger().addSource(SamplePdf.nPages(2)).selectPages("5");
        } catch (PdfMergeException e) {
            System.out.println("out-of-range page: " + e.getMessage());
        }

        try {
            new PdfMerger().addSource(SamplePdf.nPages(2)).selectPages("odd,even");
        } catch (PdfMergeException e) {
            System.out.println("odd+even together: " + e.getMessage());
        }
    }
}
