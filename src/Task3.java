// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.ArrayList;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;
    int N, M, mComplement = 0;
    ArrayList<Integer>[] adj; // lista de adiacenta a grafului din input
    ArrayList<Integer>[] complement; // lista de adiacenta a grafului complemntar
    String[] values; // retine variabilele satisfacute de oracol

    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();

        graphComplement();
        // se apeleaza task2 de N - 1 ori
        for (int K = N; K > 1; K--) {
            reduceToTask2(K);
            task2Solver.solve();
            if (task2Solver.answer.equals("True")) {
                extractAnswerFromTask2();
                break;
            } else {
                task2Solver = new Task2();
                task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
            }

        }
        writeAnswer();
    }

    /**
     * Initilizeaza listele de adiacenta
     */
    public void initializeAdjacencyList() {
        adj = new ArrayList[N + 1];
        complement = new ArrayList[N + 1];
        for (int i = 1; i <= N; i++) {
            adj[i] = new ArrayList<>();
            complement[i] = new ArrayList<>();
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
        String[] edge = line.split(" ");
        N = Integer.parseInt(edge[0]);
        M = Integer.parseInt(edge[1]);
        initializeAdjacencyList();
        for (int i = 0; i < M; i++) {
            line = reader.readLine();
            edge = line.split(" ");
            int v = Integer.parseInt(edge[0]);
            int u = Integer.parseInt(edge[1]);
            adj[v].add(u);
            adj[u].add(v);
        }
        reader.close();
    }

    /**
     * Construieste complementul grafului dat ca input
     */
    public void graphComplement() {
        for (int v = 1; v <= N; v++) {
            for (int u = 1; u <= N; u++) {
                if (!adj[v].contains(u) && v != u && !complement[u].contains(v)) {
                    mComplement++;
                    complement[v].add(u);
                }
            }
        }
    }

    /**
     * Scrie in fisierul de input al task-ului 2, relatiile grefului complementar si numarul maxim
     * de elemente pe care sa o aiba clica
     * @param K dimensiunea clicii
     */
    public void reduceToTask2(int K) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(task2InFilename));
            writer.write(N + " " + mComplement + " " + K + "\n");
            for (int v = 1; v <= N; v++) {
                for (Integer u : complement[v]) {
                    writer.write(v + " " + u  + "\n");
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extrage raspunsul dat de oracol, atunci cand am gasit clica de marime maxima
     * @throws IOException
     */
    public void extractAnswerFromTask2() throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(task2OutFilename));
            String answer = br.readLine();
            String line = br.readLine();
            values = line.split(" ");
            br.close();
    }

    /**
     * Verifica daca un nod este membru a clicii din graful complememntar
     * @param node nodul verifcat
     * @return true, este membru
     *         false, caz contrar
     */
    public boolean is_connected(int node) {
        for (int i = 0; i < values.length; i++) {
            if (node == Integer.parseInt(values[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este scris in fisierul de output raspunsul oracolului si membrii clicii din graful dat in
     * input. Membrii clicii a grafului dat ca input nu trebuie sa fie membrii a clicii din graful
     * complementar
     *
     * @throws IOException
     */
    @Override
    public void writeAnswer() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename));
        for (int i = 1; i <= N; i++) {
            if (!is_connected(i)) {
                writer.write(i + " ");
            }
        }
        writer.write("\n");
        writer.flush();
        writer.close();
    }
}
