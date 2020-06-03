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

public class Demodylyator {

    public Demodylyator() {
    }
    public void writeDemodylateSignal (Double[] sg,int colNum, String FILE_NAME_WRITE){
        // 1)  Вычитание среднего уровня по цугу
        System.out.println("1)  Вычитание среднего уровня по цугу");
        int xn = sg.length;
        System.out.println("    длина массива цуга = " + xn);
        double[] xc = new double[2000];
        double[] xs = new double[2000];
        double pod = 0;
        for (int i = 0; i < xn; i++) {
            pod = pod + sg[i] / xn;
        }
        System.out.printf("    среднее значение сигнала по цугу = %.20f%N", pod);


//2)  Поиск центра масс квадратичного сигнала
        System.out.println("2)  Поиск центра масс квадратичного сигнала");
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
        System.out.println("    центр масс квадратичного сигнала = " + centr);


//3)  Грубая оценка полупериода
        System.out.println("3)  Грубая оценка полупериода");
        int c = (int) Math.round(centr);
        double dir = centr;
        // halfperiod estimation оценка полупериода
        while (xc[c] * xc[c - 1] > 0) {
            c++;
        }
        System.out.println("    значение переменной центра масс (c) изменилось из  " + (int) Math.round(centr) + " на значение: " + c + " - четверть периода");
        int n = 1;
        while (xc[c + n] * xc[c + n - 1] > 0) {
            n++;
        }
        System.out.println("    к значению  с = " + c + " прибавляем значение переменной N = " + n + " - к-во точек в полупериоде");

        c = (int) Math.round(centr);

//4)  Квадратичная демодуляция
        System.out.println("4)  Квадратичная демодуляция");
        System.out.println("    переприсваеваем исходное зачение переменной (с) = (int) Math.round(centr) = " + c);
        System.out.println("    " + n + "   - к-во точек в полупериоде");
        // quadrature demodulator      квадратурный демодулятор
        double qr = (double) n / 2 - n / 2;
        System.out.println("    qr = " + qr + "-дробная часть числа точек в четверте периода");
        int qn = (int) (n / 2);
        System.out.println("    qn = " + qn + "-целая часть числа точек в четверте периода");
        for (int i = qn + 1; i < xn - qn - 1; i++) {
            xs[i] = (xc[i] * xc[i] + app(qr, xc[i + qn], xc[i + qn + 1]) * app(qr, xc[i + qn], xc[i + qn + 1]) / 2 +
                    app(1 - qr, xc[i - qn - 1], xc[i - qn]) * app(1 - qr, xc[i - qn - 1], xc[i - qn]) / 2) /10;
        }

        write(xs,colNum,FILE_NAME_WRITE);

    }


    private double app(double dw, double a, double b) {
        double app;
        return app = a * dw + b * (1 - dw);
    }

    private void write(double[] doubles, int colNum, String FILE_NAME_WRITE) {
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
