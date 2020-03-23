public class Koef {
    public static void main(String[] args) {
        double y[] = {77.878,
        216.878,
        352.878,
        498.878,
        584.878,
        706.878,
        823.878,
        916.878,
        1.01e3,
        1.093e3,
        1.126e3,
        1.182e3,
        1.211e3,
        1.222e3,
        1.208e3,
        1.235e3,
        1.212e3,
        1.16e3,
        1.129e3,
        1.058e3,
        970.878,
        900.878,
        761.878,
        652.878,
        571.878,
        456.878,
        348.878,
        225.878,
        94.878,
        7.878};


    double x[] = {629,
            630,
            631,
            632,
            633,
            634,
            635,
            636,
            637,
            638,
            639,
            640,
            641,
            642,
            643,
            644,
            645,
            646,
            647,
            648,
            649,
            650,
            651,
            652,
            653,
            654,
            655,
            656,
            657,
            658};

            Koef koef = new Koef();
        double max = koef.getMaxAndKoeffRegres(x, y);
        System.out.println(max);
    }

    public double getMaxAndKoeffRegres(double x[], double y[]) {
        int i = 2;
        int con = x.length;
        int con1 = 0;
        double dy[] = new double[160];
        double D, Da, Db, Dc, a, b, c, yc, xc, x2, x3, x4, x2c, x3c, x4c, xy, xyc, x2y, x2yc;
        xc = x2 = x3 = x4 = xy = x2y = x2c = x3c = x4c = xyc = x2c = x2yc = yc = .0;
        for (i = con1; i < con + con1; i++) {
            yc += y[i];
            xc += x[i];
            x2 = Math.pow(x[i], 2);
            x2c += x2;
            x3 = Math.pow(x[i], 3);
            x3c += x3;
            x4 = Math.pow(x[i], 4);
            x4c += x4;
            xy = x[i] * y[i];
            xyc += xy;
            x2y = x2 * y[i];
            x2yc += x2y;
        }
        x2c /= con;
        yc /= con;
        xc /= con;
        x3c /= con;
        x4c /= con;
        xyc /= con;
        x2yc /= con;
        D = x4c * x2c - Math.pow(x3c, 2) - xc * (xc * x4c - x2c * x3c) + x2c * (xc * x3c - Math.pow(x2c, 2));
        Da = yc * (x2c * x4c - Math.pow(x3c, 2)) - xyc * (xc * x4c - x3c * x2c) + x2yc * (xc * x3c - Math.pow(x2c, 2));
        Db = xyc * x4c - x2yc * x3c - xc * (yc * x4c - x2yc * x2c) + x2c * (yc * x3c - x2c * xyc);
        Dc = x2c * x2yc - x3c * xyc - xc * (xc * x2yc - x3c * yc) + x2c * (xc * xyc - x2c * yc);
        a = Da / D;
        b = Db / D;
        c = Dc / D;
        i = con1;
        while (i < con + con1) {
            dy[i] = a + b * x[i] + c * x[i] * x[i] - y[i];
            if (Math.abs(dy[i]) > 0.01) {
//                System.out.println(i + "  " + dy[i]);
            }
            i++;
        }
//        System.out.println((x[con1]) + " " + con + " " + a + " " + b + " " + c);
//        System.out.println("координата максимума: " + (-b / (2 * c)));
        double max = -b / (2 * c);
        return max;
    }


}




