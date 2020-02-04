public class GaussFunction {

    private static final String FILE_NAME_READ = "H:\\Информация по расчету фаз\\тестирование частей кода\\test8 31.01.2020_300x300.xlsx";
    private static final String FILE_NAME_WRITE = "H:\\Информация по расчету фаз\\тестирование частей кода\\test8 31.01.2020_300x300.xlsx";


    public static void main(String[] args) {
    ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
    Double [] arr = readAndWriteInExel.read(4,FILE_NAME_READ);
//        for (int i = 0; i <arr.length ; i++) {
//            System.out.println(i + "  =" + arr[i]);
//        }
        GaussFunction gaussFunction = new GaussFunction();
        double [] arrGaussFunction = gaussFunction.getGaussFunction(arr);
//        for (int i = 0; i <arrGaussFunction.length ; i++) {
//            System.out.println(i + "  =" + arrGaussFunction[i]);
//        }

    }

    public double [] getGaussFunction(Double[] arr) {
//      coefficient "C" - curve width or standard deviation
        int matrixLength = arr.length;
        double summaArr = 0;
        for (int i = 0; i < matrixLength; i++) {
            summaArr = summaArr + arr[i];
        }
        double averageValue = summaArr / matrixLength;
        double summa = 0;
        for (int i = 0; i < matrixLength; i++) {
            summa = summa + (arr[i] - averageValue) * (arr[i] - averageValue);
        }
        double c = Math.sqrt(summa / matrixLength);
//         coefficient "A" - peak height
        double a = 1 / (c * Math.sqrt(2 * Math.PI));
//         coefficient "B" - center position ( mathematical expectation)
        double summaXionI = 0;
        for (int i = 0; i < matrixLength; i++) {
            summaXionI = summaXionI + arr[i] * i;
        }
        double b = summaXionI / summaArr;
        double [] arrGaussFunction = new double[matrixLength];

        for (int i = 0; i < matrixLength; i++) {
            arrGaussFunction[i] = a * Math.pow(Math.E, ((-(i - b) * (i - b))) / (2 * c * c));
        }


        System.out.println("koef a = " + a);
        System.out.println("koef b = " + b);
        System.out.println("koef c = " + c);

        return arrGaussFunction;
    }


}
