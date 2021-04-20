package me.leon.tool;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

public class GetCertFile {

    //证书颁发者
    static String CertificateIssuer = "C=中国,ST=广东,L=广州,O=人民组织,OU=人民单位,CN=人民颁发";
    //证书使用者
    static String CertificateUser = "C=中国,ST=广东,L=广州,O=人民组织,OU=人民单位,CN=";

    public static void main(String[] args) {
        try {
            X509Certificate cert = getCert();
            System.out.println(cert);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * CN(Common Name名字与姓氏)
     * OU(Organization Unit组织单位名称)
     * O(Organization组织名称)
     * ST(State州或省份名称)
     * C(Country国家名称)
     * L(Locality城市或区域名称)
     * */
    public static X509Certificate getCert() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        X509V3CertificateGenerator x509V3CertificateGenerator = new X509V3CertificateGenerator();
        //设置证书序列号
        x509V3CertificateGenerator.setSerialNumber(BigInteger.TEN);
        //设置证书颁发者
        x509V3CertificateGenerator.setIssuerDN(new X500Principal(CertificateIssuer));
        //设置证书使用者
        x509V3CertificateGenerator.setSubjectDN(new X500Principal(CertificateUser + "sun"));
        //设置证书有效期
        x509V3CertificateGenerator.setNotAfter(new Date(System.currentTimeMillis() + 1000 * 365 * 24 * 3600));
        x509V3CertificateGenerator.setNotBefore(new Date(System.currentTimeMillis()));
        //设置证书签名算法
        x509V3CertificateGenerator.setSignatureAlgorithm("SHA256withRSA");


        x509V3CertificateGenerator.setPublicKey(publicKey);



        //临时bc方法添加都环境变量
        Security.addProvider(new BouncyCastleProvider());
        X509Certificate x509Certificate = x509V3CertificateGenerator.generateX509Certificate(keyPair.getPrivate(), "BC");
        //写入文件
        FileOutputStream fos = new FileOutputStream("E:\\cer.cer");
        fos.write(x509Certificate.getEncoded());
        fos.flush();
        fos.close();
        return x509Certificate;
    }
}