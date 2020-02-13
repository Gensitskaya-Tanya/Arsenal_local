/**
 * Created by vertex on 16.01.2020.
 */
public class MinMax {
    private static final String FILE_NAME_READ = "G:\\Информация по расчету фаз\\тестирование частей кода\\300x300.xlsx";
    private static final String FILE_NAME_WRITE =  "G:\\Информация по расчету фаз\\тестирование частей кода\\300x300.xlsx";

    public static void main(String[] args) {
        double logarifm = Math.log(Math.E);
        double e = Math.E;
        System.out.println(logarifm);
        System.out.println(e);

ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
        Double [] arr1 = readAndWriteInExel.read(6, FILE_NAME_READ);
        double [] arr = new double [arr1.length];
        for (int i = 0; i <arr1.length ; i++) {
            arr[i] = arr1[i];
        }

        double maxValue = getMax(arr);
        double minValue = getMin (arr);
        System.out.println("maxValue = " + maxValue);
        System.out.println("minValue = " + minValue);

        double [] removeArr = new double[arr.length];
        MinMax minMax = new MinMax();
        removeArr = minMax.removeDateFromArr(arr, 30);

        readAndWriteInExel.write(removeArr,8,0,FILE_NAME_WRITE);

    }

    public double [] removeDateFromArr (double [] arr, int limit){
        double [] newArr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            if(arr[i]<limit){
                newArr[i] = 0;
            }else{
                newArr[i]= arr[i];
            }
    }
        return newArr;
    }

    //здесь находим максимум
    public static double getMax(double[] arr) {
        double maxValue = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxValue) {
                maxValue = arr[i];
            }
        }
        return maxValue;
    }

    // здесь находим минимум
    public static double getMin(double[] inputArray) {
        double minValue = inputArray[0];
        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i] < minValue) {
                minValue = inputArray[i];
            }
        }
        return minValue;
    }


}
