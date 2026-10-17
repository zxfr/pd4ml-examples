package com.pd4ml.pdf.sign.example.usecases;

import com.pd4ml.pdf.cos.COSBase;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.path.COSPathEvaluator;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;

/** 17 -- Using com.pd4ml.pdf.cos alone (no signing) to parse and COS-path-query a PDF. */
public final class Ex17_CosInspectionAndPathQuery {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.nPages(3);
        COSDocument document = COSParser.load(pdf);

        System.out.println("Pages: " + PageTree.countPages(document.getCatalog()));
        System.out.println("Objects: " + document.size());

        COSBase mediaBox = COSPathEvaluator.evaluate(document, "/Root/Pages/Kids[0]/MediaBox");
        System.out.println("/Root/Pages/Kids[0]/MediaBox = " + mediaBox);

        COSDictionary page2 = PageTree.findPage(document.getCatalog(), 1);
        System.out.println("Page 2 dictionary: " + page2);
    }
}
