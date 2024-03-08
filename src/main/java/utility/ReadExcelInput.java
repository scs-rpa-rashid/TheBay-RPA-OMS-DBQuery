package utility;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.util.Arrays;
import utility.Constants;
public class ReadExcelInput {
    static int noOfRows;
    static int noOfColumns;
    static String val="";
    static DataFormatter dataFormatter;
    static XSSFWorkbook workbook;
    static  XSSFSheet workSheet;
    static int columnNum;
    public static void main(String args[]) throws IOException {
        getDataFromSheet();
    }
    public static String[][] getDataFromSheet() throws IOException {
         workbook = new XSSFWorkbook("C:\\Users\\H895876\\Pictures\\BAY OMoC Monitoring via RPA.xlsx");
        workSheet = workbook.getSheet(Constants.INPUT_SHEET);
        noOfRows = 0;
        columnNum = 0;
        for (Row row : workSheet) {
            if (row.getCell(columnNum) != null) {
                noOfRows += 1;
            }
        }
        noOfColumns= workSheet.getRow(0).getLastCellNum();
        System.out.println("Column count: "+noOfColumns);
        System.out.println("Row count: "+noOfRows);
        String[][] dataTable = new String[noOfRows][noOfColumns];
        for (int i = 0; i < noOfRows; i++) {
            Row row = workSheet.getRow(i);
            for (int j = 0; j < noOfColumns; j++) {
                Cell cell = row.getCell(j);
                dataTable[i][j] = getCellValueAsString(cell);
                System.out.println(dataTable[i][j]);
            }
        }
        workbook.close();
        return dataTable;
    }
    private static String getCellValueAsString(Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                val = cell.getStringCellValue();
                break;
            case NUMERIC:
                dataFormatter = new DataFormatter();
                val = dataFormatter.formatCellValue(cell);
                break;
            case BOOLEAN:
                val = String.valueOf(cell.getBooleanCellValue());
                break;
            case BLANK:
                break;
        }
        return val;
    }
}
