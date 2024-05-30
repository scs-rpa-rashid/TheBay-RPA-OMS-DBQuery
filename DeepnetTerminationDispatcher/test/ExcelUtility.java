package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtility {
    FileInputStream fileInputStream;
    XSSFWorkbook wb;
    Workbook workbook;
    Sheet sheet;
    File inputFile;

    public Sheet readInputExcel(String filePath) throws IOException {
        inputFile = new File(filePath);
        fileInputStream = new FileInputStream(inputFile);
        wb = new XSSFWorkbook(fileInputStream);
        sheet = wb.getSheetAt(0); // Assuming there is only one sheet in the workbook
        return sheet;
    }

    public void validateSheetName(Sheet sheet, String expectedSheetName) throws IOException {
        String actualSheetName = sheet.getSheetName();
        if (!actualSheetName.equals(expectedSheetName)) {
            throw new IOException("Sheet name should be '" + expectedSheetName + "'.");
        }
    }

    public boolean validateHeaderSequence(Sheet sheet, String[] expectedHeaders) {
        Row headerRow = sheet.getRow(0); // Assuming header is in the first row
        if (headerRow != null) {
            int cellIndex = 0;
            for (String expectedHeader : expectedHeaders) {
                Cell cell = headerRow.getCell(cellIndex++);
                if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeader)) {
                    return false;
                }
            }
            return true;
        }
        return false;


    }public static void validateNumberOfRows(Sheet sheet, String fileName) throws BusinessException {
        // Exclude header row while counting rows
        int numberOfRows = sheet.getPhysicalNumberOfRows() - 1;
        if (numberOfRows == 0) {
            String message = "File '" + fileName + "' is blank. No user to process.";
            Log.error(message);
            throw new BusinessException(message);
        }
        Log.info("File '" + fileName + "' has " + numberOfRows + " rows (excluding header).");
    }
    public static void validateBlankValuesInColumns(Sheet sheet, String fileName) throws BusinessException {
        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getLastCellNum();

        for (int rowNum = 1; rowNum < rowCount; rowNum++) { // Starting from row 1 (excluding header)
            Row row = sheet.getRow(rowNum);
            for (int colNum = 0; colNum < colCount; colNum++) {
                Cell cell = row.getCell(colNum);
                if (cell == null || cell.getCellType() == CellType.BLANK) { // Use CellType.BLANK
                    String message = "Blank value found in column " + (colNum + 1) + " of row " + (rowNum + 1) + " in file '" + fileName + "'.";
                    Log.error(message);
                    throw new BusinessException(message);
                }
            }
        }
        Log.info("No blank values found in columns of file '" + fileName + "'.");
    }
}
