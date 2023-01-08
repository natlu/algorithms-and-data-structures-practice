import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseballElimination {

    private int numberOfTeams;
    private String[] teams;
    private int[] w;
    private int[] l;
    private int[] r;
    private int[][] g;

    public BaseballElimination(String filename) {
        In input = new In(filename);

        numberOfTeams = Integer.parseInt(input.readLine());
        teams = new String[numberOfTeams];
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
            teams[i] = fields[0];
            w[i] = Integer.parseInt(fields[1]);
            l[i] = Integer.parseInt(fields[2]);
            r[i] = Integer.parseInt(fields[3]);
            for (int j = 0; j < numberOfTeams; j++) {
                g[i][j] = Integer.parseInt(fields[4 + j]);
            }
            i += 1;
        }
    }
    public int numberOfTeams() {
        return numberOfTeams;
    }
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }
    private int teamIndex(String team) {
        return  Arrays.asList(teams).indexOf(team);
    }
    public int wins(String team) {
        return w[teamIndex(team)];
    }
    public int losses(String team) {
        return l[teamIndex(team)];
    }
    public int remaining(String team) {
        return r[teamIndex(team)];
    }
    public int against(String team1, String team2) {
        return g[teamIndex(team1)][teamIndex(team2)];
    }
    // public          boolean isEliminated(String team)              // is given team eliminated?
    // public Iterable<String> certificateOfElimination(String team)  // subset R of teams that eliminates given team; null if not eliminated
    private String currentWinningTeam() {
        int indexOfLargest = 0;
        for (int i = 1; i < w.length; i++) {
            if (w[i] > w[indexOfLargest]) {
                indexOfLargest = i;
            }
        }
        return teams[indexOfLargest];
    }
    private boolean isTrivallyEliminated(String team) {
        return wins(team) + remaining(team) < wins(currentWinningTeam());
    }

    public static void main(String[] args) {
        BaseballElimination be = new BaseballElimination("/Users/nlu/Downloads/baseball/teams4.txt");
    }
}
