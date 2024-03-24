import java.io.*;
import java.util.*;

public class kEngine {
    private final List<List<String>> trainData = new ArrayList<>();
    private int numericalDataQuantity = 0;
    private double hitCount = 0;
    private double testDataSize = 0;
    private int kParam = 0;

    public void train(String trainSetName) {
        readFromFile(trainSetName, trainData);

        // Pętla do wyliczenia ile mamy danych numerycznych w train-set,
        // potrzebne do późniejszego "Processingu".
        for (String s : trainData.get(0)) {
            if (s.matches("\\d+(\\.\\d+)?")) {
                numericalDataQuantity += 1;
            }
        }
    }

    private void readFromFile(String fileName, List<List<String>> out) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            // Dodanie informacji do listy out.
            while ((line = br.readLine()) != null) {
                final String[] split = line.split(";");
                List<String> dataList = new ArrayList<>();
                Collections.addAll(dataList, split);
                out.add(dataList);
            }

        } catch (IOException e) {
            System.err.println("Błąd przy usuwaniu pliku " + fileName + " (możliwe, że plik nie ma uprawnień +rwx)!");
        }
    }

    public void test(String testSetName, int k) {
        List<List<String>> testData = new ArrayList<>();
        readFromFile(testSetName, testData);
        testDataSize = testData.size();
        kParam = k;

        HashMap<Double, String> dict = new HashMap<>();
        for (List<String> test : testData) {
            // Wyliczenie długości między wektorami,
            // między trainData i testData.
            for (List<String> train : trainData) {
                double dist = 0;
                for (int i = 0; i < numericalDataQuantity; i++) {
                    dist += Math.pow(Double.parseDouble(test.get(i)) - Double.parseDouble(train.get(i)), 2);
                }
                dist = Math.sqrt(dist);
                dict.put(dist, train.get(numericalDataQuantity));
            }

            // Wybór k najmniejszych odległości dla danego elementu testowanego,
            // razem z wyborem atrybutu decyzyjnego.
            final ArrayList<Double> doubles = new ArrayList<>(dict.keySet());
            Collections.sort(doubles);

            // Zliczenie wystąpień atrybutu w k elementach.
            HashMap<String, Integer> decisionMap = new HashMap<>();
            for (int i = 0; i < k; i++) {
                final String s = dict.get(doubles.get(i));
                if (decisionMap.containsKey(s)) {
                    decisionMap.put(s, decisionMap.get(s) + 1);
                } else {
                    decisionMap.put(s, 1);
                }
            }

            // Najczęściej występujący atrybut (na podstawie najkrótszej odległości),
            // decyzja w kwestii wyboru elementu, w przypadku takiej samej odległości,
            // jest pseudolosowa - ostatni element w tablicy, który ma maxCount.
            int maxCount = 0;
            String res = "";
            for (Map.Entry<String, Integer> entry : decisionMap.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    res = entry.getKey();
                }
            }

            // Oczysczenie mapy, do kolejnych iteracji.
            dict.clear();

            // Print danych z artybutem oczekiwanym i decyzyjnym.
            System.out.println(test + " <- Expected Value, Result Value -> " + res);

            if (test.get(numericalDataQuantity).equals(res)) {
                hitCount += 1;
            }
        }

    }

    public void accuracy() {
        System.out.println("Accuracy : " + hitCount / testDataSize + ", for k : " + kParam);
    }

    public void resetEngine() {
        trainData.clear();
        numericalDataQuantity = 0;
        hitCount = 0;
        testDataSize = 0;
        kParam = 0;
    }

    public String getTrainDataVectorFormat(String trainSetNameParam) {
        String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(trainSetNameParam))) {
            int i = 0;
            while (i != 1) {
                line = br.readLine();
                i++;
            }

        } catch (IOException e) {
            System.err.println("Błąd przy usuwaniu pliku " + trainSetNameParam + " (możliwe, że plik nie ma uprawnień +rwx)!");
        }

        assert line != null;
        final String[] split = line.split(";");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < split.length; i++) {
            if (split[i].matches("\\d+(\\.\\d+)?")) {
                sb.append("NUMERICAL_VALUE");
            } else {
                sb.append("STRING_VALUE");
            }

            if (i != split.length - 1) {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    public Integer getTrainSetLen(String trainSetNameParam) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(trainSetNameParam))) {
            String line;
            // Dodanie informacji do listy out.
            while ((line = br.readLine()) != null) {
                count += 1;
            }
        } catch (IOException e) {
            System.err.println("Błąd przy czytaniu pliku " + trainSetNameParam + " (możliwe, że plik nie ma uprawnień +rwx)!");
        }

        return count;
    }
}
