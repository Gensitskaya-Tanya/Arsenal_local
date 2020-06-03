public class AppNew {
    public static void main(String[] args) {

        for (int startX = 200; startX < 201 ; startX++) {
            ReadBinFile readBinFile = new ReadBinFile();
            int Y = 300;
            int[] arr = readBinFile.readPointFromBinFile(startX, Y , NameFile.BIN_FILE_NAME_READ);


//        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
//        Double[] arr1 = readAndWriteInExel.read(0, NameFile.FILE_NAME_READ);
//        double[] arr2 = new double[arr1.N];
//        for (int i = 0; i < arr1.N; i++) {
//            arr2[i] = arr1[i];
//        }
//        int[] arr3 = new int[arr1.N];
//        for (int i = 0; i < arr1.N; i++) {
//            arr3[i] = (int) arr2[i];
//            System.out.println(arr3[i]);
//        }

            AppNew appNew = new AppNew();
            double rezult = appNew.runAppNew(arr);
//            System.out.println("X: " + startX + " Y: " + Y +"  "+ rezult);
//            System.out.println(rezult);
        }


//        FindMaxFromExcel findMaxFromExcel = new FindMaxFromExcel();
//        double maxCutsignal = findMaxFromExcel.findMaxCutSignal(arr2);
//        System.out.println("maxCutsignal  "  + (maxCutsignal*5.061/1000));
    }

    private int fn, hn = 0;
    private int fi, fo = 0;
    private float sf = 0;                    // real
    private int i, k = 0;
    private double pod, dir = 0;
    private double fre, ave, per = 0;
    private double centr, wt = 0;
    private int c, n, br, ic = 0;
    private double fg = 0;                    //real;
    private double om, shift = 0;
    private double qr = 0;
    private int qn = 0;
    private double phase = 0;
    private double Min, Max, Limit = 0;

    private double[] pos = new double[100];
    // signal complex components
    private double[] xc = new double[3000];
    private double[] xs = new double[3000];
    // transform  omplex components
    private double[] fc = new double[3000];
    private double[] fs = new double[3000];
    // absolute transform
    private double[] fm = new double[3000];
    private double[] fp = new double[3000];


    public double runAppNew(int[] sg) {
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
        double Xi = 0;
        double Yi = 0;
        double XiXi = 0;
        double XiYi = 0;
        int i, N;
        double a, b;
        double[] regLine = new double[3000];
        double[] alignedDate = new double[3000];
        double Min = 65535;
        double Max = 0;
        N = sg.length;
        int xn = sg.length;

        for (i = 0; i < N; i++) {
            Xi = Xi + i;
            Yi = Yi + sg[i];
            XiXi = XiXi + i * i;
            XiYi = XiYi + i * sg[i];
        }
        a = (Yi * XiXi - Xi * XiYi) / (N * XiXi - Xi * Xi); //  n_i=a+b*x_i
        b = (N * XiYi - Xi * Yi) / (N * XiXi - Xi * Xi);    //  n_i=a+b*x_i
        for (i = 0; i < N; i++) {
            regLine[i] = a + b * i;
        }

        for (i = 0; i < N; i++) {
            xc[i] = sg[i] - regLine[i];
            if (xc[i] < Min) {
                Min = xc[i];
            }
            if (xc[i] > Max) {
                Max = xc[i];
            }
        }

//        readAndWriteInExel.write(xc, 0, 0, NameFile.FILE_NAME_WRITE);
        Limit = 30;   // kontrok amplituda
        if ((Max - Min) < Limit) {
            return 0; // лучше выбросить исключение
        }
        // center of weight seek
        fre = 0;
        ave = 0;
        for (i = 0; i < N; i++) {
            fre = fre + xc[i] * xc[i] * i;
            ave = ave + xc[i] * xc[i];
        }
        centr = fre / ave;
//        System.out.println("центр масс выровненного сигнала centr = " + centr);
        c = (int) Math.round(centr);

        dir = centr;
// halfperiod estimation
        while (xc[c] * xc[c - 1] > 0) {
            c++;
        }
        n = 1;
        while (xc[c + n] * xc[c + n - 1] > 0) {
            n++;
        }
        c = (int) Math.round(centr);


        // quadrature demodulator
        qr = (double) n / 2 - (int) (n / 2); // дробная часть числа
        qn = (int) (n / 2);
        for (i = qn + 1; i < N - qn - 1; i++) {
            xs[i] = (xc[i] * xc[i] + app(qr, xc[i + qn], xc[i + qn + 1]) * app(qr, xc[i + qn], xc[i + qn + 1]) / 2 +
                    app(1 - qr, xc[i - qn - 1], xc[i - qn]) * app(1 - qr, xc[i - qn - 1], xc[i - qn]) / 2) / 10;
        }
// readAndWriteInExel.write(xs, 4, 0, NameFile.FILE_NAME_WRITE);
// center of weight seek second iteration
        n = 1;
        ave = 0;
        fre = 0;
        // 450 ms
        for (i = 0; i < xn; i++) {
            fre = fre + xs[i] * i;
            ave = ave + xs[i];
        }
        centr = fre / ave;
//        System.out.println("центр масс демодулированного сигнала centr= " + centr);
        c = (int) Math.round(centr);
        // 460 ms
        // range estimation
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
        c = (int) Math.round(c + wt - n / 2.0);
        wt = wt + n / 2.0;
        br = (int) Math.round(wt * 2.7 / 2.0);

        // 470 ms
        // period calculation---------------------------------

        n = 0;
        for (i = -br + 1; i < br + 1; i++) {
            if ((c + i + 1) < xn && (c + i + 1) >= 0) { // kontrol
                if (xc[c + i + 1] * xc[c + i - 1] < 0) {
                    pos[n] = i + (xc[c + i - 1] + xc[c + i + 1]) / (xc[c + i - 1] - xc[c + i + 1]);
                    n++;
                }
                if (n > 1) {
                    xs[c + i] = (pos[n - 1] - pos[n - 2]) / 24.0;
                }
            }
        }
        n--;
        per = 0;
        ave = 0;
        k = 0;
        for (i = 1; i < n; i++) {
            if ((pos[i] - pos[i - 1]) > 2) {
                k++;
                pos[k] = pos[i];
            } else {
                pos[k] = (pos[i] + pos[k]) / 2;
            }
        }
        n = k;
//        ------------------------------------------------------
        per = 0;
        ave = 0;
        for (i = 1; i < n; i++) {
            if ((pos[i] - pos[i - 1]) > 2) {
                per = per + 1.0 * (pos[i] - pos[i - 1]);
                ave = ave + 1;
            }
        }
        per = per / ave;
        fre = per;
        per = 0;
        ave = 0;
        for (i = 1; i < n; i++) {
            if ((pos[i] - pos[i - 1]) > fre * 0.5) {
                per = per + 1.0 * (pos[i] - pos[i - 1]);
                ave = ave + 1;
            }
        }
        per = per / ave;
        phase = (pos[n / 2] + c);
        if ((c - phase) > per / 2) {
            phase = phase + per;
        }

        ic = 0;
        ave = Math.abs(pos[1]);
        for (i = 1; i < n; i++) {
            if (Math.abs(pos[i]) < ave) {
                ave = Math.abs(pos[i]);
                ic = i;
            }
        }
        phase = 0;
        ave = 0;
        for (i = 0; i < n / 2 - 1; i++) {
            if (i <= ic && i <= (n - ic)) {
                ave = ave + 1;
                phase = phase + (pos[ic + i] + pos[ic - i]) / 2;
            }
        }
        if (ave < 0 || ave > 0) {
            phase = c + phase / ave;
        } else {
            phase = c;
        }
//        System.out.println(phase);
//        System.out.println(per);
//        double Env = (0.561186 / 4) * phase / per; // результат в микрометрах
        double Env = phase; // результат в микрометрах
        return Env;
    }

    private double app(double dw, double a, double b) {
        double app;
        return app = a * dw + b * (1 - dw);
    }

}
