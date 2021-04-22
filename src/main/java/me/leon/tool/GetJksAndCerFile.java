package me.leon.tool;

import org.bouncycastle.util.encoders.Base64;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

/**
 * 签名生成工具, 仅支持windows
 */
public class GetJksAndCerFile {

    public static final String KEY_PATH = "D:/leon.jks";
    public static final String CER_PATH = "D:/leon.cer";

    public static void main(String[] args) {
        String alias = "Lambert";
        buildKeyAndSaveToJksFile(String.format(digitNameFmt, digits),alias);
        exportCerFile();
        try {
            readJks();
            readCer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void executeCommand(String[] arstringCommand) {
        try {
            Process exec = Runtime.getRuntime().exec(arstringCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    //生成密钥并保存到jks文件

    public static final String digitNameFmt = "CN=%s, OU=%s, O=%s, L=%s, ST=%s, C=%s";
    public static String[] digits = {"Leon", "Leon", "ll", "杭州", "浙江", "CN"};

    /**
     * @param digitName CN=(张三), OU=(人民单位), O=(人民组织), L=(广州), ST=(广东), C=(中国)
     */
    public static void buildKeyAndSaveToJksFile(String digitName,String alias) {
        String[] command = new String[]{

                "cmd ",
                "/k",
                "start", // cmd Shell命令

                "keytool",
                "-genkeypair", //表示生成密钥
                "-alias", //要处理的条目的别名（jks文件别名）
                alias,
                "-keyalg", //密钥算法名称(如 RSA DSA（默认是DSA）)
                "RSA",
//                "-keysize",//密钥位大小(长度)
//                "1024",
                "-sigalg", //签名算法名称
                "SHA256withRSA",
                "-dname",// 唯一判别名,CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称),
// ST=(州或省份名称), C=(单位的两字母国家代码)"
               digitName,
                "-validity", // 有效天数
                "36500",
                "-keypass",// 密钥口令(私钥的密码)
                "123456",
                "-keystore", //密钥库名称(jks文件路径)
                KEY_PATH,
                "-storepass", // 密钥库口令(jks文件的密码)
                "123456",
                "-v"// 详细输出（秘钥库中证书的详细信息）
        };
        executeCommand(command);
    }


    //从jks文件中导出证书文件
    public static void exportCerFile() {
        String[] command = new String[]{
                "cmd ", "/k",
                "start", // cmd Shell命令


                "keytool",
                "-exportcert", // - export指定为导出操作
                "-alias", // -alias指定别名，这里是ss
                "sun",
                "-keystore", // -keystore指定keystore文件，这里是d:/demo.keystore
                KEY_PATH,
                "-rfc",
                "-file",//-file指向导出路径
                CER_PATH,
                "-storepass",// 指定密钥库的密码
                "123456"
        };
        executeCommand(command);
    }

    //读取jks文件获取公、私钥
    public static void readJks() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(KEY_PATH), "123456".toCharArray());
        Enumeration<String> aliases = keyStore.aliases();
        String alias = null;
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
        }
        System.out.println("jks文件别名是：" + alias);
        PrivateKey key = (PrivateKey) keyStore.getKey(alias, "123456".toCharArray());
        System.out.println("jks文件中的私钥是：" + new String(Base64.encode(key.getEncoded())));
        Certificate certificate = keyStore.getCertificate(alias);
        PublicKey publicKey = certificate.getPublicKey();
        System.out.println("jks文件中的公钥:" + new String(Base64.encode(publicKey.getEncoded())));
    }

    //读取证书文件获取公钥
    public static void readCer() throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate =
                certificateFactory.generateCertificate(new FileInputStream(CER_PATH));
        PublicKey publicKey = certificate.getPublicKey();
        System.out.println("证书中的公钥:" + new String(Base64.encode(publicKey.getEncoded())));
    }
}

