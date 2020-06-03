public class CheckingSignal {


    public static void main(String[] args) {
        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
        Double[] sg = readAndWriteInExel.read(0, NameFile.FILE_NAME_READ);
        CheckingSignal checkingSignal = new CheckingSignal();
        checkingSignal.runChekingSignal(sg, 40);
    }

    public void runChekingSignal(Double[] sg, int limitForAmplitudeDifference) {
        int xn = sg.length;
        double pod = 0;
        for (int i = 0; i < xn; i++) {
            pod = pod + sg[i] / xn;
        }
        double[] xc = new double[xn];
        for (int i = 0; i < xn; i++) {
            xc[i] = sg[i]-pod;                  // Insert alignment here!!!!!!!!!!!!!!!!!
        }
        double maxValue = 0;
        int indexOfMax = 0;
        for (int i = 0; i < xn; i++) {
            if (xc[i] > maxValue) {
                maxValue = xc[i];
                indexOfMax = i;
            }
        }
        System.out.println(" maxValue = " + maxValue);
        double minValue = 0;
        for (int i = 0; i < xn; i++) {
            if (xc[i] < minValue) {
                minValue = xc[i];
            }
        }
        System.out.println(" minValue = " + minValue);
        if (maxValue - minValue < limitForAmplitudeDifference ){
            System.out.println("maxValue - minValue = " + (maxValue - minValue));
            System.out.println("Error 0 Point is bed! Difference between the maximum and minimum is less than the limit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return;
        }

        int amountPointsOnRightSideFromMax = xn - 1 - indexOfMax;
        int numberIndexLeftFromMax = 0;
        int numberIndexRightFromMax = 0;
        int lenghtOfNewArr = 0;
        if (indexOfMax > amountPointsOnRightSideFromMax) {
            if (amountPointsOnRightSideFromMax < (xn * 5 / 100)) {
                System.out.println("indexOfMax = " +  indexOfMax + "  maxValue = " +maxValue + "  minValue = " + minValue);
                System.out.println("Error 1 Point is bed! The signal is cut off on the right side!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return;
            }
            numberIndexRightFromMax = xn - 1;
            numberIndexLeftFromMax = indexOfMax - amountPointsOnRightSideFromMax;
            lenghtOfNewArr = xn - numberIndexLeftFromMax;
        } else if (indexOfMax < amountPointsOnRightSideFromMax) {
            if (indexOfMax < (xn * 5 / 100)) {
                System.out.println("indexOfMax = " +  indexOfMax + "  maxValue = " +maxValue + "  minValue = " + minValue);
                System.out.println("Error 2 Point is bed! The signal is cut off on the left side!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return;
            }
            numberIndexLeftFromMax = 0;
            numberIndexRightFromMax = 2*indexOfMax;
            lenghtOfNewArr = 2 * indexOfMax + 1;
        } else {
            numberIndexLeftFromMax = 0;
            numberIndexRightFromMax = xn - 1;
            lenghtOfNewArr = xn;
        }
        int[] arrNumberIndex = new int[numberIndexRightFromMax + 1];
        double[] arrValueInCell = new double[numberIndexRightFromMax + 1];


        double[][] arrCentered = new double[lenghtOfNewArr][2];

        int counter = 0;
        for (int i = numberIndexLeftFromMax; i < numberIndexRightFromMax + 1; i++) {
            arrCentered[counter][0] = i;
            arrCentered[counter][1] = xc[i];
            counter++;
        }

//        for (int i = 0; i < arrCentered.N; i++) {
//            System.out.println(arrCentered[i][0] + "    " + arrCentered[i][1]);
//            System.out.println(arrCentered[i][1]);

//        }


        System.out.println("indexOfMax = " + indexOfMax + "   maxValue=" + maxValue + "   amountPointsOnRightSideFromMax= " + amountPointsOnRightSideFromMax + "    numberIndexLeft = " + numberIndexLeftFromMax + "  numberIndexRight = " + numberIndexRightFromMax);


    }
}
