package com.pd4ml.pdf.merge.example.usecases;

import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.security.PdfEncryptor;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.pdf.cos.writer.COSDocumentWriter;
import com.pd4ml.examples.support.SamplePdf;
import com.pd4ml.pdf.merge.PdfMerger;

/**
 * 05 -- Merging an encrypted source alongside a plain one: pass its password
 * to {@code addSource}, and the pd4ml COS reader decrypts it transparently
 * while the pages are cloned -- no special-casing needed anywhere else.
 */
public final class Ex05_EncryptedSourceMerging {

    public static void main(String[] args) throws Exception {
        byte[] plain = SamplePdf.nPages(2);

        COSDocument toEncrypt = COSParser.load(SamplePdf.nPages(2));
        PdfEncryptor.encrypt(toEncrypt, new PdfEncryptor.Options().userPassword("open"));
        byte[] encrypted = COSDocumentWriter.write(toEncrypt);

        PdfMerger merger = new PdfMerger();
        merger.addSource(plain).selectPages("1");
        merger.addSource(encrypted, "open").selectPages("1-2");
        byte[] merged = merger.mergeToBytes();

        int pages = PageTree.countPages(COSParser.load(merged).getCatalog());
        System.out.println("Merged plain + encrypted sources: " + pages + " pages; "
                + "output isEncrypted: " + COSParser.load(merged).isEncrypted() + " (not re-encrypted by default).");
    }
}
