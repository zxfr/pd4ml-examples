pd4ml-examples
==============

New in PD4ML v4
---------------

1. **PD4ML** is **Maven**-centric now. Each v4 release is published to Maven repository. Also nightly builds are available in the 
repository as snapshots. The repository libraries are supplied with Javadoc JARs and optionally with source code JARs (access restricted to **PD4ML SRC** licensees)

2. The main **PD4ML** class package is **com.pd4ml** now. For backward (to some extend) compatibility **PD4ML** JAR includes deprecated 
wrapper **PD4ML** classes in **org.zefer** package. It is recommended to switch/migrate to the new API.

3. **PD4ML v4** is based on a new, totally re-implemented **HTML** rendering engine with a support of more **HTML5/CSS3** features. Unfortunately **HTML**s optimized 
for previous **PD4ML** versions may render with flaws.

4. **PD4ML v4** changes the default HTML-to-PDF scale factor. If **setHtmlWidth()** method is not explicitly invoked, it auto-computes the value to perform
a conversion with **72dpi** scale factor. The value depends on chosen target file format (default is portrait **A4**) and page margins 
(default is **10mm** for **PDF**, **0mm** for images) 

5. The API splits the conversion process to two phases. First you need to invoke **readHTML()** method to read a source document. 
By default an **HTML** (styled with **CSS**) is expected. However it can be arbitrary **XML**, where tag nature is specified with **CSS** "display" 
property, e.g. **&lt;sideblock style="display: block; float: right"&gt;Side content&lt;/sideblock&gt;**.

After the document is read and parsed you can render and write it as **PDF**, **PDF/A**, **RTF** or an raster image with **writePDF()** or 
another corresponding method. If you need a conversion result in different formats, there is no need to reread the source **HTML**. 

A simple converter code looks like that:
```java
PD4ML pd4ml = new PD4ML();

String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";
ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());

pd4ml.readHTML(bais);

FileOutputStream fos = new FileOutputStream("result.pdf");

pd4ml.writePDF(fos);
```
 
6. As before, **PD4ML** does not rely on third-party components. It even implements its own basic **SVG** rendering module. However when 
a full-featured **SVG** or **MathML** support is needed, **PD4ML** provides a way to plug **Batik**, **JEuclid** or another library and to associate 
it with a particular or custom tag (**&lt;svg&gt;**, **&lt;math&gt;**, **&lt;etc&gt;**)  

7. **PDF**-specific features like watermarks, page headers/footers etc are implemented with proprietary tags now (i.e. **&lt;pd4ml:watermark&gt;**).
Now you can either define let's say a watermark either in source **HTML**, or with a corresponding **PD4ML API** call or as 
an **HTML** code snippet, added to source **HTML** with **injectHTML()** API call.

8. **injectHTML()** API call allows to include an arbitrary HTML portion either after opening **&lt;body&gt;** tag or just before closing 
**&lt;/body&gt;** tag.

9. **CSS** nested **At-rules** support added for **@page** and **@font-face**
```css
@page :first {
  margin: 10pt;
  size: A4 landscape;
}
@font-face {
  font-family: "Consolas";
  src: url("java:/html/rc/FiraMono-Regular.ttf") format("ttf");
}
@font-face { 
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 400;
  src: url(https://fonts.gstatic.com/s/lato/v11/qIIYRU-oROkIk8vfvxw6QvesZW2xOQ-xsNqO47m55DA.woff) format('woff');
}

```



