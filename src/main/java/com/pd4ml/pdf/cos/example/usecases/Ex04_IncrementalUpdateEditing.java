package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.cos.writer.IncrementalUpdateWriter;
import com.pd4ml.examples.support.SamplePdf;

/**
 * 04 -- Editing an existing PDF as a real incremental update: every original
 * byte is preserved unchanged, and only the changed/new objects plus a small
 * xref+trailer are appended -- the same mechanism {@code com.pd4ml.pdf.sign} and
 * {@code com.pd4ml.pdf.sign.ltv} build on for signatures and LTV. This example
 * adds a new key to page 1's dictionary and rewrites it.
 */
public final class Ex04_IncrementalUpdateEditing {

    public static void main(String[] args) throws Exception {
        byte[] original = SamplePdf.nPages(2);
        COSDocument document = COSParser.load(original);

        COSDictionary page1 = PageTree.findPage(document.getCatalog(), 0);

        // A brand-new indirect object, wired into the existing graph.
        COSDictionary metadata = new COSDictionary();
        metadata.setString("Note", "Annotated by Ex04");

        IncrementalUpdateWriter update = new IncrementalUpdateWriter(original, document);
        update.assignNewKey(metadata);          // gives it a fresh indirect identity
        page1.setItem("PD4MLNote", metadata);   // wire it into the existing page dictionary
        update.writeObject(page1);              // page1 changed: must be rewritten
        update.writeObject(metadata);           // metadata is new: must be written

        byte[] updated = update.finish();
        System.out.println("Original: " + original.length + " bytes; after incremental update: " + updated.length + " bytes.");

        // Re-parsing confirms the edit round-trips, and that the update really was
        // incremental (both revisions' bytes are still physically present).
        COSDocument reparsed = COSParser.load(updated);
        COSDictionary reparsedPage1 = PageTree.findPage(reparsed.getCatalog(), 0);
        COSDictionary reparsedNote = reparsedPage1.getDictionary("PD4MLNote");
        System.out.println("Round-tripped note: " + reparsedNote.getString("Note"));
        System.out.println("Contains /Prev (a real incremental update): "
                + new String(updated, "ISO-8859-1").contains("/Prev"));
    }
}
