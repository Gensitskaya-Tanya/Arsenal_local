import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadBinFile {

    public static void main(String[] args) {
        ReadBinFile readBinFile = new ReadBinFile();
        int koordX = 5;//koordX 0...639
        int koordY = 2;//koordY 0...479
        double[] arrAllMaxSignalInRowY = new double[640];
        FindMaxFromExcel findMaxFromExcel = new FindMaxFromExcel();

////     calculates in matrix from X1 to X2, from Y1 to Y2
//        for (int i = 0; i <= koordY; i++) {
//            for (int j = 0; j < koordX; j++) {
//                int[] arr = readBinFile.readPointFromBinFile(j, i, NameFile.BIN_FILE_NAME_READ);
//                double[] doubleArr = new double[arr.length];
//                for (int k = 0; k < doubleArr.length; k++) {
//                    doubleArr[k] = (double) arr[k];
//                }
//                arrAllMaxSignalInRowY[j] = findMaxFromExcel.findMaxCutSignal(doubleArr);
//                System.out.println("X: " + j + ", Y: " + i + "   max = " + arrAllMaxSignalInRowY[j]);
//            }
//        }

////        calculates from X1 to X2, Y=const
//        for (int rangeX = 0; rangeX < 11; rangeX++) {
//            int[] arr = readBinFile.readPointFromBinFile(rangeX, koordY, NameFile.BIN_FILE_NAME_READ);
//            double[] doubleArr = new double[arr.length];
//            for (int i = 0; i < doubleArr.length; i++) {
//                doubleArr[i] = (double) arr[i];
//            }
//            arrAllMaxSignalInRowY[rangeX] = findMaxFromExcel.findMaxCutSignal(doubleArr);
//            System.out.println("X: " + rangeX + ", Y: " + koordY + "max = " + arrAllMaxSignalInRowY[rangeX]);
//        }


//readBinFile.readMatrixFromBinFile(0,0,NameFile.BIN_FILE_NAME_READ);

    }

    public int[] readPointFromBinFile(int koordX, int koordY, String fileName) {
        int frameX = 640;
        int frameY = 480;
        int[] binArr = null;
        try (InputStream inputStream = new FileInputStream(fileName)) {
            int lengthFile = inputStream.available();
            int signalLength = lengthFile / (2 * frameX * frameY);

            inputStream.skip(signalLength * 2 * (frameX * koordY + koordX));
            binArr = new int[signalLength];
            for (int i = 0; i < signalLength; i++) {
                binArr[i] = nextIntFromInputStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return binArr;
    }

    public void readMatrixFromBinFile(int temp1, int temp2, String fileName) {
        FindMaxFromExcel findMaxFromExcel = new FindMaxFromExcel();
        int frameX = 640;
        int frameY = 480;
        int koordX = 0;//koordX 0...639
        int koordY = 0;
        int[] binArr = null;
        double max = 0;
        try (InputStream inputStream = new FileInputStream(fileName)) {
            int lengthFile = inputStream.available();
            int signalLength = lengthFile / (2 * frameX * frameY);
            binArr = new int[signalLength];
            for (int i = 0; i <= 479; i++) {//koordY
                for (int j = 0; j <= 639; j++) {//koordX

                    for (int k = 0; k < signalLength; k++) {
                        binArr[k] = nextIntFromInputStream(inputStream);
//                        System.out.println("k: " + k + "   " + binArr[k]);
                    }

                    double[] doubleArr = new double[binArr.length];
                    for (int n = 0; n < binArr.length; n++) {
                        doubleArr[n] = (double) binArr[n];
                    }
                    max = findMaxFromExcel.findMaxCutSignal(doubleArr);
                    System.out.println("X: " + j + ", Y: " + i + "  max " + max);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int nextIntFromInputStream(InputStream is) throws IOException {
        int sb = is.read();
        int fb = is.read();
        return ((fb & 0xff) << 8) | (sb & 0xff);
    }
}
