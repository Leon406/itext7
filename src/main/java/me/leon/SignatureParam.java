package me.leon;

import com.itextpdf.kernel.geom.Rectangle;

import static me.leon.SignPDF.ROOT;

public class SignatureParam {
    public int page;
    public String src = "src";
    public String dest = "dest";
    public String keystore;
    public char[] pwd;
    public String digestAlg;
    public String reason;
    public String location;
    public Rectangle signRect;
    public int order;
    public int total;
    public String sigPath;

    public SignatureParam() {
    }

    public SignatureParam(int page, String src, String dest, String keystore, char[] pwd, String digestAlg, String reason, String location, Rectangle signRect, int order, String sigPath) {
        this.page = page;
        this.src = src;
        this.dest = dest;
        this.keystore = keystore;
        this.pwd = pwd;
        this.digestAlg = digestAlg;
        this.reason = reason;
        this.location = location;
        this.signRect = signRect;
        this.order = order;
        this.sigPath = sigPath;
    }

    public static final String TMP1 = ROOT + "\\tmp1";
    public static final String TMP2 = ROOT + "\\tmp2";

    public String getSrc() {
        return order == 1 ? src : order % 2 == 0 ? TMP1 : TMP2;

    }

    public String getDest() {
        return order == total ? dest : order % 2 == 0 ? TMP2 : TMP1;
    }



    public static void main(String[] args) {
        SignatureParam signatureParam = new SignatureParam();
        signatureParam.total = 3;

        for (int i = 0; i < signatureParam.total; i++) {
            signatureParam.order = i + 1;
            System.out.println(signatureParam.getSrc() + " " + signatureParam.getDest());
        }
    }
}
