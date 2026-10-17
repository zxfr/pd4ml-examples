package com.pd4ml.pdf.optimizer.example.usecases;

import com.pd4ml.pdf.cos.COSArray;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.COSInteger;
import com.pd4ml.pdf.cos.COSObject;
import com.pd4ml.pdf.cos.COSStream;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.pdf.optimizer.PdfOptimizeResult;
import com.pd4ml.pdf.optimizer.PdfOptimizer;

import java.nio.charset.Charset;

/**
 * 07 -- Two pages, each with its OWN font object (unlike {@code Ex03}'s
 * identity-shared font) that happen to be byte-identical: by default,
 * {@code optimize(...)} merges them into one shared copy. Also shows
 * turning that off with {@code deduplicateContent(false)}.
 */
public final class Ex07_DuplicateContentDeduplication {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        byte[] pdf = twoPagesWithDuplicateFontObjects();

        PdfOptimizeResult result = PdfOptimizer.optimize(pdf);
        System.out.println("Removed " + result.getRemovedObjectCount() + " object(s), of which "
                + result.getDuplicateObjectCount() + " were merged duplicate content.");

        COSDocument optimized = COSParser.load(result.getOptimizedPdf());
        COSDictionary catalog = optimized.getCatalog();
        COSDictionary font1 = PageTree.findPage(catalog, 0).getDictionary("Resources").getDictionary("Font").getDictionary("F1");
        COSDictionary font2 = PageTree.findPage(catalog, 1).getDictionary("Resources").getDictionary("Font").getDictionary("F1");
        System.out.println("Same object after dedup: " + font1.getKey().equals(font2.getKey()));

        // Turn it off: the two originally-distinct font objects are left distinct.
        PdfOptimizer.Options noDedup = new PdfOptimizer.Options().deduplicateContent(false);
        PdfOptimizeResult withoutDedup = PdfOptimizer.optimize(pdf, noDedup);
        System.out.println("With deduplicateContent(false): " + withoutDedup.getDuplicateObjectCount()
                + " merged (always 0).");
    }

    /** Two pages, each with its OWN font object -- byte-identical, but originally distinct object numbers. */
    private static byte[] twoPagesWithDuplicateFontObjects() {
        COSDocument doc = new COSDocument();
        doc.setVersion(1.7f);

        COSObject font1Ref = doc.createNewIndirectObject(helveticaFont());
        COSObject font2Ref = doc.createNewIndirectObject(helveticaFont());

        COSDictionary pagesDict = new COSDictionary();
        pagesDict.setName("Type", "Pages");
        COSArray kids = new COSArray();
        pagesDict.setItem("Kids", kids);
        COSObject pagesRef = doc.createNewIndirectObject(pagesDict);

        COSDictionary catalog = new COSDictionary();
        catalog.setName("Type", "Catalog");
        catalog.setItem("Pages", pagesRef);
        COSObject catalogRef = doc.createNewIndirectObject(catalog);
        doc.getTrailer().setItem("Root", catalogRef);

        kids.add(pageWithFont(doc, pagesRef, font1Ref, "Page one"));
        kids.add(pageWithFont(doc, pagesRef, font2Ref, "Page two"));
        pagesDict.setInt("Count", 2);

        try {
            return COSDocumentWriter.write(doc);
        } catch (Exception e) {
            throw new IllegalStateException("failed to build the sample PDF", e);
        }
    }

    private static COSDictionary helveticaFont() {
        COSDictionary font = new COSDictionary();
        font.setName("Type", "Font");
        font.setName("Subtype", "Type1");
        font.setName("BaseFont", "Helvetica");
        return font;
    }

    private static COSObject pageWithFont(COSDocument doc, COSObject pagesRef, COSObject fontRef, String text) {
        COSStream contentStream = new COSStream();
        contentStream.setRawData(("BT /F1 12 Tf 72 120 Td (" + text + ") Tj ET").getBytes(LATIN1));
        COSObject contentRef = doc.createNewIndirectObject(contentStream);

        COSArray mediaBox = new COSArray();
        mediaBox.add(COSInteger.get(0));
        mediaBox.add(COSInteger.get(0));
        mediaBox.add(COSInteger.get(200));
        mediaBox.add(COSInteger.get(200));

        COSDictionary fontDict = new COSDictionary();
        fontDict.setItem("F1", fontRef);
        COSDictionary resources = new COSDictionary();
        resources.setItem("Font", fontDict);

        COSDictionary page = new COSDictionary();
        page.setName("Type", "Page");
        page.setItem("Parent", pagesRef);
        page.setItem("MediaBox", mediaBox);
        page.setItem("Resources", resources);
        page.setItem("Contents", contentRef);
        return doc.createNewIndirectObject(page);
    }
}
