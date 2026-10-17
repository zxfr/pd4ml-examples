package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSBase;
import com.pd4ml.pdf.cos.COSDictionary;
import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.COSObjectKey;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;
import com.pd4ml.examples.support.SamplePdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 03 -- Enumerating every indirect object, and walking the page tree by index. */
public final class Ex03_ListingObjectsAndPages {

    public static void main(String[] args) throws Exception {
        COSDocument document = COSParser.load(SamplePdf.nPages(3));

        List<COSObjectKey> keys = new ArrayList<COSObjectKey>(document.getObjectKeys());
        Collections.sort(keys); // getObjectKeys()'s own order isn't guaranteed to be numeric
        for (COSObjectKey key : keys) {
            COSBase value = document.getObject(key);
            System.out.println(key + " -> " + describe(value));
        }

        int pageCount = PageTree.countPages(document.getCatalog());
        System.out.println("Pages: " + pageCount);
        for (int i = 0; i < pageCount; i++) {
            COSDictionary page = PageTree.findPage(document.getCatalog(), i);
            System.out.println("  page " + (i + 1) + ": " + page.getItem("Contents"));
        }
    }

    private static String describe(COSBase value) {
        String text = value.toString(); // COSStream/COSDictionary/etc. already name themselves
        return text.length() > 70 ? text.substring(0, 70) + "..." : text;
    }
}
