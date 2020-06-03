import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class Regres {
    public Regres() {
    }

    public static void main(String[] args) {
        Regres regres = new Regres();
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();

        Double[] arr = readAndWriteInExel.read(0, NameFile.FILE_NAME_READ);
        double alignedDate[] = regres.getAlignedDate(arr);
        regres.printMaxOfArr(alignedDate);
        regres.writeAllMaxOfArr(alignedDate, 12, 0, NameFile.FILE_NAME_WRITE);
//        readAndWriteInExel.write(alignedDate, 2, 0, NameFile.FILE_NAME_WRITE);


    }

    public double[] getAlignedDate(Double[] arr) {
        int N = arr.length;
        double Xi = 0;
        double Yi = 0;
        double XiXi = 0;
        double XiYi = 0;
        for (int i = 0; i < N; i++) {
            Xi = Xi + i;
            Yi = Yi + arr[i];
            XiXi = XiXi + i * i;
            XiYi = XiYi + i * arr[i];
        }
        double a = (Yi * XiXi - Xi * XiYi) / (N * XiXi - Xi * Xi); // коэффициент сдвига (а), уравнение регресии n_i=a+b*x_i
        System.out.println("a = " + a);
        double b = (N * XiYi - Xi * Yi) / (N * XiXi - Xi * Xi); // коєфф. наклона (b),  уравнение регресии n_i=a+b*x_i
        System.out.println("b = " + b);

        double[] regLine = new double[N];
        for (int i = 0; i < N; i++) {
            regLine[i] = a + b * i;
        }
        double[] alignedDate = new double[N];
        for (int i = 0; i < N; i++) {
            alignedDate[i] = arr[i] - regLine[i];
        }
        return alignedDate;
    }

    public void printMaxOfArr(double arr[]) {
        double maxY = arr[0];
        int maxX = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i + 1] > maxY) {
                maxY = arr[i + 1];
                maxX = i + 1;
            }
        }
        System.out.println("maxX= " + maxX + ";  maxY= " + maxY);
    }


    public void writeAllMaxOfArr(double arr[], int colNum, int rowNum, String FILE_NAME_WRITE) {
        double[] arrOfmax = new double[arr.length];
        double[] arrOfindex = new double[arr.length];

        int numberIndex = 0;
        double max = 0;
        for (int i = 2; i < arr.length - 3; i++) {
            if (arr[i] >= arr[i - 2] && arr[i] >= arr[i - 1] && arr[i] >= arr[i + 1] && arr[i] >= arr[i + 2] && arr[i] > 0) {
                arrOfmax[numberIndex] = arr[i];
                arrOfindex[numberIndex] = i;
                numberIndex++;
            }
            System.out.println(i);
        }
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
        readAndWriteInExel.write(arrOfindex, (colNum - 1), rowNum, NameFile.FILE_NAME_WRITE);
        readAndWriteInExel.write(arrOfmax, colNum, rowNum, NameFile.FILE_NAME_WRITE);
    }

    public void findAllMax(double [] xc, int N){
        int i =0;
        //  Find all max
        double[][] arrOFmax = new double[N][2];

        int numberIndex = 0;
        for (i = 6; i < arrOFmax.length - 8; i++) {
            if (xc[i] > 0&&

                    xc[i] >= xc[i - 6]&&
                    xc[i] >= xc[i - 5]&&
                    xc[i] >= xc[i - 4]&&
                    xc[i] >= xc[i - 3]&&
                    xc[i] >= xc[i - 2]&&
                    xc[i] >= xc[i - 1]&&
                    xc[i] >= xc[i + 1]&&
                    xc[i] >= xc[i + 2]&&
                    xc[i] >= xc[i + 3]&&
                    xc[i] >= xc[i + 4]&&
                    xc[i] >= xc[i + 5]&&
                    xc[i] >= xc[i + 6]){
                arrOFmax[numberIndex][1] = xc[i];
                arrOFmax[numberIndex][0] = i;
//                System.out.println(arrOFmax[numberIndex][0] + " "+arrOFmax[numberIndex][1]);
                numberIndex++;
            }
        }

        Arrays.sort(arrOFmax, new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                return Double.compare(o2[1], o1[1]);
            }
        });
//        for (int j = 0; j <arrOFmax.N ; j++) {
//            for (int k = 0; k <arrOFmax[j].N ; k++) {
//                System.out.print(arrOFmax[j][k] + "   ");
//            }
//            System.out.println();
//        }
        double [][] arrMax9 = new double[9][2];
        for (int j = 0; j < arrMax9.length ; j++) {
            for (int k = 0; k <arrMax9[j].length ; k++) {
                arrMax9[j][k] = arrOFmax[j][k];
            }
        }

//        for (int j = 0; j <arrMax9.N ; j++) {
//            for (int k = 0; k <arrMax9[j].N ; k++) {
//                System.out.print(arrMax9[j][k] + "   ");
//            }
//            System.out.println();
//        }

        Arrays.sort(arrMax9, new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                return Double.compare(o1[0], o2[0]);
            }
        });
//        System.out.println();
//        System.out.println();

        for (int j = 0; j <arrMax9.length ; j++) {
            for (int k = 0; k <arrMax9[j].length ; k++) {
                System.out.print(arrMax9[j][k] + "   ");
            }
            System.out.println();
        }

// Center of mass_______________________________
        double fre = 0;
        double ave = 0;
        int first = (int)arrMax9[0][0];
        int end = (int)arrMax9[8][0];
        System.out.println("к-во точек между 1-м и 9-м максимумом: " + (end-first));
        for (i = first; i < end+1; i++) {
            fre = fre + xc[i] * xc[i] * i;
            ave = ave + xc[i] * xc[i];
        }
        int centr = (int) (fre / ave);
//        System.out.println(centr);
    }
}
