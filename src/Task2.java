import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {

    public class Pair {
        /**
         * Salveaza perechi de clauze folosite
         */
        int literal1;
        int literal2;

        Pair(int literal1, int literal2) {
            this.literal1 = literal1;
            this.literal2 = literal2;
        }
    }

    int N, M, K;
    ArrayList<Integer>[] adj; // lista de adiacenta
    ArrayList<Integer> variables = new ArrayList<>(); // lista cu variablilele folosite la intrebarea oracolului
    ArrayList<Pair> clauses = new ArrayList<>(); // conditiile pe care le are clica
    String answer; // raspunsul oracolului
    ArrayList<Integer> cliqueMembers = new ArrayList<>(); // membrii clicii dat de raspunsul oracolului

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
        BufferedReader br = new BufferedReader(new FileReader(inFilename));
        String line = br.readLine();
        String[] values = line.split(" ");
        N = Integer.parseInt(values[0]);
        M = Integer.parseInt(values[1]);
        K = Integer.parseInt(values[2]);
        initializeAdjacencyList();
        // se citesc din fisier relatiile dintre familii si se aduga in lista de adiacenta
        for (int i = 0; i < M; i++) {
            line = br.readLine();
            values = line.split(" ");
            int v = Integer.parseInt(values[0]);
            int u = Integer.parseInt(values[1]);
            adj[v].add(u);
            adj[u].add(v);
        }
        br.close();
    }

    /**
     * Sunt salvate intr-un ArrayList variabilele folosite in intrebarea oracolului
     */
    public void defineOracleVariables() {
        int var = 1;
        for (int i = 1; i <= K; i++) {
            for (int v = 1; v <= N; v++) {
                variables.add(var);
                var++;
            }
        }
    }

    /**
     * Sunt salvate perechi de variabile folosite la intrebarea oracolului ce respecta conditiile
     * pe care la are o clica
     */
    public void cliqueConditions() {
        for (Integer variable1 : variables) {
            int i = (variable1 / N) + 1; // pozitia din clica a nodului v
            int v = variable1 % N; // nodul reprezentat de variablla curenta
            if (v == 0) {
                v = N;
            }
            for (Integer variable2 : variables) {
                int j = (variable2 / N) + 1; // pozitia din clica a nodului w
                int w = variable2 % N; // nodul reprezentat de variabila curenta
                if (w == 0) {
                    w = N;
                }
                // 2 noduri care nu au muchie intre ele nu pot fi amandoua in clica
                if (!adj[v].contains(w) && v != w && !variable1.equals(variable2)) {
                    clauses.add(new Pair(variable1 * (-1), variable2 * (-1)));
                }
                // 2 noduri nu pot avea aceeasi pozitie in clica sau
                // 1 nod nu poate fi pe mai multe pozitii in clica
                if (((v == w && i != j) || (v != w && i == j)) && !variable1.equals(variable2)) {
                    clauses.add(new Pair(variable1 * (-1), variable2 * (-1)));
                }
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
        defineOracleVariables();
        cliqueConditions();
        BufferedWriter writer = new BufferedWriter(new FileWriter(oracleInFilename));
        int numberOfClauses = clauses.size() + K;
        writer.write("p cnf" + " " + variables.size() + " " + numberOfClauses + "\n");
        // se pune conditia ca exista un nod in clica pe o anumita pozitie
        for (int vars = 0; vars < variables.size(); vars += N) {
            for (int k = vars + 1; k <= vars + N; k++) {
                writer.write(k + " ");
            }
            writer.write("0\n");
        }
        // se scriu in fisier clauzele salvate din ArrayList
        for (Pair pair : clauses) {
            writer.write(pair.literal1 + " " + pair.literal2 + " 0\n");
        }
        writer.close();
        clauses.clear();
    }

    /**
     * Se citeste raspunsul dat de oracol. Daca raspunsul este "True", sunt extrase variabilele
     * folosite si parcurse. In timpul parcurgerii se verifica daca fiecare valoare este pozitiva
     * deoarece reprezinta membrii clicii
     *
     * @throws IOException exceptie citire fisier
     */
    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(oracleOutFilename));
        answer = br.readLine();
        if (answer.equals("True")) {
            int V = Integer.parseInt(br.readLine());
            String line = br.readLine();
            String[] values = line.split(" ");
            for (int i = 0; i < V; i++) {
                if (Integer.parseInt(values[i]) > 0) {
                    if (Integer.parseInt(values[i]) % N == 0) {
                        cliqueMembers.add(N);
                    } else {
                        cliqueMembers.add(Integer.parseInt(values[i]) % N);
                    }
                }
            }
        }
    }

    /**
     * Este scris in fisierul de output raspunsul oracolului si membrii clicii
     *
     * @throws IOException
     */
    @Override
    public void writeAnswer() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename));
        writer.write(answer + "\n");
        if (answer.equals("True")) {
            for (Integer cliqueMember : cliqueMembers) {
                writer.write(cliqueMember + " ");
            }
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }
}
