package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtils {
    public static synchronized String readCell(int sno,String header,String sheetName) throws IOException {
            try(FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\resources\\ExcelData\\test_data.xlsx");
                XSSFWorkbook workbook = new XSSFWorkbook(fis)){
                XSSFSheet sheet = workbook.getSheet(sheetName);
                int col_target = -1;
                int row_target = -1;
                Row headerRow = sheet.getRow(0);
                for(Cell cell : headerRow){
                    if(cell.getStringCellValue().equalsIgnoreCase(header)){
                        col_target = cell.getColumnIndex();
                        break;
                    }
                }

                for(int i = 1; i<=sheet.getLastRowNum();i++){
                    Row row = sheet.getRow(i);
                    if(row != null){
                        Cell cell = row.getCell(0);
                        double i1 = cell.getNumericCellValue();
                        if(i1==sno){
                            row_target = cell.getRowIndex();
                            break;
                        }
                    }
                }

                if(col_target == -1 || row_target == -1){
                    throw new IllegalArgumentException("Either column heading or row heading not found in the sheet");
                }
                Row dataRow = sheet.getRow(row_target);
                Cell dataCell = dataRow.getCell(col_target);
                return switch (dataCell.getCellType()){
                    case NUMERIC -> String.valueOf(dataCell.getNumericCellValue());
                    case STRING -> dataCell.getStringCellValue();
                    case BOOLEAN -> String.valueOf(dataCell.getBooleanCellValue());
                    default -> null;
                };
            }

    }
}
