package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.xfdf.XfdfExporter;
import com.pd4ml.pdf.xfdf.XfdfImporter;

/**
 * 01 -- The minimal round trip: export a PDF's annotations to XFDF, then
 * import that same XFDF into a different (here, blank) PDF. Start here.
 */
public final class Ex01_BasicExportAndImport {

    public static void main(String[] args) throws Exception {
        byte[] annotatedPdf = SamplePdf.onePageWithAnnotations(); // a Square, a Highlight, and a Text note with its own popup
        byte[] xfdf = XfdfExporter.export(annotatedPdf);
        System.out.println("Exported " + annotatedPdf.length + " byte PDF -> " + xfdf.length + " byte XFDF.");

        byte[] blankPdf = SamplePdf.onePage();
        XfdfImporter.Result result = XfdfImporter.importInto(blankPdf, xfdf);

        COSDocument imported = COSParser.load(result.getPdf());
        COSDictionary page = PageTree.findPage(imported.getCatalog(), 0);
        System.out.println("Imported into a blank page: " + result.getAnnotationsAdded() + " annotation(s) added, "
                + page.getArray("Annots").size() + " now on the page.");
    }
}
