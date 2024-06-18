package utility;


import exceptionutil.ApplicationException;
import logutil.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.util.Arrays;
import utility.Constants;
public class ReadExcelInput {
    int noOfRows;
    int noOfColumns;
    String val = "";
    DataFormatter dataFormatter;
    XSSFWorkbook workbook;
    XSSFSheet workSheet;
    CellStyle cs;
    int columnNum;
    String[][] dataTable;

    Cell cell;
    Row row;

    String strExceptionMessage;

    public String[][] getDataFromSheet() throws Exception {

        try {
            Log.info("Reading OMS DB Query input file...");
            workbook = new XSSFWorkbook(Constants.INPUT_FILE);
            workSheet = workbook.getSheet(Constants.INPUT_SHEET);
            Log.info("Worksheet: " + workSheet.getSheetName());
            noOfRows = 0;
            columnNum = 0;

            for (Row row : workSheet) {
                // Get the cell in the first column (index 0)
                Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cell.getCellType() != CellType.BLANK) {
                    noOfRows++;
                }
            }
            System.out.println(noOfRows);
//            for (Row row : workSheet) {
//                if (row.getCell(columnNum).getCellType() != null) {
//                    noOfRows += 1;
//                }
//            }
                noOfColumns = workSheet.getRow(0).getLastCellNum();
                Log.info("Rows count: " + noOfRows);
                Log.info("Column count: " + noOfColumns);

               dataTable = new String[noOfRows][noOfColumns];
                for (int i = 0; i < noOfRows; i++) {
                    row = workSheet.getRow(i);
                    for (int j = 0; j < noOfColumns; j++) {
                        cell = row.getCell(j);
                        dataTable[i][j] = getCellValueAsString(cell);

                    }
                }
        }
        catch(Exception e){
                strExceptionMessage = "Failure in reading excel input file. Exception message: " + e.getMessage() + '\n' + "Exception source: " + e.getCause();
                Log.error(strExceptionMessage);
                throw new ApplicationException(strExceptionMessage);

            }
        workbook.close();
        return dataTable;
    }
        private String getCellValueAsString (Cell cell){
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

