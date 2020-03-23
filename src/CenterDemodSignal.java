import java.io.File;

public class CenterDemodSignal {
    public static void main(String[] args) {

    }


    public double centerOfWeightSeek(Double sg[]) {
//1_________________________________________________________________________________________
        int xn = sg.length;
        double[] xc = new double[xn];
        double[] xs = new double[xn];
        double pod = 0;
        for (int i = 0; i < xn; i++) {
            pod = pod + sg[i] / xn;
        }
//2_________________________________________________________________________________________
        for (int i = 0; i < xn; i++) {
            xc[i] = (sg[i] - pod);
            xs[i] = 0; ///                                                                              Зачем присваивать нули??? Убрать!
        }
        double fre = 0;
        double ave = 0;
        for (int i = 0; i < xn; i++) {
            fre = fre + xc[i] * xc[i] * i;
            ave = ave + xc[i] * xc[i];
        }
        double centr = fre / ave;
//3_________________________________________________________________________________________
        int c = (int) Math.round(centr);
        double dir = centr;   // нигде не используется
        while (xc[c] * xc[c - 1] > 0) {
            c++;
        }
        int n = 1;
        while (xc[c + n] * xc[c + n - 1] > 0) {
            n++;
        }
        c = (int) Math.round(centr);
//4_________________________________________________________________________________________
        // quadrature demodulator      квадратурный демодулятор
        double qr = (double) n / 2 - n / 2; // дробная часть числа
        int qn = (int) (n / 2);
        for (int i = qn + 1; i < xn - qn - 1; i++) {
            xs[i] = (xc[i] * xc[i] + app(qr, xc[i + qn], xc[i + qn + 1]) * app(qr, xc[i + qn], xc[i + qn + 1]) / 2 +
                    app(1 - qr, xc[i - qn - 1], xc[i - qn]) * app(1 - qr, xc[i - qn - 1], xc[i - qn]) / 2) / 10;
        }
//                readAndWriteInExel.write(xs,5,0, NameFile.FILE_NAME_WRITE);
//5_________________________________________________________________________________________
        // centr of weight seek second iteration   центр веса искать вторую итерацию
        n = 1;
        ave = 0;
        fre = 0;
        for (int i = 0; i < xn; i++) {
            fre = fre + xs[i] * i;
            ave = ave + xs[i];
        }
        centr = fre / ave;


        double maxY = xs[0];
        int maxX = 0;
        for (int i = 0; i < xs.length - 1; i++) {
            if (xs[i + 1] > maxY) {
                maxY = xs[i + 1];
                maxX = i + 1;
            }
        }
        return centr;
    }




    private static double app(double dw, double a, double b) {
        double app;
        return app = a * dw + b * (1 - dw);
    }

}
