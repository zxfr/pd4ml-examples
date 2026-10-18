pd4ml-examples
==============

Ready-to-run Java examples for **PD4ML v4**, the HTML-to-PDF converter for Java.

Documentation
-------------

The documentation source is **https://pd4ml.com/docs/** (PD4ML Programmer's Manual, API and tag reference).

* Release notes: https://pd4ml.com/release-notes/
* Product site and downloads: https://pd4ml.com/
* Maven repository: https://pd4ml.com/maven2/ (group `com.pd4ml`, artifact `pd4ml`)

Requirements
------------

* Java 8 or newer
* PD4ML v4 library. The `pom.xml` takes the newest available version from the PD4ML Maven repository
  (version range `[4.0.22,)`, snapshots enabled). Currently published:
  * v4.0.x line: releases up to **4.0.23fx2**, snapshot **4.0.24-SNAPSHOT**
  * v4.1.x line: release **4.1.1**, snapshot **4.1.2-SNAPSHOT**
* The examples in the `standards` package and the PDF tools examples in `com.pd4ml.pdf.*` use APIs
  available in the **v4.1.x** line only.

Without a license PD4ML runs in evaluation mode. A license file `pd4ml.lic` is looked up in the current working
directory and then in the classpath; see https://pd4ml.com/docs/ for licensing details.

Examples
--------

All example sources are in `src/main/java`. Each HTML conversion example has a `main()` method and, next to it,
the PDF it produces.

| Package | Examples |
|---|---|
| `basics` | E001 getting started, E002 page format, E003/E004 page headers and footers (API and inline tags), E005/E006 page background, E007/E008 watermarks, E009 password protection and document permissions, E010 `injectHtml()` |
| `html` | H001 HTML file conversion with a custom TTF font, H002 table of contents, H003 bookmarks, H004 page breaks, H005 file attachments, H006 footnotes, H007 page number info, H008/H009 multicolumn layout (two-column newsletter, three-column landscape glossary) |
| `i18n` | N001/N002 TTF fonts for international text |
| `advanced` | A001 print dialog on open, A002 silent print, A003 HTML from the classpath, A004 progress listener, A005 custom resource loaders (incl. image resampling and JPEG conversion), A006 placeholder substitution, A007 rendering status info, A008 custom tag renderer |
| `pdftools` | P001 convert and append pages of an existing PDF, P002 merge two PDFs, P003 merge and protect with a password, P004 update PDF meta info, P005 underlay/overlay |
| `standards` | Z001 ZUGFeRD / Factur-X invoice (PDF/A-3 with embedded invoice XML) |
| `com.pd4ml.pdf.cos.example.usecases` | low-level PDF object model (COS): parsing, path queries, listing objects and pages, incremental updates, encrypted PDFs, stream filters, building a PDF from scratch, error recovery |
| `com.pd4ml.pdf.merge.example.usecases` | `PdfMerger`: merging, page range grammar, filtering and reordering, shared resource deduplication, encrypted sources, encrypted output, tagged PDF reconciliation, error handling |
| `com.pd4ml.pdf.optimizer.example.usecases` | `PdfOptimizer`: cleanup, deduplication of shared and duplicate content, encrypted input and output, error handling |
| `com.pd4ml.pdf.sign.example` | `PdfSigner`: invisible and visible signatures (text, image), digest algorithms, RFC 3161 timestamps, DocMDP certification, co-signing, PKCS#11 HSM and cloud KMS signing, LTV, signature field inspection |
| `com.pd4ml.pdf.xfdf.example.usecases` | XFDF export and import of annotations and form field values, filtering, encrypted sources, post-import optimization |

`com.pd4ml.examples.support` contains helpers shared by the PDF tools examples (sample PDF generation,
a self-signed test PKI for the signing examples).

A minimal conversion:

```java
PD4ML pd4ml = new PD4ML();
pd4ml.readHTML(new ByteArrayInputStream("<b>Hello, World!</b>".getBytes(StandardCharsets.UTF_8)));
try (FileOutputStream fos = new FileOutputStream("hello.pdf")) {
    pd4ml.writePDF(fos);
}
```

The main API class is `com.pd4ml.PD4ML`. The `org.zefer.pd4ml` classes are deprecated wrappers for code written
for PD4ML v3.

How to run the examples
-----------------------

1. Clone the project: `git clone https://github.com/zxfr/pd4ml-examples.git`
2. Run all examples with Maven: `mvn clean test`. The test batch writes the generated PDFs to
   `${java.io.tmpdir}/pd4ml-examples/`.
3. Or import the project into Eclipse (*File -> Import... -> Maven -> Existing Maven Projects*) or another IDE and
   run any example class as a Java application. Run the examples with the project root as the working directory:
   some of them read their input files by relative paths.

An example opens the generated PDF in the default PDF viewer unless it is started with an argument
(e.g. `noviewer`). `RunAllExamples` runs all packages listed in it and copies the results next to the example
sources (or to the directory given as its first argument).

Without Maven, download the PD4ML JAR from https://pd4ml.com/maven2/com/pd4ml/pd4ml/ and add it to the classpath.
The `standards` example also needs the Mustang validator (`org.mustangproject:validator`, see `pom.xml`).

Troubleshooting
---------------

Maven build fails with
`sun.security.validator.ValidatorException: PKIX path building failed`

The JDK does not trust the certificate authority of the repository server. Update the JDK (or its
`lib/security/cacerts` file) to a recent version.
