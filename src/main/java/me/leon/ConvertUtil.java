package me.leon;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;

import static me.leon.SignPDF.ROOT;

/**
 * html 转 pdf
 */
public class ConvertUtil {
    public static void main(String[] args) throws Exception {
        convert();
    }

    public static void convert() throws Exception {
//        itext7();
//        itext5();

        pdf2Png();
    }

    private static void itext5() throws Exception {
        Document document = new Document(PageSize.A4, 20, 20, 10, 10);
        FileInputStream fileInputStream = new FileInputStream(new File(ROOT + "/22.html"));
        OutputStream os = new FileOutputStream(ROOT + "/htmlsim.pdf");
        PdfWriter mPdfWriter = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(mPdfWriter, document, fileInputStream, Charset.forName("UTF-8"));
        document.close();
        mPdfWriter.close();
    }

    private static void itext7() throws IOException {
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
        // 透明字体
        HtmlConverter.convertToPdf(new File(ROOT + "/22.html"), new File(ROOT + "/htmlsim.pdf"), props);
        HtmlConverter.convertToPdf(new File(ROOT + "/borrow.html"), new File(ROOT + "/htmlsim2.pdf"), props);
    }

    private static void pdf2Png() throws Exception {
        pdf2ImageDemo(ROOT + "/pdf.pdf", ROOT + "/png", 400);
//        pdf2ImageDemo(ROOT + "/demo2.pdf", ROOT + "/png", 200);
    }

    /***
     * PDF文件转PNG/JPEG图片
     * @param PdfFilePath pdf完整路径
     * @param dstImgFolder 图片存放的文件夹
     * @param dpi 越大转换后越清晰，相对转换速度越慢,一般电脑默认96dpi
     */
    public static void pdf2ImageDemo(String PdfFilePath,
                                     String dstImgFolder, int dpi) {
        File file = new File(PdfFilePath);
        PDDocument pdDocument;
        try {
            String imgPDFPath = file.getParent();
            int dot = file.getName().lastIndexOf('.');
            // 获取图片文件名
            String imagePDFName = file.getName().substring(0, dot);
            String imgFolderPath = null;
            if (dstImgFolder.equals("")) {
                imgFolderPath = imgPDFPath + File.separator + imagePDFName;
            } else {
                imgFolderPath = dstImgFolder + File.separator + imagePDFName;
            }

            if (createDirectory(imgFolderPath)) {
                pdDocument = PDDocument.load(file);
                PDFRenderer renderer = new PDFRenderer(pdDocument);
                int pages = pdDocument.getNumberOfPages();// 获取PDF页数
                System.out.println("PDF page number is:" + pages);
                StringBuffer imgFilePath = null;
                for (int i = 0; i < pages; i++) {
                    String imgFilePathPrefix = imgFolderPath
                            + File.separator + imagePDFName;
                    imgFilePath = new StringBuffer();
                    imgFilePath.append(imgFilePathPrefix);
                    imgFilePath.append("_");
                    imgFilePath.append((i + 1));
                    imgFilePath.append(".png");// PNG
                    File dstFile = new File(imgFilePath.toString());
                    BufferedImage image = renderer.renderImageWithDPI(i, dpi);
//                    BufferedImage image = renderer.renderImage(i,2f);
                    ImageIO.write(image, "png", dstFile);// PNG
                }
                System.out.println("PDF文档转PNG图片成功！");
            } else {
                System.out.println("PDF文档转PNG图片失败："
                        + "创建" + imgFolderPath + "失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean createDirectory(String folder) {
        File dir = new File(folder);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }
}
