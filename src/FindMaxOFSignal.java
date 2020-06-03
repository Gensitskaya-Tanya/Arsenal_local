import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FindMaxOFSignal {
   private Koef koef = new Koef();
    public static void main(String[] args) {
        ReadBinFile readBinFile = new ReadBinFile();
        Koef koef = new Koef();
        FindMaxOFSignal findMaxOFSignal = new FindMaxOFSignal();
//        int koordY = 401;//koordY 0...479
        double[] arrAllMaxSignalInRowY = new double[640];

        try(FileWriter writer = new FileWriter(NameFile.FILE_NAME_WRITE_TXT, false)) {



            for (int koordY = 10; koordY < 11 ; koordY++) {
                //        calculates from X1 to X2, Y=const
                for (int rangeX = 1; rangeX < 2; rangeX++) {
                    int[] arr = readBinFile.readPointFromBinFile(rangeX, koordY, NameFile.BIN_FILE_NAME_READ);
                    double[] doubleArr = new double[arr.length];
                    for (int i = 0; i < doubleArr.length; i++) {
                        doubleArr[i] = (double) arr[i];
                    }
                    arrAllMaxSignalInRowY[rangeX] = findMaxOFSignal.findMaxCutSignal(doubleArr);
//            if(rangeX == 0){                                                                // проверкой убираем разовые выбросы
//                System.out.println(arrAllMaxSignalInRowY[rangeX]);
////                System.out.println("X: " + rangeX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[rangeX]);
//            }else if( (arrAllMaxSignalInRowY[rangeX]-arrAllMaxSignalInRowY[rangeX-1])<-2 && rangeX-1>0){
//                System.out.println();
////                System.out.println("X: " + rangeX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[rangeX]+ "  выброс");
//            }else{
//                System.out.println(arrAllMaxSignalInRowY[rangeX]);
////                          System.out.println("X: " + rangeX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[rangeX] );
//
//            }
                    System.out.println("X: " + rangeX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[rangeX]);
//                    System.out.println(arrAllMaxSignalInRowY[rangeX]);
//                String text = "Hello Gold!";
                    writer.write(rangeX+ " "+ koordY +" "+ arrAllMaxSignalInRowY[rangeX]+"\r\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
//        readAndWriteInExel.write(arrAllMaxSignalInRowY,2,5,NameFile.FILE_NAME_WRITE);
    }




    public  double findMaxCutSignal(double[] arr) {
//        1. Нашли среднєє по массиву и от массива отняли среднеє
        double [] alignedData = getAlignedData(arr);
        double fre = 0;
        double ave = 0;
        for (int i = 0; i < alignedData.length; i++) {
            fre = fre + alignedData[i] * alignedData[i] * i;
            ave = ave + alignedData[i] * alignedData[i];
        }
        double centr = fre / ave;
//        System.out.println(centr);

//        2. Нашли координаты maxX  maxY всего цуга
        double maxY = alignedData[0];

//        int maxX = 0;
//        for (int i = 0; i < alignedData.N - 1; i++) {
//            if (alignedData[i + 1] > maxY) {
//                maxY = alignedData[i + 1];
//                maxX = i + 1;
//            }
//        }
// Добавила часть кода, можно отменить
        int maxX = (int)centr;
        for (int i = maxX; i < alignedData.length - 1; i++) {
            if (alignedData[i + 1] > maxY) {
                maxY = alignedData[i + 1];
                maxX = i + 1;
            }
        }
//        System.out.println(maxX);
//        System.out.println("maxX= " + maxX + ";  maxY= " + maxY);

//      3. Вырезали участок цуга от максимума влево и вправо
        int count = 0;
        for (int i = maxX; i > 1; i--) {
            if (alignedData[i] > 0) {
                count += 1;
            } else {
                break;
            }
        }
        int andArr = 0;
        for (int i = maxX - count + 1; i < alignedData.length; i++) {
            if (alignedData[i] > 0) {
                andArr += 1;
            } else {
                break;
            }
        }
        int index = 0;
        double cutArrX[] = new double[andArr];
        double cutArrY[] = new double[andArr];
        for (int i = maxX - count + 1; i < alignedData.length; i++) {
            if (alignedData[i] > 0) {
                cutArrX[index] = i;
                cutArrY[index] = alignedData[i];
                index += 1;
            } else {
                break;
            }
        }
//        for (int i = 0; i < cutArrX.N; i++) {
//            System.out.println(cutArrX[i] + "    " + cutArrY[i]);
//        }
//____________ 4. Находим коэффициенты линии тренда 2-го порядка. Коэфф. регресии
        double max = koef.getMaxAndKoeffRegres(cutArrX, cutArrY);
        int countHighs = 9;
        double wavelength = 0.561186/2; // mkm
        int countPoint = 470;
        double koef = (countHighs*wavelength)/(countPoint);
        return max*koef;
    }

    private double [] getAlignedData(double [] arr){
        double average = 0;
        int length = arr.length;
        for (int i = 0; i < length; i++) {
            average = average + (arr[i] / length);
        }
        double arrMinusAverage[] = new double[length];
        for (int i = 0; i < length; i++) {
            arrMinusAverage[i] = arr[i] - average;
        }
        return arrMinusAverage;
    }

}
