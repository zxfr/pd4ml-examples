package com.pd4ml.pdf.xfdf.example.usecases;

import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.cos.COSArray;
import com.pd4ml.pdf.cos.COSBase;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.xfdf.XfdfExporter;
import com.pd4ml.pdf.xfdf.XfdfImporter;

import java.nio.charset.StandardCharsets;

/**
 * 04 -- Form field values travel through {@code <fields>}, not
 * {@code <annots>}: {@code <fields>} only ever <em>updates</em> a field
 * that already exists in the target PDF's {@code /AcroForm} (XFDF carries
 * no widget geometry, so there's nothing to build a brand-new field from).
 * A checkbox's widget {@code /AS} is kept in sync with its new value
 * automatically.
 */
public final class Ex04_FormFieldValues {

    public static void main(String[] args) throws Exception {
        byte[] form = SamplePdf.onePageWithForm(); // text field "name"="Alice", checkbox "agree"=Off

        byte[] currentValues = XfdfExporter.export(form);
        System.out.println("Exported: " + new String(currentValues, StandardCharsets.UTF_8)
                .replaceAll("[\\r\\n\\t]+", " ").trim());

        byte[] answers = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xfdf xmlns=\"http://ns.adobe.com/xfdf/\"><fields>"
                + "<field name=\"name\"><value>Bob</value></field>"
                + "<field name=\"agree\"><value>Yes</value></field>"
                + "</fields></xfdf>").getBytes(StandardCharsets.UTF_8);

        XfdfImporter.Result result = XfdfImporter.importInto(form, answers);
        System.out.println(result.getFieldsUpdated() + " field(s) updated, "
                + result.getFieldsNotFound().size() + " not found.");

        COSDocument updated = COSParser.load(result.getPdf());
        COSArray fields = updated.getCatalog().getDictionary("AcroForm").getArray("Fields");
        for (COSBase ref : fields) {
            COSDictionary field = (COSDictionary) ref.resolve();
            System.out.println("  " + field.getString("T") + ": V=" + field.getDictionaryObject("V")
                    + (field.containsKey("AS") ? ", AS=" + field.getDictionaryObject("AS") : ""));
        }
    }
}
