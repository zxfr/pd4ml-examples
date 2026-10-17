package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMerger;

/** 03 -- With exactly one source, selectPages(...) doubles as a filter/reorder pass, not just a subset-of-pages tool. */
public final class Ex03_SingleSourceFilterAndReorder {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.nPages(3);

        PdfMerger merger = new PdfMerger();
        merger.addSource(pdf).selectPages("3,1"); // reversed order, page 2 dropped entirely
        byte[] result = merger.mergeToBytes();

        int pages = PageTree.countPages(COSParser.load(result).getCatalog());
        System.out.println("Reordered/filtered to " + pages + " pages (originally 3): output order is now 3, 1.");

        // With no selectPages() call at all and a single source, it's a pass-through (decrypt/re-encrypt aside).
        PdfMerger passthrough = new PdfMerger();
        passthrough.addSource(pdf);
        byte[] passthroughResult = passthrough.mergeToBytes();
        System.out.println("No selectPages(): " + PageTree.countPages(COSParser.load(passthroughResult).getCatalog())
                + " pages, original order.");
    }
}
