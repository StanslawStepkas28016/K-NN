import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Najmocniej przepraszam, wiem, że nie jest to kod wysokiej jakości, zero przejrzystości,
 * o wydajności można zapomnieć xD, robione po spartańsku, działa, działa :)
 */
public class Main {
    public static void main(String[] args) {
        // Można zamienić args[1] i args[2] na nazwy plików, k podajemy w interfejsie programu.
        final String trainSetName = args[1];
        final String testSetName = args[2];
        final kEngine kEngine = new kEngine();
        boolean mainLoop = true;

        do {
            System.out.println("1. Chcę testować dane na podstawie zbioru treningowego (podając k).");
            System.out.println("2. Chcę testować dane na podstawie wprowadzonego wektoru i k.");
            System.out.println("3. Chcę zakończyć działanie programu.");
            Scanner sc = new Scanner(System.in);
            System.out.print("Wprowadź cyfrę reprezentującą wybraną opcję: ");
            final String in = sc.next();

            if (in.matches("\\d+")) {
                final int i = Integer.parseInt(in);
                if (i == 1) {
                    testOnTrainSet(kEngine, trainSetName, testSetName);
                } else if (i == 2) {
                    testOnInputVector(kEngine, trainSetName, sc);
                } else if (i == 3) {
                    mainLoop = false;
                } else {
                    System.out.println("Opcja nr : " + i + ", nie istnieje!");
                }
            } else {
                System.out.println("Wprowadzono niepoprawne dane, upewnij się, że wpisujesz liczbę całkowitą!");
            }

            System.out.println("=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
        } while (mainLoop);
    }

    private static void testOnInputVector(kEngine kEngine, String trainSetName, Scanner sc) {
        final String trainDataVectorFormat = kEngine.getTrainDataVectorFormat(trainSetName);
        System.out.print("Wprowadź wektor, forma obowiązkowa dla podanego trainSet - " + trainDataVectorFormat + ": ");
        Scanner scTestOnVec = new Scanner(System.in);
        final String vec = scTestOnVec.next();

        System.out.print("Wprowadź cyfrę k: ");
        final String k = sc.next();

        final String tempName = "tempTest.csv";

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempName))) {
            bufferedWriter.write(vec);
        } catch (IOException e) {
            System.err.println("Błąd przy zapisywaniu do pliku tempTest.csv (możliwe, że plik nie ma uprawnień +rwx)!");
        }

        kEngine.train(trainSetName);
        kEngine.test(tempName, Integer.parseInt(k));
        kEngine.accuracy();
        kEngine.resetEngine();

        try {
            Files.delete(Paths.get(tempName));
        } catch (IOException e) {
            System.err.println("Błąd przy usuwaniu pliku tempTest.csv (możliwe, że plik nie ma uprawnień +rwx)!");
        }
    }

    private static void testOnTrainSet(kEngine kEngine, String trainSetName, String testSetName) {
        Scanner scTestOnTrain = new Scanner(System.in);
        System.out.print("Wprowadź cyfrę k: ");
        final String inTestOnTrain = scTestOnTrain.next();

        if (inTestOnTrain.matches("\\d+")) {
            final int i1 = Integer.parseInt(inTestOnTrain);
            final Integer kMax = kEngine.getTrainSetLen(trainSetName);
            if (i1 > 0 && i1 < kMax) {
                kEngine.train(trainSetName);
                kEngine.test(testSetName, i1);
                kEngine.accuracy();
                kEngine.resetEngine();
            } else if (i1 >= kMax) {
                System.out.println("Liczba k, musi być mniejsza od " + kMax);
            }
        } else {
            System.out.println("Wprowadzono niepoprawne dane, upewnij się, że wpisujesz liczbę całkowitą!");
        }
    }
}