import java.io.FileWriter;
import java.io.IOException;

public class FindMaxScaleInterval {

    public static void main(String[] args) {
        ReadBinFile readBinFile = new ReadBinFile();
        FindMaxScaleInterval findMaxScaleInterval = new FindMaxScaleInterval();

        try (FileWriter writer = new FileWriter(NameFile.FILE_NAME_WRITE_TXT, false)) {
            long start = System.currentTimeMillis();
//_________________________________________________________________________________________________
            int Y1 = 300;
            int Y2 = 301;  //480
            int X1 = 1;
            int X2 = 640;    //640
            double sum = 0;
            for (int koordY = Y1; koordY < Y2; koordY++) {
                for (int koordX = X1; koordX < X2; koordX++) {
                    int[] arr = readBinFile.readPointFromBinFile(koordX, koordY, NameFile.BIN_FILE_NAME_READ);
                    int N = arr.length;
                    double result = findMaxScaleInterval.getScaleInterval(arr, N);
                    sum = sum + result;
//                    System.out.println("X: " + koordX + " Y: " + koordY + "  " + result);
//                    System.out.println(result);
                }
            }
            double scaleInterval = sum / ((Y2 - Y1) * (X2 - X1));
            System.out.println(scaleInterval);
//__________________________________________________________________________________________________

            for (int koordY = 300; koordY < 301; koordY++) { //480
                for (int koordX = 0; koordX < 640; koordX++) {  //640
                    int[] arr = readBinFile.readPointFromBinFile(koordX, koordY, NameFile.BIN_FILE_NAME_READ);
                    int N1 = arr.length;
//                    double result = findMaxScaleInterval.EnvMax(arr, N); // <- коэф средний для всего кадра
                    double result = scaleInterval*findMaxScaleInterval.EnvMax(arr, N1); // <- коэф средний для всего кадра
                    writer.write(koordX + " " + koordY + " " + result + "\r\n");
                    System.out.println("X: " + koordX + " Y: " + koordY + "  " + result);
//                    System.out.println(result);
                }

            }
            long end = System.currentTimeMillis();
            System.out.println("Time for 11 point (sec)= " + (end - start) / 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //_______________________________________________________________________________________________________________________________________________________________________
    double[] xc = new double[3000];
    double[] regLine = new double[3000];
    int startIndex = 0;
    int limit = 30;
    int N = 0;
    int periodsCount = 7;
    double wavelength = 0.561186;


    public double EnvMax(int[] sg, int xn) {
        int i = 0;
        N = xn;
        if (alignDataAndControlAmplitude(sg) == 0) {
            return 0;
        }
        int center = getCenterMass(xc);
        int maxX = findNearestMax(center);
        int count = countPointInMaxHalfPeriod(maxX);
        if (count <= 2 || count > 60) {
            return 0;
        }
        double[] cutArrX = new double[count];
        double[] cutArrY = new double[count];
        for (i = 0; i < count; i++) {
            cutArrX[i] = startIndex + i;
            cutArrY[i] = xc[startIndex + i];
        }
        double EnvMax = getMaxAndKoeffRegres(cutArrX, cutArrY);
        return EnvMax; //mkm
    }

    public double getScaleInterval(int[] sg, int xn) {
        int i = 0;
        N = xn;
        if (alignDataAndControlAmplitude(sg) == 0) {
            return 0;
        }
        int center = getCenterMass(xc);
        int maxX = findNearestMax(center);
        int count = countPointInMaxHalfPeriod(maxX);
        if (count <= 2 || count > 60) {  // partial filtering of bad points
            return 0;
        }
        double distance = getDistance(xc, center, periodsCount);
        double scaleInterval = (periodsCount * wavelength) / (2 * distance); // mkm
        return scaleInterval;
    }

    private int alignDataAndControlAmplitude(int[] sg) {
        double x = 0;
        double y = 0;
        double xx = 0;
        double xy = 0;
        double min = 65535;
        double max = 0;
        double a, b;
        int i;
        for (i = 0; i < N; i++) {
            x = x + i;
            y = y + sg[i];
            xx = xx + i * i;
            xy = xy + i * sg[i];
        }
        a = (y * xx - x * xy) / (N * xx - x * x); //  n_i=a+b*x_i
        b = (N * xy - x * y) / (N * xx - x * x);    //  n_i=a+b*x_i
        for (i = 0; i < N; i++) {
            regLine[i] = a + b * i;
        }
        for (i = 0; i < N; i++) {
            xc[i] = sg[i] - regLine[i];
            xc[i] = sg[i] - regLine[i];
            if (xc[i] < min) {
                min = xc[i];
            }
            if (xc[i] > max) {
                max = xc[i];
            }
        }
        if ((max - min) < limit) {
            return 0;
        } else {
            return 1;
        }
    }

    private int getCenterMass(double[] xc) {
        int i;
        double fre = 0;
        double ave = 0;
        for (i = 0; i < N; i++) {
            fre = fre + xc[i] * xc[i] * i;
            ave = ave + xc[i] * xc[i];
        }
        int center = (int) (fre / ave);
        return center;
    }

    private int findNearestMax(int center) {
        int i;
        double maxY = 0;
        int maxX = 0;
        for (i = center; i < N - 5; i++) {
            if (xc[i] > maxY && xc[i] > 0) {
                maxY = xc[i];
                maxX = i;
                if (xc[i + 1] < maxY && xc[i + 1] > 0 &&
                        xc[i + 2] < maxY && xc[i + 2] > 0 &&
                        xc[i + 3] < maxY && xc[i + 3] > 0 &&
                        xc[i + 4] < maxY && xc[i + 4] > 0 &&
                        xc[i + 5] < maxY && xc[i + 5] > 0) {
                    break;
                }
            }
        }
        return maxX;
    }

    private int countPointInMaxHalfPeriod(int maxX) {
        int i;
        int count = 0;
        for (i = maxX; i > 1; i--) {
            if (xc[i] > 0) {
                count += 1;
            } else {
                break;
            }
        }
        startIndex = maxX - count + 1;
        count = 0;
        for (i = startIndex; i < N; i++) {
            if (xc[i] > 0) {
                count += 1;
            } else {
                break;
            }
        }
        return count;
    }

    private double getMaxAndKoeffRegres(double x[], double y[]) {
        int i = 2;
        int con = x.length;
        int con1 = 0;
        double dy[] = new double[160];
        double D, Da, Db, Dc, a, b, c, yc, xc, x2, x3, x4, x2c, x3c, x4c, xy, xyc, x2y, x2yc;
        xc = x2 = x3 = x4 = xy = x2y = x2c = x3c = x4c = xyc = x2c = x2yc = yc = 0;
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
//        System.out.println(D);
        i = con1;
        while (i < con + con1) {
            dy[i] = a + b * x[i] + c * x[i] * x[i] - y[i];
            i++;
        }
        double max = -b / (2 * c);
        return max;
    }

    private double getDistance(double[] xc, int center, int periodsCount) {
        int count;
        int i;
        double[][] arr = new double[2][4];
        int countZero;
        double point1;
        double point2;
        countZero = periodsCount + 1; // 8 half period
        count = 1;
        for (i = center; i < N - 6; i++) {
            if (xc[i] * (xc[i + 1]) < 0 &&
                    (xc[i + 1] * xc[i + 2]) > 0 &&
                    (xc[i + 2] * xc[i + 3]) > 0 &&
                    (xc[i + 3] * xc[i + 4]) > 0 &&
                    (xc[i + 4] * xc[i + 5]) > 0 &&
                    (xc[i + 5] * xc[i + 6]) > 0) {
                if (count == countZero) {
                    arr[0][0] = i;
                    arr[0][1] = xc[i];
                    arr[0][2] = i + 1;
                    arr[0][3] = xc[i + 1];
                    break;
                }
                count++;
            }
        }
        countZero = periodsCount;  // 7 half period
        count = 1;
        for (i = center - 1; i < center && i > 6; i--) {
            if (xc[i] * (xc[i + 1]) < 0 &&
                    (xc[i + 1] * xc[i + 2]) > 0 &&
                    (xc[i + 2] * xc[i + 3]) > 0 &&
                    (xc[i + 3] * xc[i + 4]) > 0 &&
                    (xc[i + 4] * xc[i + 5]) > 0 &&
                    (xc[i + 5] * xc[i + 6]) > 0) {
                if (count == countZero) {
                    arr[1][0] = i;
                    arr[1][1] = xc[i];
                    arr[1][2] = i + 1;
                    arr[1][3] = xc[i + 1];
                    break;
                }
                count++;
            }
        }
//        for ( i = 0; i <arr.N ; i++) {
//            for (int j = 0; j < arr[i].N; j++) {
//                System.out.print(arr[i][j] + "           ");
//            }
//            System.out.println();
//        }
        point1 = arr[1][2] - ((arr[1][2] - arr[1][0]) * Math.abs(arr[1][3])) / (Math.abs(arr[1][1]) + Math.abs(arr[1][3]));
        point2 = arr[0][2] - ((arr[0][2] - arr[0][0]) * Math.abs(arr[0][3])) / (Math.abs(arr[0][1]) + Math.abs(arr[0][3]));
        return point2 - point1;
    }

}
