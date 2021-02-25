import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Bonus Task
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class BonusTask extends Task {
    int N, M, K;
    ArrayList<Integer>[] adj;
    ArrayList<Integer>[] complement;
    ArrayList<Integer> variables = new ArrayList<>(); // lista cu variablilele folosite la intrebarea oracolului
    ArrayList<HashMap<Integer, Integer>> clauses = new ArrayList<>(); // conditiile pe care le are clica
    ArrayList<Integer> complementCliqueMembers = new ArrayList<>(); // retine membrii clicii a grafului complementar

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    /**
     * Initializeaza listele de adiacenta
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
        BufferedReader br = new BufferedReader(new FileReader(inFilename));
        String line = br.readLine();
        String[] edge = line.split(" ");
        N = Integer.parseInt(edge[0]);
        M = Integer.parseInt(edge[1]);
        initializeAdjacencyList();
        for (int i = 0; i < M; i++) {
            line = br.readLine();
            edge = line.split(" ");
            int v = Integer.parseInt(edge[0]);
            int u = Integer.parseInt(edge[1]);
            adj[v].add(u);
            adj[u].add(v);
        }
        br.close();
        graphComplement();
    }

    /**
     * Construieste complementul grafului dat ca input
     */
    public void graphComplement() {
        for (int v = 1; v <= N; v++) {
            for (int u = 1; u <= N; u++) {
                if (!adj[v].contains(u) && v != u && !complement[u].contains(v)) {
                    complement[v].add(u);
                    complement[u].add(v);
                }
            }
        }
    }

    /**
     * Se aproximeaza dimensiunea clicii a grafului complementar. In graful complementar este
     * cautat nodul cu gradul cel mai mare. Gradul sau este impartit la 2, rezultatul fiind adunat
     * cu 2, 2 fiind dimensiunea minima pe care poate sa o aiba o clica
     * @return dimensiunea aproximativa a clicii a grafului complementar
     */
     public int cliqueDimension() {
        int maxGrade = complement[1].size();
        for (int i = 2; i <= N; i++) {
            if (complement[i].size() > maxGrade) {
                maxGrade = complement[i].size();
            }
        }
        return (maxGrade  / 2) + 2;
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
                HashMap<Integer, Integer> pair = new HashMap<>();
                pair.put(variable1 * (-1), variable2 * (-1));
                // 2 noduri care nu au muchie intre ele nu pot fi amandoua in clica
                if (!complement[v].contains(w) && v != w && !variable1.equals(variable2)) {
                    clauses.add(pair);
                }
                // 2 noduri nu pot avea aceeasi pozitie in clica sau
                // 1 nod nu poate fi pe mai multe pozitii in clica
                if (((v == w && i != j) || (v != w && i == j)) && !variable1.equals(variable2)) {
                    clauses.add(pair);
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
        K = cliqueDimension();
        defineOracleVariables();
        cliqueConditions();
        BufferedWriter writer = new BufferedWriter(new FileWriter(oracleInFilename));
        int numberOfClauses = clauses.size() + K;
        int weights = clauses.size() + 1;
        writer.write("p wcnf" + " " + variables.size() + " " + numberOfClauses + " " + weights + "\n");
        for (HashMap<Integer, Integer> map : clauses) {
            int key = (int) map.keySet().toArray()[0];
            writer.write(weights + " " + key + " " + map.get(key) + " 0\n");
        }
        int weight = K + 1;
        for (int vars = 0; vars < variables.size(); vars += N) {
            writer.write(weight + " ");
            for (int k = vars + 1; k <= vars + N; k++) {
                writer.write(k + " ");
            }
            writer.write("0\n");
            weight--;
        }
        writer.close();
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
        BufferedReader reader = new BufferedReader(new FileReader(oracleOutFilename));
        String line = reader.readLine();
        line = reader.readLine();
        String[] answer = line.split(" ");
        for (int i = 0; i < variables.size(); i++) {
            if (Integer.parseInt(answer[i]) > 0 ) {
                if (Integer.parseInt(answer[i]) % N == 0) {
                    complementCliqueMembers.add(N);
                } else {
                    complementCliqueMembers.add(Integer.parseInt(answer[i]) % N);
                }
            }
        }
    }

    /**
     * Verifica daca un nod este membru a clicii din graful complememntar
     * @param node nodul verifcat
     * @return true, este membru
     *         false, caz contrar
     */
    public  boolean is_connected(int node) {
        for (Integer member : complementCliqueMembers) {
            if (node == member) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este scris in fisierul de output raspunsul oracolului si membrii clicii
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
