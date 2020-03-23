import java.io.File;

public class FindMaxFromExcel {
    public static void main(String[] args) {
        File dir = new File("C:\\Users\\Tanya\\Downloads\\ско по 10 точкам 3 ряда\\сигнал в точках\\ряд y=300\\xlsx"); //path указывает на директорию
        String FILE_NAME_WRITE = "C:\\Users\\Tanya\\Downloads\\ско по 10 точкам 3 ряда\\сигнал в точках\\ряд y=300\\результат y300.xlsx";
        File[] arrFiles = dir.listFiles();
        double arrOfMax [] = new double [arrFiles.length];
        double centDemSignal [] = new double [arrFiles.length];
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
        FindMaxFromExcel findMaxFromExcel = new FindMaxFromExcel();
        CenterDemodSignal centerDemodSignal = new CenterDemodSignal();

        for (int i = 0; i < arrFiles.length; i++) {
            String filename = arrFiles[i].toString();
            Double arr[] = readAndWriteInExel.read(0, filename);
            double [] doubleArr = new double[arr.length];
            for (int j = 0; j <doubleArr.length ; j++) {
                doubleArr [j] = arr[j];
            }
            double max = findMaxFromExcel.findMaxCutSignal(doubleArr);
            double maxDemod =centerDemodSignal.centerOfWeightSeek(arr);
            arrOfMax [i] = max;
            centDemSignal [i] = maxDemod;
            System.out.println(filename + " : " + "maxХ= " + max);
        }
        readAndWriteInExel.write(arrOfMax,2, 0, FILE_NAME_WRITE);
        readAndWriteInExel.write(centDemSignal,4, 0, FILE_NAME_WRITE);

    }

    public double findMaxCutSignal(double[] arr) {
//        1. Нашли среднєє по массиву и от массива отняли среднеє
        double average = 0;
        int length = arr.length;
        for (int i = 0; i < length; i++) {
            average = average + (arr[i] / length);
        }
        double arrMinusAverage[] = new double[length];
        for (int i = 0; i < length; i++) {
            arrMinusAverage[i] = arr[i] - average;
        }
//        2. Нашли координаты maxX  maxY всего цуга
        double maxY = arrMinusAverage[0];
        int maxX = 0;
        for (int i = 0; i < arrMinusAverage.length - 1; i++) {
            if (arrMinusAverage[i + 1] > maxY) {
                maxY = arrMinusAverage[i + 1];
                maxX = i + 1;
            }
        }
//        System.out.println("maxX= " + maxX + ";  maxY= " + maxY);
//________________  3. Вырезали участок цуга от максимума влево и вправо
        int count = 0;
        for (int i = maxX; i > 1; i--) {
            if (arrMinusAverage[i] > 0) {
                count += 1;
            } else {
                break;
            }
        }
        int andArr = 0;
        for (int i = maxX - count + 1; i < arrMinusAverage.length; i++) {
            if (arrMinusAverage[i] > 0) {
                andArr += 1;
            } else {
                break;
            }
        }
        int index = 0;
        double cutArrX[] = new double[andArr];
        double cutArrY[] = new double[andArr];
        for (int i = maxX - count + 1; i < arrMinusAverage.length; i++) {
            if (arrMinusAverage[i] > 0) {
                cutArrX[index] = i;
                cutArrY[index] = arrMinusAverage[i];
                index += 1;
            } else {
                break;
            }
        }
//        for (int i = 0; i < cutArrX.length; i++) {
//            System.out.println(cutArrX[i] + "    " + cutArrY[i]);
//        }
//____________ 4. Находим коэффициенты линии тренда 2-го порядка. Коэфф. регресии
        Koef koef = new Koef();
        double max = koef.getMaxAndKoeffRegres(cutArrX, cutArrY);
        return max;
    }
}
