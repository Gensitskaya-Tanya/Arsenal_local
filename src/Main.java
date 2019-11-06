import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static final String FILE_NAME = "H:\\Информация по расчету фаз\\Сгенерированный сигнал шаг 0,01.xlsx";
    private static final String FILE_NAME_WRITE = "G:\\IDEA\\Arsenal_local\\MyNextExcel.xlsx";

    public static void main(String[] args) {
        Double[] arr = read();
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
        centerOfWeightSeek(arr);
//        write(xs, 2);
    }


    private static void centerOfWeightSeek(Double[] sg) {
// вычитание среднего уровня
        int xn = sg.length;
        System.out.println(" длина массива = " + xn);
        double[] xc = new double[2000];
        double[] xs = new double[2000];
        double pod = 0;                       // среднее значение сигнала
        for (int i = 0; i < xn; i++) {
            pod = pod + sg[i] / xn;
        }
        System.out.println("среднее значение сигнала = " + pod);
        // // centr of weight seek    центр поиска веса
        for (int i = 0; i < xn; i++) {
            xc[i] = (sg[i] - pod);
            xs[i] = 0;
        }
        double fre = 0;
        double ave = 0;
        for (int i = 0; i < xn; i++) {
            fre = fre + xc[i] * xc[i] * i;
            ave = ave + xc[i] * xc[i];
        }
        double centr = fre / ave;
        System.out.println("центр масс = " + centr);
        int c = (int) Math.round(centr);
        double dir = centr;
        // halfperiod estimation оценка полупериода
        while (xc[c] * xc[c - 1] > 0) {
            c++;
        }
        System.out.println("значение переменной с = " + c);
        int n = 1;
        while (xc[c + n] * xc[c + n - 1] > 0) {
            n++;
        }
        System.out.println("значение переменной с = " + c);
        System.out.println("значение переменной n = " + n);
        c = (int) Math.round(centr);
        System.out.println("значение переменной с после  Math.round(centr) = " + c);

        // quadrature demodulator      квадратурный демодулятор
        double qr = (double) n / 2 - n / 2;
        System.out.println(" значение qr = " + qr);
        int qn = (int) (n / 2);
        System.out.println(" значение qn = " + qn);
        for (int i = qn + 1; i < xn - qn - 1; i++) {
            xs[i] = (xc[i] * xc[i] + app(qr, xc[i + qn], xc[i + qn + 1]) * app(qr, xc[i + qn], xc[i + qn + 1]) / 2 +
                    app(1 - qr, xc[i - qn - 1], xc[i - qn]) * app(1 - qr, xc[i - qn - 1], xc[i - qn]) / 2) / 10;
        }
        // centr of weight seek second iteration   центр веса искать вторую итерацию
        n = 1;
        ave = 0;
        fre = 0;
        // 450 ms
        for (int i = 0; i < xn; i++) {
            fre = fre + xs[i] * i;
            ave = ave + xs[i];
        }

        centr = fre / ave;
        c = (int) Math.round(centr);
        // 460 ms
        System.out.println("центр масс на второй итеррации демодулированного сигнала = " + c);
// range estimation     оценка дальности
        double wt = 0;
        n = 0;
        while ((xs[c + n] > xs[c] / 10) && (c + n < xn)) {
            n++;
        }
        System.out.println("оценка дальности n = " + n);
        wt = wt + n / 2.0;
        n = 0;
        while ((xs[c - n] > xs[c] / 10) && (c - n > 0)) {
            n++;
        }
        System.out.println("оценка дальности n = " + n);
//        //Env:=c+wt-n/2.0;
////  exit;
//
        c = (int) Math.round(c + wt - n / 2.0);
        wt = wt + n / 2.0;
        int br = (int) Math.round(wt * 2.7 / 2.0);
//        // 470 ms
        System.out.println("c  + wt +  br  =" + c + "   " + wt+ "  "+ br);
    }

    private static double app(double dw, double a, double b) {
        double app;
        return app = a * dw + b * (1 - dw);
    }

    //Читает exel файл 2-ю колонку
    private static Double[] read() {
        List<Double> doubles = new ArrayList<>();
        Double[] d = new Double[doubles.size()];
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(FILE_NAME);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Iterator<Cell> iterator = row.cellIterator();
                while (iterator.hasNext()) {
                    Cell cell = iterator.next();
                    Double o = null;
                    if (cell.getColumnIndex() == 1) {
                        o = cell.getNumericCellValue();
                        System.out.println("cell value = " + o);
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
            int rowNum = 3;
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
