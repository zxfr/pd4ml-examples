package com.pd4ml.pdf.cos.example.usecases;

import com.pd4ml.pdf.cos.COSDocument;
import com.pd4ml.pdf.cos.parser.COSParseException;
import com.pd4ml.pdf.cos.parser.COSParser;
import com.pd4ml.pdf.cos.util.PageTree;

import java.nio.charset.Charset;

/**
 * 08 -- {@link COSParseException} for genuinely unparseable input, versus
 * the brute-force {@code "n g obj"} recovery scan {@link COSParser} falls
 * back to for a corrupt-but-recoverable one (missing/broken xref, the same
 * situation essentially every real-world PDF reader has to cope with).
 */
public final class Ex08_ErrorHandlingAndRecovery {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        try {
            COSParser.load("this is not a PDF at all".getBytes(LATIN1));
        } catch (COSParseException e) {
            System.out.println("Not a PDF: " + e.getMessage());
        }

        // Well-formed objects, but no xref table, no trailer, no startxref -- forces the
        // brute-force recovery scan rather than failing outright.
        byte[] corrupt = corruptPdfNeedingRecovery();
        COSDocument recovered = COSParser.load(corrupt);
        System.out.println("Recovered without any xref/trailer: pages=" + PageTree.countPages(recovered.getCatalog()));
    }

    private static byte[] corruptPdfNeedingRecovery() {
        StringBuilder sb = new StringBuilder();
        sb.append("%PDF-1.4\n");
        sb.append("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
        sb.append("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
        sb.append("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 100 100] /Contents 4 0 R >>\nendobj\n");
        String content = "BT ET";
        sb.append("4 0 obj\n<< /Length ").append(content.length()).append(" >>\nstream\n")
                .append(content).append("\nendstream\nendobj\n");
        sb.append("%%EOF\n");
        return sb.toString().getBytes(LATIN1);
    }
}
