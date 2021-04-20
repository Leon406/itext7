package me.leon;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;
import com.itextpdf.signatures.*;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class SignPDF {
    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String ROOT = "E:\\gitrepo\\itext7\\src\\main\\resources";
//    public static final String ROOT = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    public static final String TEST = ROOT + "\\test.txt";//keystore文件路径
    //    public static final String KEYSTORE = ROOT + "\\yuanzong.jks";//keystore文件路径
//    public static final char[] PASSWORD = "yuanzong2016".toCharArray();    // keystore密码
    public static final String KEYSTORE = ROOT + "\\leon.jks";//keystore文件路径
    public static final String KEYSTORE2 = ROOT + "\\leon2.jks";//keystore文件路径
    public static final char[] PASSWORD = "123456".toCharArray();    // keystore密码
    public static final char[] PASSWORD2 = "123456".toCharArray();    // keystore密码

    //    public static final String SRC = ROOT + "\\contract22.pdf";
    public static final String SRC = ROOT + "\\111.pdf";

    public static final String DEST = ROOT + "\\contract2.pdf";
    public static final String DEST_TMP = ROOT + "\\tmp.pdf";
    public static final String TMP = ROOT + "\\tmp";
    public static final String stamperSrc = ROOT + "\\leon2.png";//印章路径
    public static final String stamperSrc2 = ROOT + "\\signature2.png";//印章路径

    private List<RegexBasedLocationExtractionStrategy> parsers = new ArrayList<>();

    public void sign(String src  //需要签章的pdf文件路径
            , String dest  // 签完章的pdf文件路径
            , Certificate[] chain //证书
            , PrivateKey pk //签名私钥
            , String digestAlgorithm  //摘要算法名称，例如SHA-1
            , String provider  // 密钥算法提供者，可以为null
            , CryptoStandard subfilter //数字签名格式
            , String reason  //签名的原因，显示在pdf签名属性中，随便填
            , String location) //签名的地点，显示在pdf签名属性，随便填
            throws Exception {
        //下边的步骤都是固定的，照着写就行了，没啥要解释的
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
            }
        }


        reader = new PdfReader(src);
//        目标文件输出流
        FileOutputStream os = new FileOutputStream(DEST_TMP);
        //创建签章工具PdfSigner ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        PdfSigner stamper = new PdfSigner(reader, os, new StampingProperties().useAppendMode());

        SignHelper.appearance(stamper, stamperSrc, 2, rectangle, reason, location);


        IExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
        IExternalSignature signature = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        // 调用itext签名方法完成pdf签章
        stamper.setCertificationLevel(1);
        stamper.signDetached(digest, signature, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        reader = new PdfReader(DEST_TMP);
        os = new FileOutputStream(dest);
        stamper = new PdfSigner(reader, os, new StampingProperties().useAppendMode());
        SignHelper.appearance(stamper, stamperSrc2, 2, rectangle, reason, location, 345, 519);
        stamper.setCertificationLevel(1);
        stamper.signDetached(digest, signature, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

    }

    public void sign2(String src  //需要签章的pdf文件路径
            , String dest  // 签完章的pdf文件路径
            , String jks //证书
            , char[] pwd //签名私钥
            , String digestAlgorithm  //摘要算法名称，例如SHA-1
            , String provider  // 密钥算法提供者，可以为null
            , CryptoStandard subfilter //数字签名格式
            , String reason  //签名的原因，显示在pdf签名属性中，随便填
            , String location) //签名的地点，显示在pdf签名属性，随便填
            throws Exception {

        // 解析签名位置,获取签名的位置 矩形
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
            }
        }

        reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(DEST_TMP);
        PdfSigner stamper = new PdfSigner(reader, os, new StampingProperties().useAppendMode());
        SignHelper.appearance(stamper, stamperSrc, 2, rectangle, reason, location);
        SignHelper.sign(stamper, jks, pwd, digestAlgorithm);

        reader = new PdfReader(DEST_TMP);
        os = new FileOutputStream(dest);
        stamper = new PdfSigner(reader, os, new StampingProperties().useAppendMode());
        SignHelper.appearance(stamper, stamperSrc2, 2, rectangle, reason, location, 345, 519);
        SignHelper.sign(stamper, KEYSTORE2, pwd, digestAlgorithm);
    }


    public void signOne(SignatureParam param) {

        System.out.println(MessageFormat.format("src {0} dest {1}", param.getSrc(), param.getDest()));

        try (PdfReader reader = new PdfReader(param.getSrc()); FileOutputStream os = new FileOutputStream(param.getDest())) {
            StampingProperties properties = new StampingProperties().useAppendMode();
//            if (param.total != param.order) {
//                properties = new StampingProperties();
//            }
            PdfSigner stamper = new PdfSigner(reader, os, properties);
            SignHelper.appearance(stamper, param.sigPath, param.page, param.signRect, param.reason, param.location);
            SignHelper.sign(stamper, param.keystore, param.pwd, param.digestAlg);
        } catch (Exception e) {

        }


    }


    public static void main(String[] args) throws Exception {
//        try {
//            // 读取keystore ，获得私钥和证书链 jks
//            Pair<PrivateKey, Certificate[]> keyPair = SignHelper.keyStore(KEYSTORE, PASSWORD);
//            // new一个上边自定义的方法对象，调用签名方法
//            SignPDF app = new SignPDF();
////            app.sign(SRC, DEST, keyPair.getValue(), keyPair.getKey(), DigestAlgorithms.SHA256, null, CryptoStandard.CADES, "Test 1",
////                    "Ghent");
//            app.sign2(SRC, DEST, KEYSTORE, PASSWORD, DigestAlgorithms.SHA256, null, CryptoStandard.CADES, "Test 1",
//                    "Ghent");
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, e.getMessage());
//            e.printStackTrace();
//        }

        testSign();
    }

    public static void testSign() throws Exception {

        List<SignatureParam> params = new ArrayList<>();
        // 解析签名位置,获取签名的位置 矩形
        PdfReader reader = new PdfReader(SRC);
        PdfDocument document = new PdfDocument(reader);
        int numberOfPages = document.getNumberOfPages();
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(document);

        int count = 0;
        for (int i = 0; i < numberOfPages; i++) {
            RegexBasedLocationExtractionStrategy strategy = contentParser.processContent(
                    1 + i,
                    new RegexBasedLocationExtractionStrategy("\\$name\\w"));

            for (IPdfTextLocation resultantLocation : strategy.getResultantLocations()) {
                count++;
                SignatureParam signatureParam = new SignatureParam(i + 1, SRC, DEST, count % 2 == 0 ? KEYSTORE : KEYSTORE2, PASSWORD, DigestAlgorithms.SHA256,
                        "reason", "location", resultantLocation.getRectangle(), count, count % 2 == 0 ? stamperSrc : stamperSrc2);
                params.add(signatureParam);
            }
        }
        System.out.println(params);
        SignPDF app = new SignPDF();
        for (SignatureParam param : params) {
            param.total = count;
            app.signOne(param);
        }

    }
}