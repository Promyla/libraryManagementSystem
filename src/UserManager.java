import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private final List<User> userList = new ArrayList<>();

    public void loadUsersFromExcel(String filePath, String userType) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            boolean header = true;

            for (Row row : sheet) {
                if (header) {
                    header = false;
                    continue;
                }

                String id    = getStringValue(row.getCell(0));
                String name  = getStringValue(row.getCell(1));
                String third = getStringValue(row.getCell(2));  // dept or desig

                if ("student".equalsIgnoreCase(userType)) {
                    userList.add(new Student(id, name, third));
                } else if ("teacher".equalsIgnoreCase(userType)) {
                    userList.add(new Teacher(id, name, third));
                }
            }
        }
    }

    public User authenticateUser(String id) throws UserNotFoundException {
        return userList.stream()
                .filter(u -> u.getUserId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new UserNotFoundException("User not found."));
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default      -> "";
        };
    }
}
