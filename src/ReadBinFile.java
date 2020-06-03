import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadBinFile {
    private FindMaxFromExcel findMaxFromExcel = new FindMaxFromExcel();
    private int frameX = 640;
    private int frameY = 480;


    public static void main(String[] args) {
        ReadBinFile readBinFile = new ReadBinFile();
        long start = System.currentTimeMillis();
        for (int koordY = 0; koordY < 480; koordY++) {
            for (int koordX = 0; koordX < 640; koordX++) {

                int [] arr = readBinFile.readPointFromBinFile(koordX, koordY, NameFile.BIN_FILE_NAME_READ);
                    System.out.println("X: " + koordX + " Y: " + koordY + "  " + arr.length);

//                for (int i = 0; i <arr.N ; i++) {
//                    System.out.println(i + " " + arr[i]);
//                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Time for 11 point (sec)= " + (end-start)/1000);

//        readBinFile.calculatMaxOfSignalFromMatrix(400,420,400,402, NameFile.BIN_FILE_NAME_READ);
//        double[] arr = readBinFile.calculatMaxOfSignalInRow(300, 320, 300, NameFile.BIN_FILE_NAME_READ);
//        int [] arr2 = readBinFile.readPointFromBinFile(300,300,NameFile.BIN_FILE_NAME_READ);
//        for (int i = 0; i <arr2.N ; i++) {
//            System.out.println(i +" "+arr2[i]);
//        }
//        ReadAndWriteInExel readAndWriteInExel = new ReadAndWriteInExel();
//        readAndWriteInExel.write(arr,0,0,NameFile.FILE_NAME_WRITE);


//readBinFile.readMatrixFromBinFile(0,0,NameFile.BIN_FILE_NAME_READ);

    }

    public double[] calculatMaxOfSignalInRow(int startX, int endX, int constY, String BIN_FILE_NAME_READ) {
        double[] arrAllMaxSignalInRowY = new double[endX-startX];
        int count = 0;
        for (int rangeX = startX; rangeX < endX; rangeX++) {
            int[] arr = readPointFromBinFile(rangeX, constY, NameFile.BIN_FILE_NAME_READ);
            double[] doubleArr = new double[arr.length];
            for (int i = 0; i < doubleArr.length; i++) {
                doubleArr[i] = (double) arr[i];
            }
            arrAllMaxSignalInRowY[count] = findMaxFromExcel.findMaxCutSignal(doubleArr);
//            System.out.println("X: " + rangeX + ", Y: " + constY + "max = " + arrAllMaxSignalInRowY[count]);
            count++;

        }
        return arrAllMaxSignalInRowY;
    }



    public double[] calculatMaxOfSignalFromMatrix(int startX, int endX, int startY, int endY, String BIN_FILE_NAME_READ) {
        //     calculates in matrix from X1 to X2, from Y1 to Y2
        double[] arrAllMaxSignalInRowY = new double[640];
        for (int i = startY; i <= endY; i++) {
            for (int j = startX; j < endX; j++) {
                int[] arr = readPointFromBinFile(j, i, NameFile.BIN_FILE_NAME_READ);
                double[] doubleArr = new double[arr.length];
                for (int k = 0; k < doubleArr.length; k++) {
                    doubleArr[k] = (double) arr[k];
                }
                arrAllMaxSignalInRowY[j] = findMaxFromExcel.findMaxCutSignal(doubleArr);
                System.out.println("X: " + j + ", Y: " + i + "   max = " + arrAllMaxSignalInRowY[j]);
            }
        }
        return arrAllMaxSignalInRowY;
    }

    public int[] readPointFromBinFile(int koordX, int koordY, String fileName) {
        int[] binArr = null;
        try (InputStream inputStream = new FileInputStream(fileName)) {
            int lengthFile = inputStream.available();
            int signalLength = lengthFile / (2 * frameX * frameY);
            inputStream.skip(signalLength * 2 * (frameX * koordY + koordX));

            long start = System.currentTimeMillis();
            binArr = nextIntFromInputStream(inputStream, signalLength);
            long end = System.currentTimeMillis();
//             System.out.println(koordY + " TimeMillis= "  + (end-start)/1000);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return binArr;
    }

    private static int [] nextIntFromInputStream(InputStream is, int  signalLength) throws IOException {
        byte[] buffer = new byte[signalLength*2];
        is.read(buffer);
        int [] binArr = new int[signalLength];
        int count = 0;
        for (int i = 0; i <buffer.length; i=i+2) {
            int sb = buffer[i];
            int fb = buffer[i+1];
            binArr[count] = ((fb & 0xff) << 8) | (sb & 0xff);
            count++;
        }
        return binArr ;
    }





    public short [] readPointFromBinFileToShort(int koordX, int koordY, String fileName) {
        short[] binArr = null;
        try (InputStream inputStream = new FileInputStream(fileName)) {
            int lengthFile = inputStream.available();
//            System.out.println("lengthFile = " + lengthFile);
            int signalLength = lengthFile / (2 * frameX * frameY);
            inputStream.skip(signalLength * 2 * (frameX * koordY + koordX));
            binArr = new short[signalLength];
            for (int i = 0; i < signalLength; i++) {
                binArr[i] = nextShortFromInputStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binArr;
    }

    public void readMatrixFromBinFile(String binFileName) {
        FindMaxProcedureStyle findMaxProcedureStyle = new FindMaxProcedureStyle();
        short[] binArr = null;
        try (InputStream inputStream = new FileInputStream(binFileName)) {
            int lengthFile = inputStream.available();
            int signalLength = lengthFile / (2 * frameX * frameY);
            binArr = new short[signalLength];
            for (int koordY = 0; koordY < 50; koordY++) {
                for (int koordX = 0; koordX < 640; koordX++) {
                    for (int k = 0; k < signalLength; k++) {
                        binArr[k] = nextShortFromInputStream(inputStream);
//                        System.out.println("koordX " + koordX + " koordY " + koordY + "k: " + k + "   " + binArr[k]);
                    }
//                    double result = findMaxProcedureStyle.EnvMax(binArr);
                    System.out.println("koordX " + koordX + " koordY " + koordY );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    private static short nextShortFromInputStream(InputStream is) throws IOException {
        short sb = (short) is.read();
        short fb = (short) is.read();
        return (short) (((fb & 0xff) << 8) | (sb & 0xff));
    }

}
