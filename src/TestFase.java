public class TestFase {
    public ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();


    public static void main(String[] args) {
        TestFase testFase = new TestFase();
        testFase.centerOfWeightSeek();
    }


    private void centerOfWeightSeek() {
        Double[] sg = readAndWriteInExel.read(0, NameFile.FILE_NAME_READ);
//        for (int i = 0; i < sg.length; i++) {
//            System.out.println(sg[i]);
//        }
//1_________________________________________________________________________________________
        System.out.println("1)  Вычитание среднего уровня по цугу");
        int xn = sg.length;
        System.out.println("    длина массива цуга = " + xn);
        double[] xc = new double[xn];
        double[] xs = new double[xn];
        double pod = 0;
        for (int i = 0; i < xn; i++) {
            pod = pod + sg[i] / xn;
        }
//2_________________________________________________________________________________________
        System.out.println("2)  Поиск центра масс квадратичного сигнала");
        for (int i = 0; i < xn; i++) {
            xc[i] = (sg[i] - pod);
            xs[i] = 0; ///                                                                              Зачем присваивать нули??? Убрать!
        }
//                readAndWriteInExel.write(xc,1,0, NameFile.FILE_NAME_WRITE);


        //2.1_________________________________________________________
//        System.out.println("2)  Нахождение максимумов массива");
//        double [] arrOfmax = new double[xc.length];
//        double [] arrOfindex = new double[xc.length];
//
//        int numberIndex = 0;
//        double max = 0;
//        for (int i = 2; i <sg.length-3 ; i++) {
//            if(xc[i]>=xc[i-2]&&xc[i]>=xc[i-1]&& xc[i]>=xc[i+1]&&xc[i]>=xc[i+2]&& xc[i]>0){
//                arrOfmax[numberIndex] = xc[i];
//                arrOfindex[numberIndex]= i;
//                numberIndex++;
//            }
//            System.out.println(i);
//        }
//        readAndWriteInExel.write(arrOfindex,1,0, NameFile.FILE_NAME_WRITE);
//        readAndWriteInExel.write(arrOfmax,2,0, NameFile.FILE_NAME_WRITE);



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
        double dir = centr;   // нигде не используется
        while (xc[c] * xc[c - 1] > 0) {
            c++;
        }
        System.out.println("    значение переменной (c) изменилось из  " + (int) Math.round(centr) + " на значение: " + c );
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
        double qr = (double) n / 2 - n / 2; // дробная часть числа
        System.out.println("    qr = " + qr + "-дробная часть числа точек в четверте периода");
        int qn = (int) (n / 2);
        System.out.println("    qn = " + qn + "-целая часть числа точек в четверте периода");
        for (int i = qn + 1; i < xn - qn - 1; i++) {
            xs[i] = (xc[i] * xc[i] + app(qr, xc[i + qn], xc[i + qn + 1]) * app(qr, xc[i + qn], xc[i + qn + 1]) / 2 +
                    app(1 - qr, xc[i - qn - 1], xc[i - qn]) * app(1 - qr, xc[i - qn - 1], xc[i - qn]) / 2) / 10;
        }
//                readAndWriteInExel.write(xs,5,0, NameFile.FILE_NAME_WRITE);
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
        System.out.println("    wt = " + wt + " - переменная для выравнивания центра или усредненное к-во точек справа и слева");
        System.out.println("    (n+) = " + n + " - к-во точек от центра вправо  ");
        n = 0;
        while ((xs[c - n] > xs[c] / 10) && (c - n > 0)) {
            n++;
        }
        System.out.println("    (n-) = " + n + " - к-во точек от центра влево  ");

//7_________________________________________________________________________________________
        System.out.println("7)  Коррекция уровня на вертикальную асимметрию цуга");
        System.out.println("    Определение нулевого уровня как среднего сигнала за рамками цуга");
        System.out.println("    c = " + c + " - центр демодулированного сигнала из внесенной центрировкой");
        System.out.println("    wt = " + wt + " - поправочный коэффициент для коррекции центра масс и среднее значение");

        c = (int) Math.round(c + wt - n / 2.0);
        wt = wt + n / 2.0;
        int br = (int) Math.round(wt * 2.7 / 2.0);
        System.out.println("    br = " + br + "- это (wt * 2.7 / 2.0)");
//8_________________________________________________________________________________________
        System.out.println("8)  Расчет периода осциляций");
        System.out.println("     -поиск нулей сигнала");
        System.out.println("     -фильтрация ложных нулей");
        System.out.println("     -поиск среднего значения полупериода ");
        n = 0;
        double[] pos = new double[100];
//        for (int i = -br + 1; i < br + 1 && (c+i+1)<xn && (c + i - 1)> 0; i++) {   // дописала && (c+i+1)< xn && (c + i - 1)> 0 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        for (int i = -br + 1; i < br + 1; i++) {
            if (xc[c + i + 1] * xc[c + i - 1] < 0) {
                pos[n] = i + (xc[c + i - 1] + xc[c + i + 1]) / (xc[c + i - 1] - xc[c + i + 1]); // определение координат нулей
//                System.out.println(n + " i = " + i + "  pos[n] =  "  + pos[n] + "  " + (xc[c + i - 1] + xc[c + i + 1]) / (xc[c + i - 1] - xc[c + i + 1]) + "   c + i + 1= " + (c + i + 1) +  "    xc[c + i + 1]= " + xc[c + i + 1] +  "       c + i - 1= "  + (c + i - 1) + "       xc[c + i - 1] =" + xc[c + i - 1]);
//                System.out.println(n + " i = " + i + "  pos[n] =  "  + pos[n] + "  " + (xc[c + i - 1] + xc[c + i + 1]) / (xc[c + i - 1] - xc[c + i + 1]));
                n++;
            }
            if (n > 1) {
                xs[c + i] = (pos[n - 1] - pos[n - 2]) / 24.0; // Почему делим на 24 в программе дальше используется только во втором демодуляторе ?????????????????????????????????????????
//                System.out.println((c + i)+ "  " + ((pos[n - 1] - pos[n - 2]) / 24.0));
            }
        }
//        System.out.println(n);
        n--;
        double per = 0;
        ave = 0;

        int k = 0;
        for (int i = 1; i < n; i++) {                                // Последний ноль не захватывается  не надо делать n-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if ((pos[i] - pos[i - 1]) > 2) {
                k++;
                pos[k] = pos[i];
//                System.out.println("k = " + k + "   pos[k] = " + pos[k]);  // Все правильно, может в другой массив сохранять!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Проверить на идеальном сигнале
            } else {
                pos[k] = (pos[i] + pos[k]) / 2;   // если нули лежат близко друг к другу, находим их среднее
//                System.out.println("k = " + k + "   pos[k] = " + pos[k]);
            }
        }
        for (int i = 0; i < pos.length; i++) {
//            System.out.println(i + "  " + pos[i]); // записано только первые 25 значений нулей остальные остались дубликаты
//            System.out.println(pos[i]);
        }
        n = k;
        per = 0;
        ave = 0;
//        System.out.println("n = "+n);
        for (int i = 1; i < n; i++) {      // !!!!!!!!!!!!!!!!!!! значение n на 1 меньше, чем реальных нулей нужно ставить <=
            if ((pos[i] - pos[i - 1]) > 2) {
                per = per + 1.0 * (pos[i] - pos[i - 1]); // суммируем расстояние между нулями
                ave = ave + 1; //суммируем к-во нулей
//                System.out.println(pos[i]);
            }
        }


        per = per / ave; //среднее расстояние между нулями (к-во точек)
        System.out.println("     - 1.среднее значения полупериода (к-во точек между нулями) = " + per);
        fre = per;
        per = 0;
        ave = 0;
        for (int i = 1; i < n; i++) {
            if ((pos[i] - pos[i - 1]) > fre * 0.5) {
                per = per + 1.0 * (pos[i] - pos[i - 1]); // сумма отрезков между  нулями, которые больше 0,5 значения среднего периода
                ave = ave + 1;
            }
        }
        per = per / ave;
        System.out.println("     - 2. 1.среднее значения полупериодов для условия (per * 0.5) = " + per);
        double phase = (pos[n / 2] + c);  // центр масс, который отцентрирован сдвинули в позицию центрального нуля
        System.out.println(" pos[n / 2] = " +  pos[n / 2] + " c=  " + c + "   pos[n / 2] + c = " + (pos[n / 2] + c));
//9_________________________________________________________________________________________
        System.out.println("9)  Поиск центрального нуля фазы");
        System.out.println("    - Поиск нуля ближайшего к центру масс с правой стороны = " + phase);
        if ((c - phase) > per / 2) {   // 906-986= -86   -86<127/2 or 290-289,0201582<12
            phase = phase + per;
            System.out.println("work");
            System.out.println("    - Поиск нуля ближайшего к центру масс с левой стороны = " + phase);
        }

        int ic = 0;
        ave = Math.abs(pos[1]);
        System.out.println("    ave = Math.abs(pos[1]) = " + ave);

        for (int i = 1; i < n; i++) {
            if (Math.abs(pos[i]) < ave) {
                ave = Math.abs(pos[i]); // находим  координату центрального нуля
                ic = i;
            }
        }
        System.out.println("    находим  координату центрального нуля, который лежит ближе к центру масс демодулированного сигнала = " + ave);
        System.out.println("    количество итерраций ic = " + ic);
        System.out.println("   n = " + n);
        System.out.println("    pos.length = " + pos.length);
        phase = 0;
        ave = 0;
        System.out.println(pos.length);
//        for (int i = 0; i < n / 2 - 1 && i<=ic && i<=n-ic; i++) {  ///  Лучше поставить такую проверку (int i = 0; i < n / 2 - 1 && i<=ic && i<=n-ic; i++)
             for (int i = 0; i < n / 2 - 1; i++) {
                ave = ave + 1;
//            System.out.println("ic =" + ic + " i=" + i);
            phase = phase + (pos[ic + i] + pos[ic - i]) / 2; // суммируем расстояние между отрицательными нулями и положительными
        }

        System.out.println("     phase/ave = " + (phase / ave));
        if (ave < 0 || ave > 0) {                                   // в каком случае ave будет меньше нуля, оно всегда положительное ave < 0
            phase = c + phase / ave; // c + среднее значение по
            System.out.println("    центр масс +    среднее значение по фазе (c+phase/ave) = " + phase);
        } else {
            phase = c;
            System.out.println("   phase  = " + phase);
        }
        System.out.println("     c = " + c);
        double Env = 0.135 * phase / per;
        // выдача результата
        System.out.println();
        System.out.println("Env = " + Env);

    }


    private static double app(double dw, double a, double b) {
        double app;
        return app = a * dw + b * (1 - dw);
    }
}
