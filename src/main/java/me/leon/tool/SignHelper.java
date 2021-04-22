package me.leon.tool;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Image;
import com.itextpdf.signatures.*;
import javafx.util.Pair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * 签名工具封装
 */
public class SignHelper {

    public static PdfSignatureAppearance appearance(PdfSigner stamper, String sigPath, int page, Rectangle rectangle,
                                                    String reason, String location) throws Exception {

        ImageData img = ImageDataFactory.create(sigPath);
        //读取图章图片，这个image是itext包的image
        Image image = new Image(img);

        float height = image.getImageHeight();
        float width = image.getImageWidth();
        float scale = getProperScale(height, width, 100f, 40f);
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance()
                .setPageNumber(page)
                .setPageRect(new Rectangle(rectangle.getX(), rectangle.getY(), width / scale, height / scale))
                .setSignatureGraphic(img)
                .setReason(reason)
                .setLocation(location)
                .setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);


        return appearance;
    }

    public static PdfSignatureAppearance appearance(PdfSigner stamper, String sigPath, int page, Rectangle rectangle,
                                                    String reason, String location, int left, int bottom) throws Exception {

        ImageData img = ImageDataFactory.create(sigPath);
        //读取图章图片，这个image是itext包的image
        Image image = new Image(img);

        float height = image.getImageHeight();
        float width = image.getImageWidth();
        float scale = getProperScale(height, width, 100f, 40f);
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance()
                .setPageNumber(page)
                .setPageRect(new Rectangle(left, bottom, width / scale, height / scale))
                .setSignatureGraphic(img)
                .setReason(reason)
                .setLocation(location)
                .setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);


        return appearance;
    }

    public static Pair<PrivateKey, Certificate[]> keyStore(String path, char[] pwd) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(path), pwd);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, pwd);
        Certificate[] chain = ks.getCertificateChain(alias);

        return new Pair(pk, chain);

    }

    public static IExternalDigest digest = new BouncyCastleDigest();

    public static void sign(PdfSigner stamper, String keyStorePath, char[] keyStorePwd, String digestAlgorithm) throws Exception {

        Pair<PrivateKey, Certificate[]> keyStore = keyStore(keyStorePath, keyStorePwd);
        IExternalSignature signature = new PrivateKeySignature(keyStore.getKey(), digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        stamper.setCertificationLevel(1);
        stamper.signDetached(digest, signature, keyStore.getValue(), null, null, null, 0, PdfSigner.CryptoStandard.CADES);
    }

    public static float getProperScale(float width, float height, float targetX, float targetY) {
        return Math.max(width / targetX, height / targetY);
    }

}
