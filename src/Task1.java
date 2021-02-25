// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.ArrayList;

/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {
    int N, M, K;
    String answer; // raspunsul oracolului
    ArrayList<Integer>[] adj; // lista de adiacenta
    ArrayList<Integer> spies = new ArrayList<>(); // retine spionii repartizati fiecarei familii

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    /**
     * Initializeaza lista de adiacenta
     */
    public void initializeAdjacencyList() {
        adj = new ArrayList[N + 1];
        for (int i = 1; i <= N; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    /**
     * Citeste din fisierul de input datele despre familii si le adauga in lista de adiacenta
     * @throws IOException exceptie citire din fisier
     */
    @Override
    public void readProblemData() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inFilename));
        String line = reader.readLine();
        String[] values = line.split(" ");
        N = Integer.parseInt(values[0]);
        M = Integer.parseInt(values[1]);
        K = Integer.parseInt(values[2]);
        initializeAdjacencyList();
        // se citesc din fisier relatiile dintre familii si se aduga in lista de adiacenta
        for (int i = 0; i < M; i++) {
            line = reader.readLine();
            values = line.split(" ");
            int v = Integer.parseInt(values[0]);
            int u = Integer.parseInt(values[1]);
            adj[v].add(u);
            adj[u].add(v);
        }
        reader.close();
    }

    /**
     * Este scrisa in fisier urmatoarea clauza:
     *
     * O familie poate avea orice spion, numerotat de la 1 la K.
     *
     * @param writer instanta de tip BufferedWriter
     * @param v familia careia i se aplica clauza
     * @throws IOException exceptie scriere fisier
     */
    public void spies4Families(BufferedWriter writer, int v) throws IOException {
            // spionii pentru familia curenta
            for (int j = K; j > 0; j--) {
                int spy = (v * K) - j + 1;
                writer.write(spy + " ");
            }
            writer.write("0\n");
    }


    /**
     * Este scrisa in fisier urmatoarea clauza:
     *
     * O familie poate avea doar un singur spion
     *
     * @param writer instanta de tip BufferedWriter
     * @param v familia careia i se aplica clauza
     * @throws IOException exceptie scriere fisier
     */
    public void uniqueSpy(BufferedWriter writer, int v) throws IOException {
        for (int j = (v * K) - (K - 1); j < K * v; j++) {
            for (int k = j + 1; k <= K * v; k++) {
                writer.write(j * (-1) + " " + k * (-1) + " 0\n");
            }
        }
    }

    /**
     * Este scrisa in fisier urmatoarea clauza:
     *
     * Familiile prietene a unei familii nu pot avea acelasi spion
     *
     * @param writer instanta de tip BufferedWriter
     * @param v familia carei i se aplica clauza
     * @throws IOException excpetie scriere in fisier
     */
    public void differentSpies(BufferedWriter writer, int v) throws IOException {
        // pentru fiecare familie prietena verific sa nu aiba acelasi spion
        for (Integer u : adj[v]) {
            for (int j = 1; j <= K; j++) {
                int spyV = ((v * K) - K + j) * (-1);
                int spyU = ((u * K) - K + j) * (-1);
                writer.write(spyV + " " + spyU + " 0\n");
            }
        }
    }

    /**
     * Este formulata intrebarea oracolul, scriind in fisierul de intrare a oracolului clauzele
     * reducerii la problema SAT
     * @throws IOException
     */
    @Override
    public void formulateOracleQuestion() throws IOException {
        int variables = N * K;
        int clauses = N + K * ((K * (K - 1)) / 2 + M);
        BufferedWriter writer = new BufferedWriter(new FileWriter(oracleInFilename));
        writer.write("p cnf" + " " + variables + " " + clauses + "\n");
        for (int i = 1; i <= N; i++) {
            spies4Families(writer, i);
            uniqueSpy(writer, i);
            differentSpies(writer, i);
        }
        writer.flush();
        writer.close();
    }

    /**
     * Se citeste raspunsul dat de oracol. Daca raspunsul este "True", sunt extrase variabilele
     * folosite si parcurse. In timpul parcurgerii se verifica daca fiecare valoare este pozitiva
     * deoarece reprezinta numarul spionului asignat unei familii.
     *
     * @throws IOException
     */
    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(oracleOutFilename));
        answer = br.readLine();
        if (answer.equals("True")) {
            String line = br.readLine();
            line = br.readLine();
            String[] variables = line.split(" ");
            for (int i = 1; i < variables.length; i++) {
                if (Integer.parseInt(variables[i - 1]) > 0) {
                    if (Integer.parseInt(variables[i - 1]) % K == 0) {
                        spies.add(K);
                    } else {
                        spies.add(Integer.parseInt(variables[i - 1]) % K);
                    }
                }
            }
        }
    }

    /**
     * Este scris in fisierul de output raspunsul oracolului si spionii asignati fiecarei familii
     *
     * @throws IOException
     */
    @Override
    public void writeAnswer() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename));
        writer.write(answer + "\n");
        if (answer.equals("True")) {
            for (Integer spy : spies) {
                writer.write(spy % K + " ");
            }
            writer.write("\n");
        }
        writer.close();
    }
}
