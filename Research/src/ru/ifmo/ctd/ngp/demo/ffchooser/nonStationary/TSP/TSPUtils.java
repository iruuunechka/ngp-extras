package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Irene Petrova
 */
public class TSPUtils {
    public static List<List<Integer>> generateRandomPopulation(int size, double[][] tsp, Random rng) {
        List<List<Integer>> population = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            population.add(generateRandomIndividual(tsp, rng));
        }
        return population;
    }

    public static List<Integer> generateRandomIndividual(double[][] tsp, Random rng) {
        List<Integer> donor = new ArrayList<>();
        for (int i = 0; i < tsp.length; i++) {
            donor.add(i);
        }
        Collections.shuffle(donor, rng);
        return donor;
    }

    public static double evalTime(List<Integer> individual, double[][] tsp) {
        double time = 0;
        for (int i = 0; i < individual.size() - 1; ++i) {
            time += tsp[individual.get(i)][individual.get(i + 1)];
        }
        time += tsp[individual.get(individual.size() - 1)][individual.get(0)];
        return time;
    }

    public static double eucledianCostEstimate(double[][] tsp) {
        double K = 0.7124;
        double A = 1.0;
        return K * Math.sqrt(tsp.length * A);
    }

    public static TSPProblem readJsonInstance(String fname) throws FileNotFoundException {
        System.out.println(String.format("Loading %s", fname));
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new FileReader(fname));
//        String jstr = reader.readText();
//            reader.close()
        Type tparr = new TypeToken<List<JsonElement>>(){}.getType();
        List<JsonElement> json = gson.fromJson(reader, tparr);
        Type arr = new TypeToken<double[][]>(){}.getType();
        double[][] tsp = gson.fromJson(json.get(0).getAsJsonObject().getAsJsonArray("a"), arr);
        Integer[] solution = null;
        if (!json.get(1).isJsonNull()) {
            solution = gson.fromJson(json.get(1).getAsJsonObject().getAsJsonArray("solution"), Integer[].class);
        }
        return new TSPProblem(tsp, solution == null ? -1 : evalTime(Arrays.asList(solution), tsp));
    }

    public static TSPProblem readXMLInstance(String fname) throws ParserConfigurationException, IOException, SAXException {
        System.out.println(String.format("Loading %s", fname));
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new File(fname));
//        double doublePrecision = Double.parseDouble(doc.getElementsByTagName("doublePrecision").item(0).getFirstChild().getTextContent());
//        double ignoreDigits = Double.parseDouble(doc.getElementsByTagName("ignoredDigits").item(0).getFirstChild().getTextContent());
        NodeList graph = doc.getElementsByTagName("vertex");
        int size = graph.getLength();
        double[][] tsp = new double[size][size];
        for (int i = 0; i < size; ++i) {
            NodeList edges = ((Element) graph.item(i)).getElementsByTagName("edge");
            for (int j = 0; j < edges.getLength(); ++j) {
                Element e = (Element) edges.item(j);
                double cost = Double.parseDouble(e.getAttribute("cost"));
                int to = Integer.parseInt(e.getTextContent());
                tsp[i][to] = cost;
            }
        }
        double optimal = Double.parseDouble(doc.getElementsByTagName("optimal").item(0).getFirstChild().getTextContent());
        return new TSPProblem(tsp, optimal);
    }

    private static void reverse(List<Integer> individual, int pos1, int pos2, Map<Integer, Integer> posInWay) {
        int len = individual.size();
        int segment = pos2 > pos1 ? pos2 - pos1 + 1 : pos2 - pos1 + len + 1;
        for (int i = 0; i <= segment / 2 - 1; ++i) {
            int curPos1 = (pos1 + i) % len;
            int curPos2 = (pos2 - i) % len;
            if (curPos2 < 0) {
                curPos2 += len;
            }

            int tmp = individual.get(curPos1);
            individual.set(curPos1, individual.get(curPos2));
            posInWay.put(individual.get(curPos2), curPos1);
            individual.set(curPos2, tmp);
            posInWay.put(tmp, curPos2);
        }
    }

    public static void apply2Opt(List<Integer> individual, TSPProblem problem) {
        Map<Integer, Integer> posInWay = new HashMap<>();
        //        for (int i : individual) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
        for (int i = 0; i < individual.size(); ++i) {
            posInWay.put(individual.get(i), i);
        }
        for (int i = 0; i < individual.size(); ++i) {
            int c1 = individual.get(i);
            int c2 = individual.get((i + 1) % individual.size());
            double c1c2Val = problem.getTSPValue(c1,c2);
            int cur = 0;
            int c3 = problem.getTSPNeighbour(c1, cur);
            boolean improved = false;
            while (problem.getTSPValue(c1, c3) < c1c2Val) {
                int c4 = individual.get((posInWay.get(c3) + 1) % individual.size());
                if (c3 == c1 || c3 == c2 || c4 == c1 || c4 == c2) {
                    cur++;
                    c3 = problem.getTSPNeighbour(c1, cur);
                    continue;
                }

//                int c4_1 = individual.get(posInWay.get(c3) == 0 ? individual.size() - 1 : posInWay.get(c3) - 1);
//                if (problem.getTSPValue(c2, c4_1) - problem.getTSPValue(c3, c4_1) <
//                        problem.getTSPValue(c2, c4) - problem.getTSPValue(c3, c4)) {
//                    c4 = c4_1;
//                }

                if (problem.getTSPValue(c1, c3) + problem.getTSPValue(c2, c4) - problem.getTSPValue(c3, c4) - c1c2Val < 0) {
                    int posc2 = posInWay.get(c2);
                    int posc3 = posInWay.get(c3);
//                    individual.set(posc2, c3);
//                    individual.set(posc3, c2);
                    reverse(individual, posc2, posc3, posInWay);
//                    posInWay.put(c2, posc3);
//                    posInWay.put(c3, posc2);
                    improved = true;
                    break;
                }

                cur++;
                c3 = problem.getTSPNeighbour(c1, cur);
            }

//            double newTime = evalTime(individual, problem.tsp);
//            if (newTime > oldTime) {
//                for (int ii : individual) {
//                    System.out.print(ii + " ");
//                }
//                System.out.println();
//                throw new RuntimeException(newTime + " " + oldTime);
//            }
//            oldTime = newTime;

            if (improved) {
                continue;
            }
            cur = 0;
            int c4 = problem.getTSPNeighbour(c2, cur);
            while (problem.getTSPValue(c2, c4) < c1c2Val) {
                c3 = individual.get(posInWay.get(c4) == 0 ? individual.size() - 1 : posInWay.get(c4) - 1);
                if (c4 == c1 || c4 == c2 || c3 == c1 || c3 == c2) {
                    cur++;
                    c4 = problem.getTSPNeighbour(c2, cur);
                    continue;
                }
//                int c3_1 = individual.get(posInWay.get(c4) == 0 ? individual.size() - 1 : posInWay.get(c4) - 1);
//                if (problem.getTSPValue(c3_1, c4) - problem.getTSPValue(c1, c3_1) >
//                        problem.getTSPValue(c3, c4) - problem.getTSPValue(c1, c3)) {
//                    c3 = c3_1;
//                }

                if (problem.getTSPValue(c1, c3) + problem.getTSPValue(c2, c4) - problem.getTSPValue(c3, c4) - c1c2Val < 0) {
                    int posc2 = posInWay.get(c2);
                    int posc3 = posInWay.get(c3);
//                    individual.set(posc2, c3);
//                    individual.set(posc3, c2);
//                    posInWay.put(c2, posc3);
//                    posInWay.put(c3, posc2);
                    reverse(individual, posc2, posc3, posInWay);
                    break;
                }

                cur++;
                c4 = problem.getTSPNeighbour(c2, cur);
            }
//            newTime = evalTime(individual, problem.tsp);
//            if (Double.compare(newTime, oldTime) > 0) {
//                for (int ii : individual) {
//                    System.out.print(ii + " ");
//                }
//                System.out.println();
//                throw new RuntimeException(newTime + " " + oldTime);
//            }
        }

    }
}
