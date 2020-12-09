package me.leon;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static me.leon.SignPDF.ROOT;

public class PdfTemplate {
    public static void main(String[] args) throws Exception {
        Map<String, String> keys = new HashMap<>();
        keys.put("assistDamage", "0");
        keys.put("dependentsDamage", "0");
        keys.put("disabilityDamage", "0");
        keys.put("identifyDamage", "0");
        keys.put("medicalDamage", "0");
        keys.put("moralDamage", "0");
        keys.put("inpatientFoodDamage", "0");
        keys.put("insuranceDamage1", "0");
        keys.put("insuranceDamage2", "0");
        keys.put("insuranceDamage3", "0");
        keys.put("nurseDamage", "0");
        keys.put("nurseDependenceDamage", "0");
        keys.put("propertyDamage", "0");
        keys.put("tardyDamage", "0");
        keys.put("trafficDamage", "0");

        InputStream is = new FileInputStream(ROOT + "/66.pdf");
        File file = new File(ROOT + "/template_out.pdf");
        FileOutputStream fos = new FileOutputStream(file);


        itext7(keys, is, fos);
//        itext5(keys, is, fos);
    }

    private static void itext5(Map<String, String> keys, InputStream is, FileOutputStream fos) throws Exception {

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(is);
        // 创建一个新的字节数组输出流,缓冲容量为32字节,在必要时会增加
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 开始添加额外的过程内容到现有的PDF文
        PdfStamper stamp = new PdfStamper(reader, baos);
        AcroFields acroFields = stamp.getAcroFields();
        // 创建字体
        BaseFont bf =BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        acroFields.addSubstitutionFont(bf);
        setField(acroFields, keys);
        // 如果为false那么生成的PDF文件还能编辑，一定要设为true
        stamp.setFormFlattening(true);
        stamp.close();

        Document doc = new Document();
        // itext中合并页面的工具类
        PdfCopy pdfCopy = new PdfCopy(doc, fos);
        doc.open();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            // 从pdf模板中抓取第i个页面
            PdfImportedPage impPage = pdfCopy.getImportedPage(new com.itextpdf.text.pdf.PdfReader(baos.toByteArray()), i);
            // 合并页面
            pdfCopy.addPage(impPage);
        }
        doc.close();
        fos.flush();
    }

    /**
     * 添加数据
     *
     * @param af
     * @param data
     * @return AcroFields
     * @throws Exception
     */
    private static AcroFields setField(AcroFields af, Map<String, String> data) throws Exception {
        for (String key : data.keySet()) {
                                            /*BaseColorred = BaseColor.RED;   // 设置字体颜色
                                            form.setFieldProperty(temp.toString(),"textcolor", red, null);*/
            Object o = data.get(key);
            af.setField(key, String.valueOf(o));
        }
        return af;
    }

    private static void itext7(Map<String, String> keys, InputStream is, FileOutputStream fileOutputStream) throws IOException {
        PdfReader reader = new PdfReader(is);
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(fileOutputStream));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();


        for (Map.Entry<String, String> stringObjectEntry : keys.entrySet()) {
            PdfFormField field;
            field = fields.get(stringObjectEntry.getKey());
            if (field == null) {
                continue;
            }
            field.setJustification(PdfFormField.ALIGN_CENTER);
            field.setValue(stringObjectEntry.getValue());
        }

        form.flattenFields();
        pdfDoc.close();
    }
}
