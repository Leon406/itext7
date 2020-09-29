package me.leon;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;
import com.itextpdf.layout.element.Image;
import com.itextpdf.signatures.*;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignPDF {
    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String ROOT = "E:\\gitrepo\\itext7\\src\\main\\resources";

    public static final String KEYSTORE = ROOT + "\\yuanzong.jks";//keystore�ļ�·��
    public static final String TEST = ROOT + "\\test.txt";//keystore�ļ�·��
    public static final char[] PASSWORD = "yuanzong2016".toCharArray();    // keystore����

    //    public static final String SRC = ROOT + "\\contract.pdf";
    public static final String SRC = ROOT + "\\111.pdf";

    public static final String DEST = ROOT + "\\contract2.pdf";
    public static final String DEST_TMP = ROOT + "\\tmp.pdf";
    public static final String stamperSrc = ROOT + "\\leon2.png";//ӡ��·��
    public static final String stamperSrc2 = ROOT + "\\signature2.png";//ӡ��·��

    /**
     * @param string
     * @return
     * @Title: unicodeEncode
     * @Description: unicode����
     */
    public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    private Map<String, Rectangle> sigRects = new HashMap<>(8);
    private List<RegexBasedLocationExtractionStrategy> parsers = new ArrayList<>();

    public void sign(String src  //��Ҫǩ�µ�pdf�ļ�·��
            , String dest  // ǩ���µ�pdf�ļ�·��
            , Certificate[] chain //֤��
            , PrivateKey pk //ǩ��˽Կ
            , String digestAlgorithm  //ժҪ�㷨���ƣ�����SHA-1
            , String provider  // ��Կ�㷨�ṩ�ߣ�����Ϊnull
            , CryptoStandard subfilter //����ǩ����ʽ
            , String reason  //ǩ����ԭ����ʾ��pdfǩ�������У������
            , String location) //ǩ���ĵص㣬��ʾ��pdfǩ�����ԣ������
            throws GeneralSecurityException, IOException {
        //�±ߵĲ��趼�ǹ̶��ģ�����д�����ˣ�ûɶҪ���͵�
        PdfReader reader = new PdfReader(src);

        PdfDocument document = new PdfDocument(reader);
        int numberOfPages = document.getNumberOfPages();
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(document);

        for (int i = 0; i < numberOfPages; i++) {
            parsers.add(contentParser.processContent(
                    1 + i,
                    new RegexBasedLocationExtractionStrategy("\\$name\\w")));

        }

        int size = parsers.size();
        Rectangle rectangle = null;
        for (int i = 0; i < size; i++) {

            for (IPdfTextLocation resultantLocation : parsers.get(i).getResultantLocations()) {
                rectangle = resultantLocation.getRectangle();
                System.out.println(rectangle.getX() + " " + rectangle.getY());
                try {

                    System.out.println(Encoding.detectEncoding(resultantLocation.getText()));
                    System.out.println(new String(resultantLocation.getText().getBytes(Charset.forName("GB2312"))));
                } catch (Exception e) {

                }
            }

        }


        PdfReader reader2 = new PdfReader(src);
//        Ŀ���ļ������
        FileOutputStream os = new FileOutputStream(DEST_TMP);
        //����ǩ�¹���PdfSigner �����һ��boolean����
        //false�Ļ���pdf�ļ�ֻ����ǩ��һ�Σ����ǩ�������һ����Ч
        //true�Ļ���pdf���Ա�׷��ǩ������ǩ���߿���ʶ���ÿ��ǩ��֮���ĵ��Ƿ��޸�
        PdfSigner stamper = new PdfSigner(reader2, os, true);

        // ��ȡ����ǩ�����Զ����趨����ǩ�µ�����
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);

        ImageData img = ImageDataFactory.create(stamperSrc);
        //��ȡͼ��ͼƬ�����image��itext����image
        Image image = new Image(img);

        float height = image.getImageHeight();
        float width = image.getImageWidth();
        float scale = getProperScale(height, width, 100f, 40f);


        //����ǩ����λ�ã�ҳ�룬ǩ�������ƣ����׷��ǩ����ʱ��ǩ�������Ʋ���һ��
        //ǩ����λ�ã���ͼ�������pdfҳ���λ�����꣬ԭ��Ϊpdfҳ�����½�
        //�ĸ������ķֱ��ǣ�ͼ�����½�x��ͼ�����½�y��ͼ�¿�ȣ�ͼ�¸߶�   A4��С 595 842
        appearance.setPageNumber(2);
//        appearance.setPageRect(new Rectangle(445, 519, width / scale, height / scale));
        appearance.setPageRect(new Rectangle(rectangle.getX(), rectangle.getY(), width / scale, height / scale));
        //�������ͼƬ
        appearance.setSignatureGraphic(img);
        //����ͼ�µ���ʾ��ʽ������ѡ�����ֻ��ʾͼ�£�����������ģʽ������ͼ�º�ǩ������һͬ��ʾ��
        appearance.setRenderingMode(RenderingMode.GRAPHIC);


        // �����itext�ṩ��2������ǩ���Ľӿڣ������Լ�ʵ�֣��������˵���ʵ��
        // ժҪ�㷨
        IExternalDigest digest = new BouncyCastleDigest();
        // ǩ���㷨
        IExternalSignature signature = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        // ����itextǩ���������pdfǩ��
        stamper.setCertificationLevel(1);
        stamper.signDetached(digest, signature, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);


        reader2 = new PdfReader(DEST_TMP);
        os = new FileOutputStream(dest);
        stamper = new PdfSigner(reader2, os, true);
        // ��ȡ����ǩ�����Զ����趨����ǩ�µ�����
        appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);

        ImageData img2 = ImageDataFactory.create(stamperSrc2);
        //��ȡͼ��ͼƬ�����image��itext����image
        Image image2 = new Image(img2);

        float height2 = image2.getImageHeight();
        float width2 = image2.getImageWidth();
        float scale2 = getProperScale(height2, width2, 100f, 40f);
        appearance.setPageNumber(2);
        appearance.setPageRect(new Rectangle(345, 519, width2 / scale2, height2 / scale2));
        //�������ͼƬ
        appearance.setSignatureGraphic(img);
        //����ͼ�µ���ʾ��ʽ������ѡ�����ֻ��ʾͼ�£�����������ģʽ������ͼ�º�ǩ������һͬ��ʾ��
        appearance.setRenderingMode(RenderingMode.GRAPHIC);
        stamper.setCertificationLevel(1);
        stamper.signDetached(digest, signature, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);


    }

    public float getProperScale(float width, float height, float targetX, float targetY) {

        return Math.max(width / targetX, height / targetY);
    }


    public static void main(String[] args) {
        try {

            // ��ȡkeystore �����˽Կ��֤���� jks
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(KEYSTORE), PASSWORD);
            String alias = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
            Certificate[] chain = ks.getCertificateChain(alias);
            // newһ���ϱ��Զ���ķ������󣬵���ǩ������
            SignPDF app = new SignPDF();
            app.sign(SRC, String.format(DEST, 1), chain, pk, DigestAlgorithms.SHA256, null, CryptoStandard.CADES, "Test 1",
                    "Ghent");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }
}