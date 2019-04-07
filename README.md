pd4ml-examples
==============

> **PD4ML v4** is already released and available for download.  

> The actual **PD4ML v4** release v4.0.2 still has some restrictions (comparing to v3.10): missing proprietary **&lt;pd4ml:layoutbox&gt;** tag support
and generateMulticolumn() API call,
**&lt;pd4ml:page.break&gt;** attributes ignored; some others. The features will be available in the forthcoming releases.

The new product Web-site is currently under construction and going to be available soon.

New in PD4ML v4
---------------

1. **PD4ML** is **Maven**-friendly now. Each v4 release is published to Maven repository (See **pom.xml** for its current location). 
Also nightly builds are available in the 
repository as snapshots. The repository libraries are supplied with Javadoc JARs and optionally with source code JARs (access restricted to **PD4ML SRC** licensees)

2. The main **PD4ML** class package is **com.pd4ml** now. For backward (to some extend) compatibility **PD4ML** JAR includes deprecated 
wrapper **PD4ML** classes in **org.zefer** package. It is recommended to switch/migrate to the new API.

3. **PD4ML v4** is based on a new, totally re-implemented **HTML** rendering engine with a support of more **HTML5/CSS3** features. Unfortunately **HTML**s optimized 
for previous **PD4ML** versions may render with visual differences.

4. **PD4ML v4** now supports **Tagged PDF**, and based on it **PDF/UA** (as well as **PDF/A-2a**) document output. **PDF/UA** support is of interest to persons with disabilities who require 
or benefit from assistive technology when reading electronic content.

5. **PD4ML v4** changes the default HTML-to-PDF scale factor. If **setHtmlWidth()** method is not explicitly invoked, it auto-computes the value to perform
a conversion with **72dpi** scale factor. The value depends on chosen target file format (default is portrait **A4**) and page margins 
(default is **10mm** for **PDF**, **0mm** for images) 

6. The API splits the conversion process to two phases. First you need to invoke **readHTML()** method to read a source document. 
By default an **HTML** (styled with **CSS**) is expected. However it can be arbitrary **XML**, where tag nature is specified with **CSS** "display" 
property, e.g. **&lt;sideblock style="display: block; float: right"&gt;Side content&lt;/sideblock&gt;**.

After the document is read and parsed you can render and write it as **PDF**, **PDF/A-1b**, **PDF/UA** (**PDF/A-2a**), **RTF** or an raster image with **writePDF()** or 
another corresponding method. If you need a conversion result in different formats, there is no need to reread the source **HTML**. 

A simple converter code looks like that:
```java
PD4ML pd4ml = new PD4ML();

String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";
ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());

pd4ml.readHTML(bais);

FileOutputStream fos = new FileOutputStream("hello.pdf");

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

PD4ML v4 Release Notes
-----------------------

https://pd4ml.tech/maven2/com/pd4ml/pd4ml/relnotes.php


How to run the examples
-----------------------

The examples are available as an Eclipse project and supplied with Maven build script. 
It is not mandatory to use Eclipse and Maven, but the combination makes the evaluation process simpler.   

1. Check PD4ML examples project out. You may use the GitHub web interface to download 
the project or to clone it with the command line ```git clone https://github.com/zxfr/pd4ml-examples.git```

2. Import the project to Eclipse as Maven project with *File->Import...->Maven->Existing Maven project* 

3. Right mouse click on the 'pd4ml-examples' project and choose *Run As->Maven build*. Type ```clean install``` 
as build goals. The build script picks the most recent PD4ML library version from the repository.

From this point you can run each example class (like E001GettingStarted.java) individually by a right-clicking 
on it in Eclipse navigation pane and choosing *Run as->Java Application*

Command ```mvn clean test``` runs all examples automatically as a test batch.

If you prefer to run the examples without Maven, download the newest 
pd4ml-4.0.3-*.*-*.jar from https://pd4ml.tech/maven2/com/pd4ml/pd4ml/4.0.3-SNAPSHOT/ or from https://pd4ml.tech/v4/ and add it to the projects classpath.

> ```pd4ml.tech``` domain will change to ```pd4ml.com``` after the new PD4ML web site launch in the nearest future

Troubleshooting
---------------

Maven build failed with
```sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException```

The exception tells, that your JDK does not include the most actual list of well-known certificate authorities. 
It seems the most straightforward solution is to copy ```jre/lib/security/cacerts``` file from any JDK 1.9+ to the identical location of your actual JRE. 

For example: 
```C:\Program Files\Java\jdk-10.0.2\lib\security\cacerts``` from Windows to ```/Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/jre/lib/security``` on macOS 

 


