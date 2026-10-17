package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.xfdf.XfdfAnnotation;
import com.pd4ml.pdf.xfdf.XfdfDocument;
import com.pd4ml.pdf.xfdf.XfdfExporter;
import com.pd4ml.pdf.xfdf.XfdfReader;

/**
 * 02 -- {@link XfdfReader#read(byte[])} turns XFDF bytes into a plain
 * {@link XfdfDocument} you can inspect directly, independent of what PDF
 * (if any) you eventually import it into -- each {@link XfdfAnnotation}
 * exposes both typed accessors (name, page, rect, ...) and every raw
 * attribute for the type-specific ones (color, quad points, ...).
 */
public final class Ex02_InspectingTheParsedModel {

    public static void main(String[] args) throws Exception {
        byte[] annotatedPdf = SamplePdf.onePageWithAnnotations();
        XfdfDocument xfdf = XfdfReader.read(XfdfExporter.export(annotatedPdf));

        for (XfdfAnnotation annot : xfdf.getAnnotations()) {
            System.out.println(annot.getType() + " \"" + annot.getName() + "\" on page " + annot.getPage()
                    + ", color=" + annot.getAttribute("color")
                    + (annot.getContents() != null ? ", contents=\"" + annot.getContents() + "\"" : "")
                    + (annot.getPopup() != null ? " (has its own popup)" : ""));
        }
    }
}
