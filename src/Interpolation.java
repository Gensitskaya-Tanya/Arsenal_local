import java.util.Arrays;

public class Interpolation {

    public static void main(String[] args) {
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
        Double[] arrOfMax = readAndWriteInExel.read(2, NameFile.FILE_NAME_READ);
        Double[] arrOfIndex = readAndWriteInExel.read(1, NameFile.FILE_NAME_READ);
        double[][] dl = readAndWriteInExel.correctReadMethod(1, 2, NameFile.FILE_NAME_READ);
        double delta = findDelta(dl);
        double x = dl[1][0];
        double A2 = dl[1][1];
        double p = 5;
        int n = 2;
        double[][] sigma = new double[dl.length][2];
        double[][] interpol = new double[dl.length][2];
        for (int i = 1; i < dl.length; i++) {
            for (int j = n; j < dl.length; j++) {
                if (dl[j][0] - dl[i][0] > 5) break;
                sigma[j][1] = Math.abs(dl[j][0] - x - delta);
                sigma[j][0] = j;
            }
            sigma[i][1] = 1000000;
            double minNum = sigma[0][1];
            for (double[] aSigma1 : sigma) {
                if (aSigma1[1] < minNum) {
                    minNum = aSigma1[1];
                }
            }
            double[][] coordXX = new double[1][2];
            for (double[] aSigma : sigma) {
                if (aSigma[1] == minNum) {
                    coordXX[0][0] = aSigma[0];
                    coordXX[0][1] = aSigma[1];
                }
            }
            interpol[i][0] = x;
            interpol[i][1] = (coordXX[0][1] - A2)/(coordXX[0][0] - x) * delta + A2;

            A2 = interpol[i][1];
            n++;
            x = i * delta;
        }
               for (double[] anInterpol : interpol) {
            System.out.println(anInterpol[1]);

        }
//        interpolation.getInterpolation(arrOfMax, arrOfIndex);
    }

    public static double findDelta(double[][] dl) {
        return (dl[dl.length - 1][0] - dl[0][0]) / ((dl.length - 1) - 1);
    }

    public static double findDelta(Double[] arrOfMax, Double[] arrOfIndex) {
        return (arrOfIndex[arrOfMax.length - 1] - arrOfIndex[0]) / ((arrOfMax.length - 1) - 1);
    }


}
