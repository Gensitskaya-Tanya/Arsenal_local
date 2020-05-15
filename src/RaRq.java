import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;

public class RaRq {
    public static void main(String[] args) {
        RaRq raRq = new RaRq();
        raRq.printRaRq();
    }

    public  void printRaRq(){
        double[][] data = readTXTToARR(NameFile.FILE_NAME_WRITE_TXT);
        double[][] matrixX = matrixX(data);
        double[] matrixZ = matrixZ(data);


        RealMatrix X = new Array2DRowRealMatrix(matrixX);
        RealMatrix Z = new Array2DRowRealMatrix(matrixZ);

        RealMatrix Xt = X.transpose();
        double[][] Xtr = Xt.getData();
//        raRq.printMatrix(Xtr);
//        System.out.println();

        RealMatrix XtX = Xt.multiply(X);
        double[][] S1 = XtX.getData();
//        raRq.printMatrix(S1);
//        System.out.println();


        RealMatrix XtZ = Xt.multiply(Z);
        double[][] S2 = XtZ.getData();
//        raRq.printMatrix(S2);
//        System.out.println();


        RealMatrix inv = new LUDecomposition(XtX).getSolver().getInverse();
        double[][] S1inv = inv.getData();
//        raRq.printMatrix(S1inv);
//        System.out.println();


        RealMatrix bmul = inv.multiply(XtZ);
        double[][] b = bmul.getData();
//        raRq.printMatrix(b);
//        System.out.println();
//        System.out.println();


        double[][] alingMatrix = alignDate(data, b, matrixZ);
//        raRq.printMatrix(alingMatrix);
        System.out.println("Ra=  " + getRa(alingMatrix) + " (nm)");
        System.out.println("Rq=  " + getRq(alingMatrix) + " (nm)");
    }

    public double getRq(double[][] alingMatrix) {
        double sum = 0;
        for (int i = 0; i < alingMatrix.length; i++) {
            sum = sum + Math.pow(alingMatrix[i][2], 2);
        }
        return Math.sqrt(sum / alingMatrix.length);
    }

    public double getRa(double[][] alingMatrix) {
        double sum = 0;
        for (int i = 0; i < alingMatrix.length; i++) {
            sum = sum + Math.abs(alingMatrix[i][2]);
        }
        return sum / alingMatrix.length;
    }

    public double[][] alignDate(double[][] data, double[][] b, double[] matrixZ) {
        double[][] alingMatrix = new double[data.length][data[0].length];
        for (int i = 0; i < alingMatrix.length; i++) {
            alingMatrix[i][0] = data[i][0];
            alingMatrix[i][1] = data[i][1];
            alingMatrix[i][2] = matrixZ[i] - (b[0][0] + b[1][0] * data[i][0] + b[2][0] * data[i][1]);
        }
        return alingMatrix;
    }


    public double[][] matrixX(double[][] matrix) {
        double[][] matrixX = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrixX[i][0] = 1;
                matrixX[i][1] = matrix[i][0];
                matrixX[i][2] = matrix[i][1];
            }
        }
        return matrixX;
    }

    public double[] matrixZ(double[][] matrix) {
        double[] matrixZ = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            matrixZ[i] = matrix[i][2] * 1000; // * 1000 - mkm to nm
        }
        return matrixZ;
    }


    public double[][] readTXTToARR(String fileName) {
        double[][] data = new double[640 * 480][3];
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            int count = 0;
            String line = reader.readLine();
            while (line != null) {
                String[] arr = line.split(" ");
                for (int i = 0; i < 3; i++) {
                    data[count][i] = (double) Double.valueOf(arr[i]);
                }
                line = reader.readLine();
                count++;
            }
            fr.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void printMatrix(double[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + "  ");
            }
            System.out.println();
        }
    }


    public void transposeMatrix(double[][] matrix, double[][] transMatrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                transMatrix[j][i] = matrix[i][j];
            }
        }
    }


    public void matrixMultiplication(double[][] matrixA, double[][] matrixB, double[][] mulMatrix) {
        if (matrixA[0].length == matrixB.length) {
            double sum = 0;
            for (int i = 0; i < matrixA.length; i++) {
                for (int j = 0; j < matrixB[0].length; j++) {
                    for (int k = 0; k < matrixB.length; k++) {
                        sum = sum + matrixA[i][k] * matrixB[k][j];
                    }
                    mulMatrix[i][j] = sum;
                    sum = 0;
                }
            }
        } else {
            System.out.println("ERROR: matrixA[0].length should be = matrixB.length!!!");
        }
    }


}
