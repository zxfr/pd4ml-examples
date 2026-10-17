package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.xfdf.XfdfDocument;
import com.pd4ml.pdf.xfdf.XfdfExporter;
import com.pd4ml.pdf.xfdf.XfdfImporter;
import com.pd4ml.pdf.xfdf.XfdfReader;

import java.nio.charset.StandardCharsets;

/**
 * 05 -- {@code Options.types(String)}/{@code ids(String)} restrict export or
 * import to a comma-separated subset: an annotation matches by its XFDF
 * element name (e.g. {@code "square"}), a field by its PDF {@code /FT} code
 * (e.g. {@code "tx"}) -- the two vocabularies don't overlap, so one list
 * naturally selects from both categories at once. {@code ids(String)}
 * matches exactly against an annotation's {@code /NM} or a field's
 * fully-qualified name instead. Both work the same way on export and import.
 */
public final class Ex05_TypeAndIdFiltering {

    public static void main(String[] args) throws Exception {
        byte[] pdf = SamplePdf.onePageWithAnnotations(); // a Square "square-1", a Highlight "highlight-1", a Text "note-1"

        byte[] squaresOnly = XfdfExporter.export(pdf, new XfdfExporter.Options().types("square"));
        System.out.println("types(\"square\"): " + countAnnotations(squaresOnly) + " annotation(s) exported.");

        byte[] byId = XfdfExporter.export(pdf, new XfdfExporter.Options().ids("highlight-1"));
        System.out.println("ids(\"highlight-1\"): " + countAnnotations(byId) + " annotation(s) exported.");

        // The same filters apply symmetrically on import: only the matching entries in the
        // XFDF are applied, everything else in the file is simply left alone.
        byte[] twoNewAnnotations = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xfdf xmlns=\"http://ns.adobe.com/xfdf/\"><annots>"
                + "<square name=\"sq-2\" page=\"0\" rect=\"0,0,10,10\"/>"
                + "<circle name=\"ci-2\" page=\"0\" rect=\"0,0,10,10\"/>"
                + "</annots></xfdf>").getBytes(StandardCharsets.UTF_8);
        XfdfImporter.Result result = XfdfImporter.importInto(pdf, twoNewAnnotations,
                new XfdfImporter.Options().types("circle"));
        System.out.println("import types(\"circle\"): " + result.getAnnotationsAdded() + " annotation(s) added "
                + "(the square in the file was skipped).");
    }

    private static int countAnnotations(byte[] xfdfBytes) throws Exception {
        XfdfDocument xfdf = XfdfReader.read(xfdfBytes);
        return xfdf.getAnnotations().size();
    }
}
