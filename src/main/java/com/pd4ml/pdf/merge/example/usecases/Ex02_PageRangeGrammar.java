package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMerger;
import com.pd4ml.pdf.merge.PdfMergeSource;

/**
 * 02 -- The page-range grammar: {@code N}, {@code N-M} (descending too),
 * {@code N+}, and {@code odd}/{@code even} as suppressing filters over
 * everything else in the same call, with deduplication.
 */
public final class Ex02_PageRangeGrammar {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.nPages(10);

        show("2-5", pdf);         // a plain range
        show("5-2", pdf);         // descending, still honored for ordering
        show("6+", pdf);          // to the last page
        show("odd", pdf);         // alone: every odd page of the whole document
        show("1-2,4+,odd", pdf);  // union first (1,2,4..10), then odd suppresses the even ones -> 1,5,7,9
        show("1,1,2,2", pdf);     // deduplicated -> 1,2
    }

    private static void show(String range, byte[] pdf) throws Exception {
        PdfMerger merger = new PdfMerger();
        PdfMergeSource source = merger.addSource(pdf);
        source.selectPages(range);
        byte[] merged = merger.mergeToBytes();
        int pages = PageTree.countPages(COSParser.load(merged).getCatalog());
        System.out.println("\"" + range + "\" -> " + pages + " page(s)");
    }
}
