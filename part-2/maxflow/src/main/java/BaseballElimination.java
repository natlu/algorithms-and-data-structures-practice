import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.List;

public class BaseballElimination {

    private String[] team;
    private int[] w;
    private int[] l;
    private int[] r;
    private int[][] g;

    public BaseballElimination(String filename) {
        In input = new In(filename);

        int numberOfTeams = Integer.parseInt(input.readLine());
        team = new String[numberOfTeams];
        w = new int[numberOfTeams];
        l = new int[numberOfTeams];
        r = new int[numberOfTeams];
        g = new int[numberOfTeams][numberOfTeams];

        int i = 0;
        while (input.hasNextLine()) {
            String line = input.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] fields = line.split("\\s+");
            team[i] = fields[0];
            w[i] = Integer.parseInt(fields[1]);
            l[i] = Integer.parseInt(fields[2]);
            r[i] = Integer.parseInt(fields[3]);
            for (int j = 0; j < numberOfTeams; j++) {
                g[i][j] = Integer.parseInt(fields[4 + j]);
            }
            i += 1;
        }
//        for (int n = 0; n < numberOfTeams; n++) {
//            for (int m = 0; m < numberOfTeams; m++) {
//                System.out.printf(Integer.toString(g[n][m]));
//            }
//            System.out.println("-----");
//        }

    }

    // public              int numberOfTeams()                        // number of teams
    // public Iterable<String> teams()                                // all teams
    // public              int wins(String team)                      // number of wins for given team
    // public              int losses(String team)                    // number of losses for given team
    // public              int remaining(String team)                 // number of remaining games for given team
    // public              int against(String team1, String team2)    // number of remaining games between team1 and team2
    // public          boolean isEliminated(String team)              // is given team eliminated?
    // public Iterable<String> certificateOfElimination(String team)  // subset R of teams that eliminates given team; null if not eliminated

    public static void main(String[] args) {
        BaseballElimination be = new BaseballElimination("/Users/nlu/Downloads/baseball/teams4.txt");
    }
}
