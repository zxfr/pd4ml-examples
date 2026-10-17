package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.examples.support.SamplePdf;

/** 01 -- The minimal call: parse a PDF, then walk trailer to Root (the Catalog). Start here. */
public final class Ex01_ParsingAndCatalog {

    public static void main(String[] args) throws Exception {
        byte[] pdfBytes = SamplePdf.nPages(2);

        COSDocument document = COSParser.load(pdfBytes); // empty password
        System.out.println("Version: " + document.getVersion());
        System.out.println("Encrypted: " + document.isEncrypted());
        System.out.println("Objects: " + document.size());

        COSDictionary trailer = document.getTrailer();
        System.out.println("Trailer: " + trailer);

        COSDictionary catalog = document.getCatalog(); // resolves trailer -> /Root
        System.out.println("Catalog /Type: " + catalog.getNameAsString("Type"));
        System.out.println("Catalog: " + catalog);
    }
}
