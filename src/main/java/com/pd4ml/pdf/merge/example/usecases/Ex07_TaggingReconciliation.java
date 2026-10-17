package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMerger;

/**
 * 07 -- Merging one tagged source with one untagged source: the tagged
 * page keeps its accessibility structure, the untagged page simply comes
 * along without any -- best-effort, not all-or-nothing.
 */
public final class Ex07_TaggingReconciliation {

    public static void main(String[] args) throws Exception {
        byte[] tagged = SamplePdf.onePageTagged();
        byte[] untagged = SamplePdf.onePage();

        PdfMerger merger = new PdfMerger();
        merger.addSource(tagged).selectPages("1");
        merger.addSource(untagged).selectPages("1");
        byte[] merged = merger.mergeToBytes();

        COSDictionary catalog = COSParser.load(merged).getCatalog();
        COSDictionary structTreeRoot = catalog.getDictionary("StructTreeRoot");
        System.out.println("Merged document has /StructTreeRoot: " + (structTreeRoot != null));
        if (structTreeRoot != null) {
            System.out.println("/StructTreeRoot/ParentTree/Nums: "
                    + structTreeRoot.getDictionary("ParentTree").getArray("Nums"));
        }

        // If NEITHER source is tagged, the merged document gets no /StructTreeRoot at all.
        PdfMerger plainMerger = new PdfMerger();
        plainMerger.addSource(untagged).selectPages("1");
        plainMerger.addSource(SamplePdf.onePage()).selectPages("1");
        COSDictionary plainCatalog = COSParser.load(plainMerger.mergeToBytes()).getCatalog();
        System.out.println("Both sources untagged, /StructTreeRoot: " + plainCatalog.getDictionary("StructTreeRoot"));
    }
}
