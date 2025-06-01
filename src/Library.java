import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Library {

    private final List<Book> bookList = new ArrayList<>();
    private String bookFilePath;

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

    public void loadBooksFromExcel(String filePath) throws IOException {
        this.bookFilePath = filePath;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            boolean header = true;

            for (Row row : sheet) {
                if (header) { header = false; continue; }

                Cell cell0 = row.getCell(0);
                Cell cell1 = row.getCell(1);
                Cell cell2 = row.getCell(2);
                Cell cell3 = row.getCell(3);

                String id     = getStringValue(cell0);
                String title  = getStringValue(cell1);
                String author = getStringValue(cell2);
                int copies    = (int) cell3.getNumericCellValue();

                bookList.add(new Book(id, title, author, copies));
            }
        }
    }

    public void saveBooksToExcel() throws IOException {
        if (bookFilePath == null) return;

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Books");

            // header
            Row h = sheet.createRow(0);
            h.createCell(0).setCellValue("Book ID");
            h.createCell(1).setCellValue("Title");
            h.createCell(2).setCellValue("Author");
            h.createCell(3).setCellValue("Copies");

            int r = 1;
            for (Book b : bookList) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(b.getBookId());
                row.createCell(1).setCellValue(b.getTitle());
                row.createCell(2).setCellValue(b.getAuthor());
                row.createCell(3).setCellValue(b.getAvailableCopies());
            }

            try (FileOutputStream fos = new FileOutputStream(bookFilePath)) {
                wb.write(fos);
            }
        }
    }

    public Book getBookById(String id) {
        return bookList.stream()
                .filter(b -> b.getBookId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void displayAvailableBooks() {
        bookList.forEach(Book::displayInfo);
    }
}
