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


public class Regres {
    public Regres() {
    }
    private static final String FILE_NAME_READ = "G:\\Информация по расчету фаз\\программа для СЫРЫХ ДАННЫХ\\zercalo step 8aver8 4000 30.01.2020\\0x470 zerkalo.xlsx";
    private static final String FILE_NAME_WRITE = "G:\\Информация по расчету фаз\\программа для СЫРЫХ ДАННЫХ\\zercalo step 8aver8 4000 30.01.2020\\0x470 zerkalo.xlsx";
//    private static final String FILE_NAME_READ = "G:\\Информация по расчету фаз\\Проверка коэфф регресии.xlsx";
//    private static final String FILE_NAME_WRITE = "G:\\Информация по расчету фаз\\Проверка коэфф регресии.xlsx";

    public static void main(String[] args) {
        Double[] arr = read();
        Regres regres = new Regres();
       regres.getAlignedDate(arr);

    }

    public double [] getAlignedDate(Double[] arr) {
        int N = arr.length;
        double Xi = 0;
        double Yi = 0;
        double XiXi = 0;
        double XiYi = 0;
        for (int i = 0; i < N; i++) {
            Xi = Xi + i;
            Yi = Yi + arr[i];
            XiXi = XiXi + i * i;
            XiYi = XiYi + i*arr[i];
        }
        double a = (Yi*XiXi-Xi*XiYi)/(N*XiXi-Xi*Xi); // коэффициент сдвига (а), уравнение регресии n_i=a+b*x_i
        System.out.println("a = " + a);
        double b = (N*XiYi-Xi*Yi)/(N*XiXi-Xi*Xi); // коєфф. наклона (b),  уравнение регресии n_i=a+b*x_i
        System.out.println("b = " + b);

        double [] regLine = new double [N];
        for (int i = 0; i < N ; i++) {
            regLine[i] = a + b*i;
        }
        double [] alignedDate = new double [N];
        for (int i = 0; i < N; i++) {
            alignedDate[i] = arr [i] -regLine[i];
        }
        double max = alignedDate[1];
        double min = alignedDate[0];
        for (int i = 0; i < N-1; i++) {
            if(alignedDate[i]<max){
                min = alignedDate[i];
                max = alignedDate[i+1];
            }else {
                min = alignedDate[i+1];
                max = alignedDate[i];
            }

        }
        return alignedDate;
    }



    private static Double[] read() {
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
                    if (cell.getColumnIndex() == 0) {
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


    private static void write(double[] doubles, int colNum) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(new File(FILE_NAME_WRITE));
            XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;
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
