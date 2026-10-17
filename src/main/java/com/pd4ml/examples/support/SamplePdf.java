package com.pd4ml.examples.support;

import com.pd4ml.pdf.cos.COSArray;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.COSException;
import com.pd4ml.pdf.cos.COSFloat;
import com.pd4ml.pdf.cos.COSInteger;
import com.pd4ml.pdf.cos.COSObject;
import com.pd4ml.pdf.cos.COSStream;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.pdf.cos.writer.IncrementalUpdateWriter;
import com.pd4ml.pdf.cos.parser.COSParser;

import java.nio.charset.Charset;

/**
 * Builds small, well-formed PDFs entirely with {@code com.pd4ml.pdf.cos} (object
 * model + {@link COSDocumentWriter}) -- used by the runnable examples under
 * {@code com.pd4ml.*.example.usecases} so none of them needs an external
 * sample file to demonstrate real, end-to-end behavior. Not part of the
 * public API; every example class here builds its own throwaway input, the
 * same way a real caller would build (or already have) a PDF of their own.
 */
public final class SamplePdf {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");

    private SamplePdf() {
    }

    /** A single page reading "Page 1". */
    public static byte[] onePage() {
        return nPages(1);
    }

    /**
     * An {@code n}-page PDF, each page's content stream reading
     * {@code "Page <i>"} (1-based), all pages sharing one Helvetica font
     * object -- useful for anything page-range- or shared-resource-related.
     */
    public static byte[] nPages(int n) {
        COSDocument doc = new COSDocument();
        doc.setVersion(1.7f);

        COSDictionary font = new COSDictionary();
        font.setName("Type", "Font");
        font.setName("Subtype", "Type1");
        font.setName("BaseFont", "Helvetica");
        COSObject fontRef = doc.createNewIndirectObject(font);

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

        for (int i = 1; i <= n; i++) {
            kids.add(addPage(doc, pagesRef, fontRef, "Page " + i));
        }
        pagesDict.setInt("Count", n);

        return write(doc);
    }

    /**
     * A one-page PDF whose page content is tagged: a minimal
     * {@code /StructTreeRoot} with one top-level {@code <P>} structure
     * element wrapping the page's single marked-content span (MCID 0) --
     * enough to exercise structure-tree reconciliation without needing a
     * real authoring tool.
     */
    public static byte[] onePageTagged() {
        COSDocument doc = new COSDocument();
        doc.setVersion(1.7f);

        COSDictionary font = new COSDictionary();
        font.setName("Type", "Font");
        font.setName("Subtype", "Type1");
        font.setName("BaseFont", "Helvetica");
        COSObject fontRef = doc.createNewIndirectObject(font);

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

        String content = "/P <</MCID 0>> BDC BT /F1 12 Tf 72 120 Td (Tagged page) Tj ET EMC";
        COSStream contentStream = new COSStream();
        contentStream.setRawData(content.getBytes(LATIN1));
        COSObject contentRef = doc.createNewIndirectObject(contentStream);

        COSDictionary page = new COSDictionary();
        page.setName("Type", "Page");
        page.setItem("Parent", pagesRef);
        page.setItem("MediaBox", box(0, 0, 200, 200));
        COSDictionary resources = new COSDictionary();
        COSDictionary fontDict = new COSDictionary();
        fontDict.setItem("F1", fontRef);
        resources.setItem("Font", fontDict);
        page.setItem("Resources", resources);
        page.setItem("Contents", contentRef);
        page.setInt("StructParents", 0);
        COSObject pageRef = doc.createNewIndirectObject(page);
        kids.add(pageRef);
        pagesDict.setInt("Count", 1);

        // Structure element for the one MCID-0 span, referencing the page directly (§14.7.5.3).
        COSDictionary structElem = new COSDictionary();
        structElem.setName("Type", "StructElem");
        structElem.setName("S", "P");
        structElem.setItem("Pg", pageRef);
        structElem.setInt("K", 0); // MCID, page implied by /Pg
        COSObject structElemRef = doc.createNewIndirectObject(structElem);

        COSDictionary structTreeRoot = new COSDictionary();
        structTreeRoot.setName("Type", "StructTreeRoot");
        COSArray kArray = new COSArray();
        kArray.add(structElemRef);
        structTreeRoot.setItem("K", kArray);

        COSDictionary parentTree = new COSDictionary();
        COSArray ptNums = new COSArray();
        ptNums.add(COSInteger.get(0));
        COSArray ptEntry0 = new COSArray();
        ptEntry0.add(structElemRef);
        ptNums.add(ptEntry0);
        parentTree.setItem("Nums", ptNums);
        structTreeRoot.setItem("ParentTree", parentTree);
        COSObject structTreeRootRef = doc.createNewIndirectObject(structTreeRoot);
        structElem.setItem("Parent", structTreeRootRef);

        catalog.setItem("StructTreeRoot", structTreeRootRef);
        COSDictionary markInfo = new COSDictionary();
        markInfo.setBoolean("Marked", true);
        catalog.setItem("MarkInfo", markInfo);

        return write(doc);
    }

    /**
     * A one-page PDF carrying three real annotations for {@code com.pd4ml.pdf.xfdf}
     * examples to work with: a red Square (<code>/NM "square-1"</code>, {@code /Contents
     * "Reviewed"}), a yellow Highlight (<code>/NM "highlight-1"</code>), and a Text
     * sticky note (<code>/NM "note-1"</code>) with its own nested Popup
     * (<code>/NM "popup-1"</code>).
     */
    public static byte[] onePageWithAnnotations() {
        COSDocument doc = new COSDocument();
        doc.setVersion(1.7f);

        COSObject pageRef = onePlainPage(doc, "Annotated page");

        COSDictionary square = new COSDictionary();
        square.setName("Type", "Annot");
        square.setName("Subtype", "Square");
        square.setItem("Rect", box(20, 20, 120, 70));
        square.setString("NM", "square-1");
        square.setString("Contents", "Reviewed");
        square.setItem("C", colorRgb(1, 0, 0));
        COSObject squareRef = doc.createNewIndirectObject(square);

        COSDictionary highlight = new COSDictionary();
        highlight.setName("Type", "Annot");
        highlight.setName("Subtype", "Highlight");
        highlight.setItem("Rect", box(20, 100, 170, 120));
        highlight.setString("NM", "highlight-1");
        highlight.setItem("QuadPoints", quadPoints(20, 120, 170, 120, 20, 100, 170, 100));
        highlight.setItem("C", colorRgb(1, 1, 0));
        COSObject highlightRef = doc.createNewIndirectObject(highlight);

        COSDictionary popup = new COSDictionary();
        popup.setName("Type", "Annot");
        popup.setName("Subtype", "Popup");
        popup.setItem("Rect", box(180, 20, 380, 100));
        popup.setString("NM", "popup-1");
        popup.setBoolean("Open", false);
        COSObject popupRef = doc.createNewIndirectObject(popup);

        COSDictionary note = new COSDictionary();
        note.setName("Type", "Annot");
        note.setName("Subtype", "Text");
        note.setItem("Rect", box(140, 140, 160, 160));
        note.setString("NM", "note-1");
        note.setString("Contents", "Looks good");
        note.setName("Name", "Comment");
        note.setItem("Popup", popupRef);
        COSObject noteRef = doc.createNewIndirectObject(note);
        popup.setItem("Parent", noteRef);

        COSArray annots = new COSArray();
        annots.add(squareRef);
        annots.add(highlightRef);
        annots.add(noteRef);
        annots.add(popupRef);
        ((COSDictionary) pageRef.resolve()).setItem("Annots", annots);

        return write(doc);
    }

    /**
     * A one-page PDF with an {@code /AcroForm}: a text field named {@code "name"}
     * (initial value {@code "Alice"}) and a checkbox field named {@code "agree"}
     * (initially {@code /Off}, with real {@code /AP/N} appearance states for both
     * {@code Yes} and {@code Off} so a value change is actually visually meaningful).
     */
    public static byte[] onePageWithForm() {
        COSDocument doc = new COSDocument();
        doc.setVersion(1.7f);

        COSObject pageRef = onePlainPage(doc, "Form page");
        COSDictionary page = (COSDictionary) pageRef.resolve();

        COSDictionary nameField = new COSDictionary();
        nameField.setName("Type", "Annot");
        nameField.setName("Subtype", "Widget");
        nameField.setName("FT", "Tx");
        nameField.setString("T", "name");
        nameField.setString("V", "Alice");
        nameField.setItem("Rect", box(20, 20, 160, 40));
        nameField.setItem("P", pageRef);
        COSObject nameFieldRef = doc.createNewIndirectObject(nameField);

        COSObject onAppearanceRef = doc.createNewIndirectObject(checkboxAppearance());
        COSObject offAppearanceRef = doc.createNewIndirectObject(checkboxAppearance());

        COSDictionary appearanceStates = new COSDictionary();
        appearanceStates.setItem("Yes", onAppearanceRef);
        appearanceStates.setItem("Off", offAppearanceRef);
        COSDictionary appearance = new COSDictionary();
        appearance.setItem("N", appearanceStates);

        COSDictionary agreeField = new COSDictionary();
        agreeField.setName("Type", "Annot");
        agreeField.setName("Subtype", "Widget");
        agreeField.setName("FT", "Btn");
        agreeField.setString("T", "agree");
        agreeField.setName("V", "Off");
        agreeField.setName("AS", "Off");
        agreeField.setItem("Rect", box(20, 50, 40, 70));
        agreeField.setItem("P", pageRef);
        agreeField.setItem("AP", appearance);
        COSObject agreeFieldRef = doc.createNewIndirectObject(agreeField);

        COSArray annots = new COSArray();
        annots.add(nameFieldRef);
        annots.add(agreeFieldRef);
        page.setItem("Annots", annots);

        COSDictionary acroForm = new COSDictionary();
        COSArray fields = new COSArray();
        fields.add(nameFieldRef);
        fields.add(agreeFieldRef);
        acroForm.setItem("Fields", fields);
        doc.getCatalog().setItem("AcroForm", acroForm);

        return write(doc);
    }

    /** A bare one-page (200x200) PDF with a single Helvetica-labeled content stream, no annotations/form -- shared setup for the fixtures above. */
    private static COSObject onePlainPage(COSDocument doc, String text) {
        COSDictionary font = new COSDictionary();
        font.setName("Type", "Font");
        font.setName("Subtype", "Type1");
        font.setName("BaseFont", "Helvetica");
        COSObject fontRef = doc.createNewIndirectObject(font);

        COSDictionary pagesDict = new COSDictionary();
        pagesDict.setName("Type", "Pages");
        COSArray kids = new COSArray();
        pagesDict.setItem("Kids", kids);
        pagesDict.setInt("Count", 1);
        COSObject pagesRef = doc.createNewIndirectObject(pagesDict);

        COSDictionary catalog = new COSDictionary();
        catalog.setName("Type", "Catalog");
        catalog.setItem("Pages", pagesRef);
        COSObject catalogRef = doc.createNewIndirectObject(catalog);
        doc.getTrailer().setItem("Root", catalogRef);

        COSObject pageRef = addPage(doc, pagesRef, fontRef, text);
        kids.add(pageRef);
        return pageRef;
    }

    /** A minimal, valid (empty-content) Form XObject usable as a checkbox's {@code /AP/N} appearance state. */
    private static COSStream checkboxAppearance() {
        COSStream appearance = new COSStream();
        appearance.setName("Type", "XObject");
        appearance.setName("Subtype", "Form");
        appearance.setItem("BBox", box(0, 0, 20, 20));
        appearance.setRawData(new byte[0]);
        return appearance;
    }

    private static COSArray colorRgb(float r, float g, float b) {
        COSArray color = new COSArray();
        color.add(new COSFloat(r));
        color.add(new COSFloat(g));
        color.add(new COSFloat(b));
        return color;
    }

    private static COSArray quadPoints(float... values) {
        COSArray array = new COSArray();
        for (float v : values) {
            array.add(new COSFloat(v));
        }
        return array;
    }

    /**
     * Parses {@code basePdf}, then applies a real incremental update that
     * drops its second page from {@code /Kids} without deleting the
     * now-orphaned page/content objects -- the exact situation
     * {@code com.pd4ml.pdf.optimizer} is built to clean up.
     */
    public static byte[] withOrphanedSecondPage(byte[] basePdf) {
        try {
            COSDocument doc = COSParser.load(basePdf);
            COSDictionary pages = doc.getCatalog().getDictionary("Pages");
            COSArray kids = pages.getArray("Kids");
            kids.remove(1);
            pages.setInt("Count", kids.size());

            IncrementalUpdateWriter update = new IncrementalUpdateWriter(basePdf, doc);
            update.writeObject(pages);
            return update.finish();
        } catch (COSException e) {
            throw new IllegalStateException("failed to build a sample incremental update", e);
        }
    }

    private static COSObject addPage(COSDocument doc, COSObject pagesRef, COSObject fontRef, String text) {
        byte[] content = ("BT /F1 12 Tf 72 120 Td (" + text + ") Tj ET").getBytes(LATIN1);
        COSStream contentStream = new COSStream();
        contentStream.setRawData(content);
        COSObject contentRef = doc.createNewIndirectObject(contentStream);

        COSDictionary page = new COSDictionary();
        page.setName("Type", "Page");
        page.setItem("Parent", pagesRef);
        page.setItem("MediaBox", box(0, 0, 200, 200));
        COSDictionary resources = new COSDictionary();
        COSDictionary fontDict = new COSDictionary();
        fontDict.setItem("F1", fontRef);
        resources.setItem("Font", fontDict);
        page.setItem("Resources", resources);
        page.setItem("Contents", contentRef);
        return doc.createNewIndirectObject(page);
    }

    private static COSArray box(int x0, int y0, int x1, int y1) {
        COSArray box = new COSArray();
        box.add(COSInteger.get(x0));
        box.add(COSInteger.get(y0));
        box.add(COSInteger.get(x1));
        box.add(COSInteger.get(y1));
        return box;
    }

    private static byte[] write(COSDocument doc) {
        try {
            return COSDocumentWriter.write(doc);
        } catch (COSException e) {
            throw new IllegalStateException("failed to write a sample PDF", e);
        }
    }
}
