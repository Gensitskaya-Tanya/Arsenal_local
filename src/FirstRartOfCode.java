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

/**
 * Created by vertex on 07.02.2020.
 */
public class FirstRartOfCode {


    private static final String FILE_NAME_READ = "G:\\Информация по расчету фаз\\тестирование частей кода\\300x300.xlsx";
    private static final String FILE_NAME_WRITE = "G:\\Информация по расчету фаз\\тестирование частей кода\\300x300.xlsx";

    public static void main(String[] args) {
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
        Double[] arr = readAndWriteInExel.read(0, FILE_NAME_READ);
//        for (int i = 0; i < arr.length; i++) {
//            System.out.println(arr[i]);
//        }
        centerOfWeightSeek(arr);
//        write(xs, 2);
    }


    private static void centerOfWeightSeek(Double[] sg) {
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();

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

        for (int i = 0; i < xn; i++) {
//            xc[i] = (sg[i] - pod);
            xs[i] = 0;
        }
//Часть кода, которая выравнивает данные, убирая наклон. Используется линия регрессии.
//_______________________________________________________________
        Regres regres = new Regres();
        xc = regres.getAlignedDate(sg);
        readAndWriteInExel.write(xc, 4,0, FILE_NAME_WRITE);

//2_________________________________________________________________________________________
        System.out.println("2)  Поиск центра масс квадратичного сигнала");
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
//        write(xs, 6);

        /*
            private static double app(double dw, double a, double b) {
        double app;
        return app = a * dw + b * (1 - dw);      app= (xc[i + qn])*qr + xc[i + qn + 1]*(1-qr)
    }
         */


//4.1        Удаление значений из массива, которые ниже порога 30
        System.out.println("4.1)  Удаление значений из массива, которые ниже порога 30");
        MinMax minMax = new MinMax();
        double[] newArr = minMax.removeDateFromArr(xs, 35);
        GaussFunction gaussFunction = new GaussFunction();

        double[] gaus = gaussFunction.getGaussFunction(newArr);
        for (int i = 0; i < gaus.length; i++) {
            xs[i] = gaus[i];
        }
        readAndWriteInExel.write(xs, 9,0, FILE_NAME_WRITE);
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
            double temp = (phase / ave);
            System.out.println("MinMax = " + temp);
            System.out.println("    центр масс +    среднее значение по фазе (c+phase/ave) = " + c + "+" + temp + "=" + phase);
        } else {
            phase = c;
            System.out.println("   phase  = " + phase);
        }
        System.out.println("     c = " + c);


        // выдача результата
//        double Env = (phase / fn) * 0.270 + centreDemod*0.135/per ;//c-shift; (centreDemod*0.135/per  выражение добавлено 16,01,2020)

        double Env = 0.135 * phase / per;
        System.out.println("Env = " + Env);

    }


    private static double app(double dw, double a, double b) {
        double app;
        return app = a * dw + b * (1 - dw);
    }


}
