package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.optimizer.PdfOptimizeResult;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

/**
 * 03 -- Two pages sharing one font object still share it after optimizing --
 * reachability-based cloning (via the same {@code COSObjectImporter} used by
 * {@code com.pd4ml.pdf.merge}) visits a shared object once, not once per
 * referencing page.
 */
public final class Ex03_SharedResourceIsNotDuplicated {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.nPages(2); // both pages share one Helvetica font object

        PdfOptimizeResult result = PdfOptimizer.optimize(pdf);
        COSDictionary catalog = COSParser.load(result.getOptimizedPdf()).getCatalog();
        COSDictionary page1 = PageTree.findPage(catalog, 0);
        COSDictionary page2 = PageTree.findPage(catalog, 1);
        COSDictionary font1 = page1.getDictionary("Resources").getDictionary("Font").getDictionary("F1");
        COSDictionary font2 = page2.getDictionary("Resources").getDictionary("Font").getDictionary("F1");

        System.out.println("Same clone after optimizing: " + font1.getKey().equals(font2.getKey()));
    }
}
