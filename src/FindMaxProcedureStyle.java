import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class FindMaxProcedureStyle {

    public static void main(String[] args) {
        ReadBinFile readBinFile = new ReadBinFile();
        FindMaxProcedureStyle findMaxProcedureStyle = new FindMaxProcedureStyle();
        RaRq raRq = new RaRq();
//      koordY = 0...479;
//      koordX = 0...639;
        double[] arrAllMaxSignalInRowY = new double[640];
//        int Y1 = 0;
//        int Y2 = 480;
//        int X1 = 0;
//        int X2 = 640;

//        int arrLength = (Y2-Y1)*(X2-X1);
//        double [][] arrHight = new double[arrLength][3];

//        int count0 = 0;
        try (FileWriter writer = new FileWriter(NameFile.FILE_NAME_WRITE_TXT, false)) {
            long start = System.currentTimeMillis();
            for (int koordY = 300; koordY < 301; koordY++) {
                for (int koordX = 0; koordX < 640; koordX++) {

//                    short[] arr = readBinFile.readPointFromBinFileToShort(koordX, koordY, NameFile.BIN_FILE_NAME_READ);
                    int [] arr = readBinFile.readPointFromBinFile(koordX, koordY, NameFile.BIN_FILE_NAME_READ);

                    double result = findMaxProcedureStyle.EnvMax(arr); // <- method
//                    arrHight[count0][0] = koordX;
//                    arrHight[count0][1] = koordY;
//                    arrHight[count0][2] = result;
//                    arrAllMaxSignalInRowY[koordX] = result;
//                    if(koordX == 0){                                                                // проверкой убираем разовые выбросы
//                      System.out.println(arrAllMaxSignalInRowY[koordX]);
////                        System.out.println("X: " + koordX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[koordX]);
//                     }else if( (arrAllMaxSignalInRowY[koordX]-arrAllMaxSignalInRowY[koordX-1])<-2 && koordX-1>0){
//                      System.out.println();
////                        System.out.println("X: " + koordX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[koordX]+ "  выброс");
//                     }else{
//                      System.out.println(arrAllMaxSignalInRowY[koordX]);
////                        System.out.println("X: " + koordX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[koordX] );
//                     }


                    writer.write(koordX + " " + koordY + " " + result + "\r\n");
//                    System.out.println("X: " + koordX + " Y: " + koordY + "  " + result);
                    System.out.println(result);
//                    count0++;
                }

            }
            long end = System.currentTimeMillis();
            System.out.println("Time for 11 point (sec)= " + (end-start)/1000);
//            for (int i = 0; i <arrHight.length ; i++) {
//                for (int j = 0; j <arrHight[i].length ; j++) {
//                    writer.write(arrHight [i][j] + " ");
//                    System.out.print(arrHight [i][j] + " ");
//                }
//                writer.write( "\r\n");
//                System.out.println();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        raRq.printRaRq();

    }

    public double EnvMax(int [] sg) {
        double Limit = 0;
        double x = 0;
        double y = 0;
        double xx = 0;
        double xy = 0;
        int N = sg.length;
        int i;
        double a, b;
        double[] xc = new double[3000];
        double[] regLine = new double[3000];
        double Min = 65535;
        double Max = 0;
        int maxX = 0;
        int count = 0;

//Find the regression line____________________________________________
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


//        for (int j = 0; j <N ; j++) {
//            System.out.println(regLine[j]);
//        }
//Align data_____________________________________
        for (i = 0; i < N; i++) {
            xc[i] = sg[i] - regLine[i];
            if (xc[i] < Min) {
                Min = xc[i];
            }
            if (xc[i] > Max) {
                Max = xc[i];
                maxX = i;
            }
        }

//        System.out.println("Min = "+ Min + " Max= " +Max + " " + maxX);
//Control amplitude_____________________________
        Limit = 30;
        if ((Max - Min) < Limit) {
            return 0;
        }


// Center of mass_______________________________
        double fre = 0;
        double ave = 0;
        for (i = 0; i < N; i++) {  // Xc to N
            fre = fre + xc[i] * xc[i] * i;
            ave = ave + xc[i] * xc[i];
        }
        int centr = (int) (fre / ave);
//        System.out.println(centr + "  fre: " + fre + "  ave:  "+ ave);

// Find the nearest maximum______________________
//        double max1Y = 0;
//        int max1X = 0;
//        for (i = centr - 1; i < centr && i > 5; i--) {
//            if (xc[i] > max1Y && xc[i] > 0) {
//                max1Y = xc[i];
//                max1X = i;
//                if (    xc[i - 1] < max1Y && xc[i - 1] > 0 &&
//                        xc[i - 2] < max1Y && xc[i - 2] > 0 &&
//                        xc[i - 3] < max1Y && xc[i - 3] > 0 &&
//                        xc[i - 4] < max1Y && xc[i - 4] > 0 &&
//                        xc[i - 5] < max1Y && xc[i - 5] > 0)
//                {
//                    break;
//                }
//            }
//        }
//        System.out.println("centr " + centr + " max1X =" + max1X);
        double max2Y = 0;
        int max2X = 0;
        for (i = centr; i < N-5; i++) {
            if (xc[i] > max2Y && xc[i] > 0) {
                max2Y = xc[i];
                max2X = i;
                if (    xc[i + 1] < max2Y && xc[i + 1]>0 &&
                        xc[i + 2] < max2Y && xc[i + 2]>0 &&
                        xc[i + 3] < max2Y && xc[i + 3]>0 &&
                        xc[i + 4] < max2Y && xc[i + 4]>0 &&
                        xc[i + 5] < max2Y && xc[i + 5]>0)
                {
                    break;
                }
            }
        }

//        System.out.println("centr " + centr + " max2X =" + max2X);
//        if (Math.abs(centr - max1X) >= Math.abs(centr - max2X)) {
//            maxX = max2X;
//        } else {
//            maxX = max1X;
//        }

         maxX = max2X;
//Cut out the maximum half period
        count = 0;
        for (i = maxX; i > 1; i--) {
            if (xc[i] > 0) {
                count += 1;
            } else {
                break;
            }
        }
        int startIndex = maxX - count + 1;
        count = 0;
        for (i = startIndex; i < N; i++) {
            if (xc[i] > 0) {
                count += 1;
            } else {
                break;
            }
        }
//        System.out.println("count " + count);
        if (count<=2 || count > 50){
          return 0;
        }
        double[] cutArrX = new double[count];
        double[] cutArrY = new double[count];
        for (i = 0; i < count; i++) {
            cutArrX[i] = startIndex + i;
            cutArrY[i] = xc[startIndex + i];
        }


//        for (i = 0; i < cutArrX.length; i++) {
//            System.out.println(cutArrX[i] + "    " + cutArrY[i]);
//        }

//____________ 4. Находим коэффициенты линии тренда 2-го порядка. Коэфф. регресии
        int countHighs = 9;
        double wavelength = 0.561186 / 2; // mkm
        int countPoint = 470;
        double koef = (countHighs * wavelength) / (countPoint);
        double EnvMax = getMaxAndKoeffRegres(cutArrX, cutArrY);
        return EnvMax; //mkm
    }


    static double getMaxAndKoeffRegres(double x[], double y[]) {
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
