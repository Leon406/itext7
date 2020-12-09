package me.leon;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static me.leon.SignPDF.ROOT;

public class PdfTemplate {
    public static void main(String[] args) throws Exception {
        InputStream is = new FileInputStream(ROOT + "/66.pdf");
        File file = new File(ROOT + "/template_out.pdf");
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        PdfReader reader = new PdfReader(is);

        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(fileOutputStream));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();
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
