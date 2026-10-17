package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSArray;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.COSInteger;
import com.pd4ml.pdf.cos.COSObject;
import com.pd4ml.pdf.cos.COSStream;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;

import java.nio.charset.Charset;

/**
 * 07 -- Building a PDF entirely from the COS object model, then serializing
 * it with {@link COSDocumentWriter} -- no template, no other PDF library.
 * This is exactly the pattern {@code com.pd4ml.pdf.merge} and
 * {@code com.pd4ml.pdf.optimizer} use to assemble their output documents.
 */
public final class Ex07_BuildingAndWritingADocumentFromScratch {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        COSDocument doc = new COSDocument();
        doc.setVersion(1.7f);

        COSDictionary font = new COSDictionary();
        font.setName("Type", "Font");
        font.setName("Subtype", "Type1");
        font.setName("BaseFont", "Helvetica");
        COSObject fontRef = doc.createNewIndirectObject(font);

        COSStream content = new COSStream();
        content.setRawData("BT /F1 24 Tf 72 700 Td (Hello, com.pd4ml.pdf.cos) Tj ET".getBytes(LATIN1));
        COSObject contentRef = doc.createNewIndirectObject(content);

        COSDictionary resources = new COSDictionary();
        COSDictionary fontDict = new COSDictionary();
        fontDict.setItem("F1", fontRef);
        resources.setItem("Font", fontDict);

        COSDictionary page = new COSDictionary();
        page.setName("Type", "Page");
        page.setItem("MediaBox", box(0, 0, 612, 792));
        page.setItem("Resources", resources);
        page.setItem("Contents", contentRef);
        COSObject pageRef = doc.createNewIndirectObject(page);

        COSDictionary pages = new COSDictionary();
        pages.setName("Type", "Pages");
        COSArray kids = new COSArray();
        kids.add(pageRef);
        pages.setItem("Kids", kids);
        pages.setInt("Count", 1);
        COSObject pagesRef = doc.createNewIndirectObject(pages);
        page.setItem("Parent", pagesRef);

        COSDictionary catalog = new COSDictionary();
        catalog.setName("Type", "Catalog");
        catalog.setItem("Pages", pagesRef);
        COSObject catalogRef = doc.createNewIndirectObject(catalog);
        doc.getTrailer().setItem("Root", catalogRef);

        byte[] pdfBytes = COSDocumentWriter.write(doc);
        System.out.println("Built and wrote " + pdfBytes.length + " bytes from scratch.");

        COSDocument reparsed = COSParser.load(pdfBytes);
        System.out.println("Round-trip pages: " + PageTree.countPages(reparsed.getCatalog()));
    }

    private static COSArray box(int x0, int y0, int x1, int y1) {
        COSArray b = new COSArray();
        b.add(COSInteger.get(x0));
        b.add(COSInteger.get(y0));
        b.add(COSInteger.get(x1));
        b.add(COSInteger.get(y1));
        return b;
    }
}
