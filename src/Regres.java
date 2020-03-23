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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Regres {
    public Regres() {
    }

    public static void main(String[] args) {
        Regres regres = new Regres();
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();

        Double[] arr = readAndWriteInExel.read(0, NameFile.FILE_NAME_READ);
        double alignedDate[] = regres.getAlignedDate(arr);
        regres.printMaxOfArr(alignedDate);
//        regres.writeAllMaxOfArr(alignedDate, 6, 0, NameFile.FILE_NAME_READ);
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

}
