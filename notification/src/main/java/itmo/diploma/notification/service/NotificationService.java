package itmo.diploma.notification.service;

import jakarta.mail.internet.MimeMessage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void processAndSendNotification(Object data, String email) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;

            if (!dataList.isEmpty()) {
                Row headerRow = sheet.createRow(0);
                Map<String, Object> firstItem = dataList.get(0);
                int cellIndex = 0;
                for (String key : firstItem.keySet()) {
                    headerRow.createCell(cellIndex++).setCellValue(key);
                }

                int rowIndex = 1;
                for (Map<String, Object> item : dataList) {
                    Row row = sheet.createRow(rowIndex++);
                    cellIndex = 0;
                    for (Object value : item.values()) {
                        row.createCell(cellIndex++).setCellValue(value.toString());
                    }
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] bytes = bos.toByteArray();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Your Data Export");
            helper.setText("Please find your data attached as an Excel file.");
            helper.addAttachment("data.xlsx", new ByteArrayResource(bytes));

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process notification", e);
        }
    }
}