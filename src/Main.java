import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static final String FILE_NAME_READ = "H:\\Информация по расчету фаз\\тестирование частей кода\\test8 31.01.2020_300x300.xlsx";
    private static final String FILE_NAME_WRITE = "H:\\Информация по расчету фаз\\тестирование частей кода\\test8 31.01.2020_300x300.xlsx";

    public static void main(String[] args) {
        Double[] arr = read();
//        for (int i = 0; i < arr.length; i++) {
//            System.out.println(arr[i]);
//        }
        centerOfWeightSeek(arr);
//        write(xs, 2);
    }


    private static void centerOfWeightSeek(Double[] sg) {
//1_________________________________________________________________________________________
        System.out.println("1)  Вычитание среднего уровня по цугу");
        int xn = sg.length;
        System.out.println("    длина массива цуга = " + xn);
        double[] xc = new double[2000];
        double[] xs = new double[2000];
        double pod = 0;
        for (int i = 0; i < xn; i++) {
            pod = pod + sg[i] / xn;
        }
//2_________________________________________________________________________________________
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
//3_________________________________________________________________________________________
        System.out.println("3)  Грубая оценка полупериода");
        int c = (int) Math.round(centr);
        double dir = centr;
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
//4_________________________________________________________________________________________
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
        write(xs, 3);
//5_________________________________________________________________________________________
        System.out.println("5)  Поиск центра масс демодулированого сигнала");
        // centr of weight seek second iteration   центр веса искать вторую итерацию
        n = 1;
        ave = 0;
        fre = 0;
        for (int i = 0; i < xn; i++) {
            fre = fre + xs[i] * i;
            ave = ave + xs[i];
        }
        centr = fre / ave;
        double centreDemod = centr;
        c = (int) Math.round(centr);

        System.out.println("    centr = " + c + "   - центр масс демодулированного сигнала");
//6_________________________________________________________________________________________
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
        c = (int) Math.round(c + wt - n / 2.0);
        wt = wt + n / 2.0;
        int br = (int) Math.round(wt * 2.7 / 2.0);
//7_________________________________________________________________________________________
        System.out.println("7)  Коррекция уровня на вертикальную асимметрию цуга");
        System.out.println("    Определение нулевого уровня как среднего сигнала за рамками цуга");
        System.out.println("    c = " + c + " - центр демодулированного сигнала из внесенной центрировкой");
        System.out.println("    wt = " + wt + " - усредненное к-во точек справа и слева");
        System.out.println("    br = " + br + "- это (wt * 2.7 / 2.0)");
//8_________________________________________________________________________________________
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
            if ((pos[i] - pos[i - 1]) > 2) {
                k++;
                pos[k] = pos[i];
            } else {
                pos[k] = (pos[i] + pos[k]) / 2;   // записали все нули
            }
        }
        for (int i = 0; i < pos.length; i++) {
//            System.out.println(i + "  " + pos[i]); // записано только первые 25 значений нулей остальные остались дубликаты
        }
        n = k;
        per = 0;
        ave = 0;
//        System.out.println("n = "+n);
        for (int i = 1; i < n; i++) {      // !!!!!!!!!!!!!!!!!!! значение n на 1 меньше, чем реальных нулей нужно ставить <=
            if ((pos[i] - pos[i - 1]) > 2) {
                per = per + 1.0 * (pos[i] - pos[i - 1]); // суммируем расстояние между нулями
                ave = ave + 1; //суммируем к-во нулей
            }
        }

//        System.out.println(ave);
        per = per / ave; //среднее расстояние между нулями (к-во точек)
        System.out.println("     - 1.среднее значения полупериода (к-во точек между нулями) = " + per);
        fre = per;
        per = 0;
        ave = 0;
        for (int i = 1; i < n; i++) {
            if ((pos[i] - pos[i - 1]) > fre * 0.5) {
                per = per + 1.0 * (pos[i] - pos[i - 1]); // сумма отрезков между всеми нулями
                ave = ave + 1;
            }
        }
        per = per / ave;
        System.out.println("     - 2. среднее значения полупериода (к-во точек между нулями) = " + per);
        double phase = (pos[n / 2] + c);
//9_________________________________________________________________________________________
        System.out.println("9)  Поиск центрального нуля фазы");
        System.out.println("    - Поиск нуля ближайшего к центру масс с правой стороны = " + phase);
        if ((c - phase) > per / 2) {
            phase = phase + per;
            System.out.println("work");
        }
        System.out.println("    - Поиск нуля ближайшего к центру масс с левой стороны = " + phase);
        int ic = 0;
        ave = Math.abs(pos[1]);
        System.out.println("    ave = Math.abs(pos[1]) = " + ave);

        for (int i = 1; i < n; i++) {
            if (Math.abs(pos[i]) < ave) {
                ave = Math.abs(pos[i]); // находим  координату центрального нуля
                ic = i;
            }
        }
        System.out.println("    находим  координату центрального нуля = " + ave);
        System.out.println("    количество итерраций ic = " + ic);
        System.out.println("   n = " + n);
        System.out.println("    pos.length = " + pos.length);
        phase = 0;
        ave = 0;
        for (int i = 0; i < n / 2 - 1; i++) {
            ave = ave + 1;
            phase = phase + (pos[ic + i] + pos[ic - i]) / 2; // суммируем расстояние между нулями в правой части от центра
        }
        System.out.println("     phase/ave = " + (phase / ave));
        if (ave < 0 || ave > 0) {
            phase = c + phase / ave; // c + среднее значение по
            double temp =( phase / ave);
            System.out.println("temp = " + temp);
            System.out.println("    центр масс +    среднее значение по фазе (c+phase/ave) = " + c + "+" + temp + "=" +phase);
        } else {
            phase = c;
            System.out.println("   phase  = " +phase);
        }
        System.out.println("     c = " + c);


//10_________________________________________________________________________________________
        System.out.println("10) Квадратурный демодулятор второй итерации "); //расчет по среднему значению полупериода
        qr = (double) per / 2 - (int) per / 2;
        qn = (int) (per / 2);
        System.out.println("    qr = " + qr + "-дробная часть числа точек в четверте периода между нулями");
        System.out.println("    qn = " + qn + "-целая часть числа точек в четверте периода между нулями");
        for (int i = qn + 1; i < xn - qn - 1; i++) {
            xs[i] = (xc[i] * xc[i] + app(qr, xc[i + qn], xc[i + qn + 1]) * app(qr, xc[i + qn], xc[i + qn + 1]) / 2 +
                    app(1 - qr, xc[i - qn - 1], xc[i - qn]) * app(1 - qr, xc[i - qn - 1], xc[i - qn]) / 2) / 10;
        }
//write(xs, 10);
        wt = 0;
        n = 0;
        while ((xs[c + n] > xs[c] / 10) && (c + n < xn)) {
            n++;
        }
        wt = wt + n / 2.0;
        n = 0;
        while ((xs[c - n] > xs[c] / 10) && (c - n > 0)) {
            n++;
        }
        wt = wt + n / 2.0;
        br = (int) Math.round(1.7 * wt * 2.7 / 2.7);
        int hn = (int) (1.7 * wt * 2.7 / 2.7);
        System.out.println("wt = " + wt);
        System.out.println("hn = " + hn);
        System.out.println("br = " + br);


        if ((c + hn) > xn) {
            hn = xn - c;
        }
        if ((c - hn) < 0) {
            hn = c;
        }
        System.out.println("    c = " + c);
        System.out.println("    xn = " + xn);
        System.out.println("    hn = " + hn);
//_________________________________________________________________________________________
// frequency range calculation  расчет частотного диапазона
        System.out.println("расчет частотного диапазона");
        int fn = 100 * xn;
        double fg = fn / (2 * per);
        int fi = (int) Math.round(fg - 1.5 * 2.7 * 2.7 * Math.log(2) * fn / (2 * Math.PI * wt));
        int fo = (int) Math.round(fg + 1.5 * 2.7 * 2.7 * Math.log(2) * fn / (2 * Math.PI * wt));
        br = Math.round(20 * fn / (fo - fi));
        fi = Math.round(fi * br / fn);
        fo = Math.round(fo * br / fn);
        fn = br;
        double sf = Math.sqrt(fn);
        System.out.println("fi = " + fi);
        System.out.println("fo = " + fo);
        // local fourier transform локальное преобразование Фурье
        double[] fc = new double[2000];
        double[] fs = new double[2000];
        double[] fm = new double[2000];

        for (k = fi; k < fo; k++) {
            fc[k - fi] = xc[c] / sf;
            fs[k - fi] = 0;
            for (int i = 1; i < hn - 1; i++) {
                fc[k - fi] = fc[k - fi] + (xc[c - i] + xc[c + i]) * Math.cos(2 * Math.PI * k * i / fn) / sf;
                fs[k - fi] = fs[k - fi] + (xc[c - i] - xc[c + i]) * Math.sin(2 * Math.PI * k * i / fn) / sf;
            }
            fm[k - fi] = Math.sqrt(fc[k - fi] * fc[k - fi] + fs[k - fi] * fs[k - fi]);
        }

//write(fm,15);

        // calculate central frequency   рассчитать центральную частоту
        fre = 0;
        k = 1;
        for (int i = 0; i < fo - fi; i++) {
            if (fm[i] > fm[k]) {
                k = i;
            }
        }
        double om = 0;
        if ((fm[k + 1] - fm[k - 1]) > 0) {
            om = (fm[k + 1] - fm[k - 1]) / (fm[k] - fm[k - 1]);
            om = om / 2;
        }
        System.out.println("om = "+ om);
        if ((fm[k + 1] - fm[k - 1]) < 0) {
            om = (fm[k + 1] - fm[k - 1]) / (fm[k] - fm[k + 1]);
            om = om / 2;
        }
        System.out.println("om = "+ om);
        om = om + k;
        System.out.println("om + k = "+ om);
        k = (int) (om);
// calculate phase  рассчитать фазу
        double[] fp = new double[2000];
        for (int i = 0; i < fo - fi; i++) {
            if (fm[i] > fm[k] * 0.5) {
                fp[i] = Math.acos(fc[i] / fm[i]);
            }
        }
//        write(fp,16);
//        for (int i = 0; i <fp.length ; i++) {
//            System.out.println(fp[i]);
//        }
        double shift = 0;
        fre = 0;
        ave = 0;
        System.out.println("k="+k);
        shift = Math.abs(fp[k] - fp[k - 1]);
        System.out.println("shift = " + shift);
        int i = k + 1;
        while ((fm[i] > fm[k] * 0.5) && (Math.abs(shift - Math.abs(fp[i] - fp[i - 1])) < shift)) {
            if (Math.abs(shift - Math.abs(fp[i] - fp[i - 1])) < shift) {
                ave = ave + 1;
                fre = fre + Math.abs(fp[i] - fp[i - 1]);
            }
            i++;
        }
        i = k - 1;
        while ((fm[i] > fm[k] * 0.5) && (Math.abs(shift - Math.abs(fp[i + 1] - fp[i])) < shift)) {
            if (Math.abs(shift - Math.abs(fp[i + 1] - fp[i])) < shift) {
                ave = ave + 1;
                fre = fre + Math.abs(fp[i + 1] - fp[i]);
            }
            i--;
        }
        phase = fn / 2 + fp[k] - (fre / ave) * (om + fi);
        System.out.println("phase = " + phase);
        shift = -fn * (fre / ave) / (2 * Math.PI);
        if (dir < c) {
            shift = -shift;
        }
        // выдача результата
        double Env = (phase / fn) * 0.270 + centreDemod*0.135/per ;//c-shift; (centreDemod*0.135/per  выражение добавлено 16,01,2020)


        System.out.println("shift = "+shift);

        System.out.println();

        System.out.println("Env = " + Env);

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
            XSSFWorkbook workbook = new XSSFWorkbook(FILE_NAME_READ);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Iterator<Cell> iterator = row.cellIterator();
                while (iterator.hasNext()) {
                    Cell cell = iterator.next();
                    Double o = null;
                    if (cell.getColumnIndex() == 1) {
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
