import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadAndWriteInExel {

    public ReadAndWriteInExel() {
    }


    //Читает exel файл 2-ю колонку
    public  Double[] read(int colNum, String FILE_NAME_READ) {
        List<Double> doubles = new ArrayList<>();
        Double[] d = new Double[doubles.size()];
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(FILE_NAME_READ);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Iterator<Cell> iterator = row.cellIterator();
                while (iterator.hasNext()) {
                    Cell cell = iterator.next();
                    Double o = null;
                    if (cell.getColumnIndex() == colNum) {
                        o = cell.getNumericCellValue();
//                        System.out.println("cell value = " + o);
                        doubles.add(o);
                    }
                }
            }
            d = doubles.toArray(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return d;
    }





    public static void write(double[] doubles, int colNum, int rowNum, String FILE_NAME_WRITE) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(new File(FILE_NAME_WRITE));
            XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
//            int rowNum = 3;
            for (double value : doubles) {
                Row row = sheet.getRow(rowNum) == null ? sheet.createRow(rowNum) : sheet.getRow(rowNum);
                rowNum++;
                Cell cell = row.getCell(colNum) == null ? row.createCell(colNum) : row.getCell(colNum);
                cell.setCellValue(value);
            }
            outputStream = new FileOutputStream(FILE_NAME_WRITE);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done");
    }


}
