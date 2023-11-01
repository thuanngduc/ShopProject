package com.shop.admin.user.export;

import com.shop.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//        String timestamp = dateFormatter.format(new Date());
//        String fileName = "users_"+timestamp+".csv";
//        response.setContentType("text/csv");
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=" +fileName;
//        response.setHeader(headerKey, headerValue);
        super.setResponseHeader(response, "text/csv", ".csv", "users_");
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "E-mail", "FirstName", "LastName", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
        csvWriter.writeHeader(csvHeader);
        for (User user : listUsers)
        {
            csvWriter.write(user, fieldMapping);
        }
        csvWriter.close();

    }
}
