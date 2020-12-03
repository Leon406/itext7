package me.leon;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;

import java.io.File;
import java.io.IOException;

import static me.leon.SignPDF.ROOT;

public class ConvertUtil {
    public static void main(String[] args) throws IOException {
        convert();
    }

    public static void convert() throws IOException {
        ConverterProperties props = new ConverterProperties();
        FontProvider fp = new FontProvider();
        fp.addStandardPdfFonts();
        //添加系统
//        fp.addSystemFonts();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        fp.addDirectory(classLoader.getResource("fonts").getPath());
        String fonts = classLoader.getResource("fonts").getPath();
        System.out.println(fonts);
//        fp.addFont("C:/Windows/Fonts/simhei.ttf");
        fp.addDirectory(fonts);
//        props.setCharset("utf-8");
        props.setFontProvider(fp);
        for (FontInfo font : fp.getFontSet().getFonts()) {
            System.out.println(font.getFontName());
        }

        // props.setBaseUri(baseResource);
        HtmlConverter.convertToPdf(new File(ROOT + "/docs.html"), new File(ROOT + "/html.pdf"), props);
    }
}
