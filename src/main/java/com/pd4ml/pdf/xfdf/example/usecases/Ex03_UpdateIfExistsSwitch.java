package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.cos.COSArray;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.xfdf.XfdfImporter;

import java.nio.charset.StandardCharsets;

/**
 * 03 -- {@link XfdfImporter.Options#updateIfExists(boolean)}: an
 * {@code <annots>} entry is matched to an existing PDF annotation by its
 * {@code name} (the PDF {@code /NM}). With the switch on, a match is updated
 * in place; with it off (the default), a same-named entry is simply added
 * as a new, separate annotation instead.
 */
public final class Ex03_UpdateIfExistsSwitch {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.onePageWithAnnotations(); // already has a Square named "square-1"

        // A revised version of that exact same annotation: new position, new color, new text.
        byte[] revisedSquare = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xfdf xmlns=\"http://ns.adobe.com/xfdf/\"><annots>"
                + "<square name=\"square-1\" page=\"0\" rect=\"40,40,160,110\" color=\"#00AA00\">"
                + "<contents>Approved</contents></square>"
                + "</annots></xfdf>").getBytes(StandardCharsets.UTF_8);

        // Switch off (default): the revision is added as a brand-new, separate annotation.
        XfdfImporter.Result added = XfdfImporter.importInto(pdf, revisedSquare);
        System.out.println("updateIfExists=false: " + annotationCount(added.getPdf()) + " annotations on the page "
                + "(" + added.getAnnotationsAdded() + " added, " + added.getAnnotationsUpdated() + " updated).");

        // Switch on: the revision replaces the original "square-1" in place.
        XfdfImporter.Result updated = XfdfImporter.importInto(pdf, revisedSquare,
                new XfdfImporter.Options().updateIfExists(true));
        System.out.println("updateIfExists=true:  " + annotationCount(updated.getPdf()) + " annotations on the page "
                + "(" + updated.getAnnotationsAdded() + " added, " + updated.getAnnotationsUpdated() + " updated).");
    }

    private static int annotationCount(byte[] pdf) throws Exception {
        COSDocument doc = COSParser.load(pdf);
        COSDictionary page = PageTree.findPage(doc.getCatalog(), 0);
        COSArray annots = page.getArray("Annots");
        return annots == null ? 0 : annots.size();
    }
}
