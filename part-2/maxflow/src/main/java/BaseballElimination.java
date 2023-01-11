import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;


public class BaseballElimination {

    private final int numberOfTeams;
    private final String[] teams;
    private final int[] w;
    private final int[] l;
    private final int[] r;
    private final int[][] g;
    private int sourceNode;
    private int targetNode;

    private FordFulkerson fordFulkerson;

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
            line = line.strip();
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
     public boolean isEliminated(String team) { // is given team eliminated?
        if (isTriviallyEliminated(team)) return true;
        FlowNetwork fn = createFlowNetwork(team);
        fordFulkerson = new FordFulkerson(fn, sourceNode, targetNode);

         for (FlowEdge e : fn.adj(sourceNode)) {
             if (e.flow() < e.capacity()) return true;
         }

         return false;
     }

    public Iterable<String> certificateOfElimination(String team) { // subset R of teams that eliminates given team; null if not eliminated
        if (!isEliminated(team)) return null;

        ArrayList<String> teamSubset = new ArrayList<>();

        for (int v = 0; v < numberOfTeams; v++) {
            if (v == sourceNode) continue;
            if (fordFulkerson.inCut(v)) {
                teamSubset.add(teams[v]);
            }
        }

        return teamSubset;
    }

    private String currentWinningTeam() {
        int indexOfLargest = 0;
        for (int i = 1; i < w.length; i++) {
            if (w[i] > w[indexOfLargest]) {
                indexOfLargest = i;
            }
        }
        return teams[indexOfLargest];
    }
    private boolean isTriviallyEliminated(String team) {
        return wins(team) + remaining(team) < wins(currentWinningTeam());
    }

    private FlowNetwork createFlowNetwork(String team) {
        int teamIndex = teamIndex(team);
        int teamPotential = wins(team) + remaining(team);
        int numberOfTeamsWithGames = numberOfTeamsWithGames(teamIndex);
        int numberOfVertices = 2 + numberOfMatchUps(teamIndex) + numberOfTeamsWithGames;
        FlowNetwork flowNetwork = new FlowNetwork(numberOfVertices);

        sourceNode = teamIndex;
        targetNode = numberOfTeamsWithGames + 1;
        int numberOfMatchUps = 0;

        boolean[] teamHasMatch = new boolean[numberOfTeams];

        for (int team1Index = 0; team1Index < numberOfTeams; team1Index++) {
            if (team1Index == teamIndex) continue;
            for (int team2Index = team1Index + 1; team2Index < numberOfTeams; team2Index++) {
                if (team2Index == teamIndex) continue;
                double capacity = g[team1Index][team2Index];
                if (capacity > 0) {
                    int matchUpNode = targetNode + numberOfMatchUps + 1;
                    flowNetwork.addEdge(new FlowEdge(sourceNode, matchUpNode, capacity));
//                    System.out.println(sourceNode + "-(" + capacity + ")->" + matchUpNode);
                    flowNetwork.addEdge(new FlowEdge(matchUpNode, team1Index, Double.POSITIVE_INFINITY));
//                    System.out.println(matchUpNode + "-(oo)->" + team1Index);
                    flowNetwork.addEdge(new FlowEdge(matchUpNode, team2Index, Double.POSITIVE_INFINITY));
//                    System.out.println(matchUpNode + "-(oo)->" + team2Index);

                    if (!teamHasMatch[team1Index]) {
                        flowNetwork.addEdge(new FlowEdge(team1Index, targetNode, teamPotential - w[team1Index]));
//                        double c1 = teamPotential - w[team1Index];
//                        System.out.println(team1Index + "-(" + c1 + ")->" + targetNode);
                    }
                    if (!teamHasMatch[team2Index]) {
                        flowNetwork.addEdge(new FlowEdge(team2Index, targetNode, teamPotential - w[team2Index]));
//                        double c2 = teamPotential - w[team2Index];
//                        System.out.println(team2Index + "-(" + c2 + ")->" + targetNode);
                    }
                    teamHasMatch[team1Index] = true;
                    teamHasMatch[team2Index] = true;
                    numberOfMatchUps++;
                }
            }
        }

        return flowNetwork;
    }

    private int numberOfTeamsWithGames(int teamIndex) {
        int count = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) continue;
            for (int j = 0; j < numberOfTeams; j++) {
                if (j == teamIndex) continue;
                if (g[i][j] > 0) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private int numberOfMatchUps(int teamIndex) {
        int count = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) continue;
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == teamIndex) continue;
                if (g[i][j] > 0) count++;
            }
        }
        return count;
    }

//    public FordFulkerson Foo(String team) { // is given team eliminated?
//        FlowNetwork fn = createFlowNetwork(team);
//        FordFulkerson ff = new FordFulkerson(fn, sourceNode, targetNode);
//        return ff;
//    }
//
//    public FordFulkerson Bar(FlowNetwork fn) { // is given team eliminated?
//        FordFulkerson ff = new FordFulkerson(fn, sourceNode, targetNode);
//        return ff;
//    }


    public static void main(String[] args) {
        BaseballElimination be = new BaseballElimination("/Users/nlu/Downloads/baseball/teams10.txt");
        FlowNetwork fn = be.createFlowNetwork("Atlanta");
        System.out.println(fn.toString());
//
//        System.out.println(be.isEliminated("Philadelphia"));
//
//        Iterable<String> subset = be.certificateOfElimination("Philadelphia");
//        for (String s: subset) {
//            System.out.println(s);
//        }

//        String line = "    aasdf 3   34     2";
//        String[] fields = line.split("\\s+");
//        System.out.printf("---------");
//        System.out.println(fields[0]);
//        System.out.printf("---------");
//        for (String f: fields) {
//            System.out.println(f);
//        }


    }
}

