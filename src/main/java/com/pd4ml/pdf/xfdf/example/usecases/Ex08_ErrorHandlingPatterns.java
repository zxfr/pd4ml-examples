package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.xfdf.XfdfException;
import com.pd4ml.pdf.xfdf.XfdfExporter;
import com.pd4ml.pdf.xfdf.XfdfImporter;

import java.nio.charset.StandardCharsets;

/** 08 -- Every {@link XfdfException} failure mode worth knowing by name. */
public final class Ex08_ErrorHandlingPatterns {

    public static void main(String[] args) throws Exception {
        try {
            XfdfExporter.export("not a pdf at all".getBytes(StandardCharsets.UTF_8));
        } catch (XfdfException e) {
            System.out.println("unparsable PDF: " + e.getMessage());
        }

        try {
            XfdfImporter.importInto(SamplePdf.onePage(), "<not-xfdf-at-all/>".getBytes(StandardCharsets.UTF_8));
        } catch (XfdfException e) {
            System.out.println("wrong root element: " + e.getMessage());
        }

        try {
            XfdfImporter.importInto(SamplePdf.onePage(), "this is not even xml".getBytes(StandardCharsets.UTF_8));
        } catch (XfdfException e) {
            System.out.println("malformed XML: " + e.getMessage());
        }
    }
}
