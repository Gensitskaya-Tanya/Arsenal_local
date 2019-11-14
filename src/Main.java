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
//        for (int i = 0; i < arr.length; i++) {
//            System.out.println(arr[i]);
//        }
        centerOfWeightSeek(arr);
//        write(xs, 2);
    }


    private static void centerOfWeightSeek(Double[] sg) {
// вычитание среднего уровня
        System.out.println("1)  Вычитание среднего уровня по цугу");
        int xn = sg.length;
        System.out.println("    длина массива цуга = " + xn);
        double[] xc = new double[2000];
        double[] xs = new double[2000];

        double pod = 0;
        for (int i = 0; i < xn; i++) {
            pod = pod + sg[i] / xn;
        }
        System.out.println("    среднее значение сигнала по цугу = " + pod);
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

        System.out.println("3)  Грубая оценка полупериода");
        int c = (int) Math.round(centr);
        double dir = centr;
        // halfperiod estimation оценка полупериода
        while (xc[c] * xc[c - 1] > 0) {
            c++;
        }
        System.out.println("    значение переменной (c) изменилось из  " + (int) Math.round(centr) + " на значение: " + c + " - четверть периода");
        int n = 1;
        while (xc[c + n] * xc[c + n - 1] > 0) {
            n++;
        }
        System.out.println("    к значению  с = " + c + " прибавляем значение переменной n = " + n + " - следующий полупериод");

        c = (int) Math.round(centr);
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
                    app(1 - qr, xc[i - qn - 1], xc[i - qn]) * app(1 - qr, xc[i - qn - 1], xc[i - qn]) / 2) / 10;
        }
        System.out.println("5)  Поиск центра масс демодулированного сигнала");
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
        System.out.println("    centr = " + c + "   - центр масс демодулированного сигнала");
        // 460 ms
// range estimation     оценка дальности
        System.out.println("6)  Оценка размера цуга по скорости затухания демодулированного сигнала");
        double wt = 0;
        n = 0;
        while ((xs[c + n] > xs[c] / 10) && (c + n < xn)) {
            n++;

        }
        wt = wt + n / 2.0;
        System.out.println("    xn = " + xn + " - к-во точек суммарное");
        System.out.println("    wt = " + " - переменная для выравнивания центра или усредненное к-во точек справа и слева");
        System.out.println("    (n+) = " + n + " - к-во точек от центра вправо  ");


        n = 0;
        while ((xs[c - n] > xs[c] / 10) && (c - n > 0)) {
            n++;
        }
        System.out.println("    (n-) = " + n + " - к-во точек от центра влево  ");
//        //Env:=c+wt-n/2.0;
////  exit;
//
        c = (int) Math.round(c + wt - n / 2.0);

        wt = wt + n / 2.0;
        int br = (int) Math.round(wt * 2.7 / 2.0);
//        // 470 ms
        System.out.println("7)  Коррекция уровня на вертикальную асимметрию цуга");
        System.out.println("    Определение нулевого уровня как среднего сигнала за рамками цуга");
        System.out.println("    c = " + c + " - центр демодулированного сигнала из внесенной центрировкой");
        System.out.println("    wt = " + wt + " - усредненное к-во точек справа и слева");
        System.out.println("    br = " + br + "- это (wt * 2.7 / 2.0)");

        System.out.println("8)  Расчет периода осциляций");
        System.out.println("     -поиск нулей сигнала");
        System.out.println("     -фильтрация ложных нулей");
        System.out.println("     -поиск среднего значения полупериода ");




        n = 0;
        double[] pos = new double[100];
        for (int i = -br + 1; i < br + 1; i++) {
            if (xc[c + i + 1] * xc[c + i - 1] < 0) {
                pos[n] = i + (xc[c + i - 1] + xc[c + i + 1]) / (xc[c + i - 1] - xc[c + i + 1]); // определение нулей
//                System.out.println(n + " " + pos[n] + "  " + (xc[c + i - 1] + xc[c + i + 1]) / (xc[c + i - 1] - xc[c + i + 1]));
                n++;
            }
            if (n > 1) {
                xs[c + i] = (pos[n - 1] - pos[n - 2]) / 24.0;
//                System.out.println((c + i)+ "  " + ((pos[n - 1] - pos[n - 2]) / 24.0));
            }
        }
//        System.out.println(n);
        n--;
        double per = 0;
        ave = 0;

        int k = 0;
        for (int i = 1; i < n; i++) {
            if ((pos[i]-pos[i-1])>2) {
                k++;
                pos[k]=pos[i];
            }else {
                pos[k]=(pos[i]+pos[k])/2;   // записали все нули
            }

        }
        ;
//        for (int i = 0; i < pos.length ; i++) {
//            System.out.println(i + "  "+ pos[i]); // записано только первые 25 значений нулей остальные остались дубликаты
//        }
        n=k;

//
//        for i:=1 to n do
//            if ((pos[i]-pos[i-1])>2) then
//                    begin
//        per:=per+1.0*(pos[i]-pos[i-1]);
//        ave:=ave+1;
//        end;
//        per:=per/ave;
//        fre:=per;
//        per:=0;
//        ave:=0;
//        for i:=1 to n do
//            if ((pos[i]-pos[i-1])>fre*0.5) then
//                    begin
//        per:=per+1.0*(pos[i]-pos[i-1]);
//        ave:=ave+1;
//        end;
//        per:=per/ave;

        per=0;
        ave=0;
        for (int i = 1; i < n ; i++) {
            if  ((pos[i]-pos[i-1])>2){
                per=per+1.0*(pos[i]-pos[i-1]); // суммируем расстояние между нулями
                ave=ave+1; //суммируем к-во нулей
            }
        }

        per=per/ave; //среднее расстояние между нулями (к-во точек)
        System.out.println("     - 1.среднее значения полупериода (к-во точек между нулями) = " + per);
        fre=per;
        per=0;
        ave=0;
        for (int i = 1; i < n; i++) {
            if ((pos[i]-pos[i-1])>fre*0.5){
                per=per+1.0*(pos[i]-pos[i-1]);
                ave=ave+1;
            }
        }
        per=per/ave;
        System.out.println("     - 2. среднее значения полупериода (к-во точек между нулями) = " + per);
        double phase =(pos[n/2]+c);
        System.out.println(phase);



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
