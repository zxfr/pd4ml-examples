package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSBase;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.path.COSPathEvaluator;
import com.pd4ml.pdf.cos.path.COSPathException;
import com.pd4ml.examples.support.SamplePdf;

/**
 * 02 -- The COS-path query language: {@code /Root/...} starts from the
 * catalog, {@code [N]} indexes into an array, indirect references are
 * followed transparently at every step. {@code evaluate} returns
 * {@code null} on a miss; {@code evaluateStrict} throws instead.
 */
public final class Ex02_CosPathQueries {

    public static void main(String[] args) throws Exception {
        COSDocument document = COSParser.load(SamplePdf.nPages(3));

        COSBase mediaBox = COSPathEvaluator.evaluate(document, "/Root/Pages/Kids[1]/MediaBox");
        System.out.println("/Root/Pages/Kids[1]/MediaBox = " + mediaBox);

        COSBase pageCount = COSPathEvaluator.evaluate(document, "/Root/Pages/Count");
        System.out.println("/Root/Pages/Count = " + pageCount);

        COSBase miss = COSPathEvaluator.evaluate(document, "/Root/Pages/Kids[99]/MediaBox");
        System.out.println("out-of-range index (evaluate): " + miss); // null, not an exception

        try {
            COSPathEvaluator.evaluateStrict(document, "/Root/Pages/Kids[99]/MediaBox");
        } catch (COSPathException e) {
            System.out.println("out-of-range index (evaluateStrict): " + e.getMessage());
        }
    }
}
