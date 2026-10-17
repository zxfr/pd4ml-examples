package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMerger;

/**
 * 04 -- Two pages of the SAME source sharing one font object are cloned
 * exactly once in the merged output, thanks to {@code COSObjectImporter}'s
 * per-source cache.
 */
public final class Ex04_SharedResourceDeduplication {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.nPages(2); // both pages share one Helvetica font object

        PdfMerger merger = new PdfMerger();
        merger.addSource(pdf).selectPages("1,2");
        byte[] merged = merger.mergeToBytes();

        COSDictionary catalog = COSParser.load(merged).getCatalog();
        COSDictionary page1 = PageTree.findPage(catalog, 0);
        COSDictionary page2 = PageTree.findPage(catalog, 1);
        COSDictionary font1 = page1.getDictionary("Resources").getDictionary("Font").getDictionary("F1");
        COSDictionary font2 = page2.getDictionary("Resources").getDictionary("Font").getDictionary("F1");

        System.out.println("Page 1's /F1 key: " + font1.getKey());
        System.out.println("Page 2's /F1 key: " + font2.getKey());
        System.out.println("Same clone: " + font1.getKey().equals(font2.getKey()));
    }
}
