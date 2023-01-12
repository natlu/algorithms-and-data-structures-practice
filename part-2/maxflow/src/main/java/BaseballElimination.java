import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;


public class BaseballElimination {

    private final int numberOfTeams;
    private final String[] teams;
    private final int[] w;
    private final int[] l;
    private final int[] r;
    private final int[][] g;
    private int sourceNode;
    private int targetNode;
    private HashSet<Integer> teamsWithGamesIndex;

    private FordFulkerson fordFulkerson;

    public BaseballElimination(String filename) {
        System.out.println("creating BaseballElimination");
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
        System.out.println("created BaseballElimination");
    }
    public int numberOfTeams() {
        return numberOfTeams;
    }
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }
    private int teamIndex(String team) {
        return Arrays.asList(teams).indexOf(team);
    }
    private boolean teamExists(String team) {
//        System.out.println("searching for " + team);
        for (String t : teams) {
//            System.out.println("team" + t);
            if (t.equals(team)) return true;
        }
        return false;
    }
    private void validateTeam(String team) {
        if (!teamExists(team))
            throw new IllegalArgumentException(team + " doesn't exist");
    }
    public int wins(String team) {
        validateTeam(team);
        return w[teamIndex(team)];
    }
    public int losses(String team) {
        validateTeam(team);
        return l[teamIndex(team)];
    }
    public int remaining(String team) {
        validateTeam(team);
        return r[teamIndex(team)];
    }
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return g[teamIndex(team1)][teamIndex(team2)];
    }
     public boolean isEliminated(String team) { // is given team eliminated?
         validateTeam(team);
         System.out.println("checking if trivially eliminated");
         if (isTriviallyEliminated(team)) return true;

         System.out.println("creating flow network");
         FlowNetwork fn = createFlowNetwork(team);
         System.out.println("creating ford fulkerson");
         fordFulkerson = new FordFulkerson(fn, sourceNode, targetNode);

         System.out.println("checking if eliminated");
          for (FlowEdge e : fn.adj(sourceNode)) {
              if (e.flow() < e.capacity()) return true;
          }

          return false;
     }

    public Iterable<String> certificateOfElimination(String team) { // subset R of teams that eliminates given team; null if not eliminated
        validateTeam(team);
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
        System.out.println("invoked createFlowNetwork");
        int teamIndex = teamIndex(team);
        int teamPotential = wins(team) + remaining(team);
        int numberOfTeamsWithGames = numberOfTeamsWithGames(teamIndex);
        int numberOfVertices = 2 + numberOfMatchUps(teamIndex) + numberOfTeamsWithGames;
        FlowNetwork flowNetwork = new FlowNetwork(numberOfVertices);

        sourceNode = teamIndex;
//        int numberOfMatchUps = 0;

        boolean[] teamHasMatch = new boolean[numberOfTeams];

        LinkedList<Integer> availableNodes = new LinkedList<>();
        for (int i = 0; i < numberOfVertices; i++) {
            if (i == sourceNode) continue;
            if (teamsWithGamesIndex.contains(i)) continue;
            availableNodes.add(i);
            System.out.println("availableNodes: " + i);
        }

        targetNode = availableNodes.pop();

        System.out.println("numberOfTeamsWithGames: " + numberOfTeamsWithGames);
        System.out.println("numberOfVertices: " + numberOfVertices);
        System.out.println("sourceNode: " + sourceNode);
        System.out.println("targetNode: " + targetNode);

        for (int team1Index = 0; team1Index < numberOfTeams; team1Index++) {
            if (team1Index == teamIndex) continue;
            for (int team2Index = team1Index + 1; team2Index < numberOfTeams; team2Index++) {
                if (team2Index == teamIndex) continue;
                double capacity = g[team1Index][team2Index];
                if (capacity > 0) {
//                    int matchUpNode = targetNode + numberOfMatchUps + 1;
                    int matchUpNode = availableNodes.pop();
                    flowNetwork.addEdge(new FlowEdge(sourceNode, matchUpNode, capacity));
                    System.out.println(sourceNode + "-(" + capacity + ")->" + matchUpNode);
                    flowNetwork.addEdge(new FlowEdge(matchUpNode, team1Index, Double.POSITIVE_INFINITY));
                    System.out.println(matchUpNode + "-(oo)->" + team1Index);
                    flowNetwork.addEdge(new FlowEdge(matchUpNode, team2Index, Double.POSITIVE_INFINITY));
                    System.out.println(matchUpNode + "-(oo)->" + team2Index);

                    if (!teamHasMatch[team1Index]) {
                        flowNetwork.addEdge(new FlowEdge(team1Index, targetNode, teamPotential - w[team1Index]));
                        double c1 = teamPotential - w[team1Index];
                        System.out.println(team1Index + "-(" + c1 + ")->" + targetNode);
                    }
                    if (!teamHasMatch[team2Index]) {
                        flowNetwork.addEdge(new FlowEdge(team2Index, targetNode, teamPotential - w[team2Index]));
                        double c2 = teamPotential - w[team2Index];
                        System.out.println(team2Index + "-(" + c2 + ")->" + targetNode);
                    }
                    teamHasMatch[team1Index] = true;
                    teamHasMatch[team2Index] = true;
//                    numberOfMatchUps++;
                }
            }
        }

        return flowNetwork;
    }

    private HashSet<Integer> teamsWithGames(int teamIndex) {
        teamsWithGamesIndex = new HashSet<>();
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) continue;
            for (int j = 0; j < numberOfTeams; j++) {
                if (j == teamIndex) continue;
                if (g[i][j] > 0) {
                    teamsWithGamesIndex.add(i);
                    break;
                }
            }
        }
        return teamsWithGamesIndex;
    }

    private int numberOfTeamsWithGames(int teamIndex) {
        teamsWithGamesIndex = teamsWithGames(teamIndex);
        return teamsWithGamesIndex.size();
//        int count = 0;
//        for (int i = 0; i < numberOfTeams; i++) {
//            if (i == teamIndex) continue;
//            for (int j = 0; j < numberOfTeams; j++) {
//                if (j == teamIndex) continue;
//                if (g[i][j] > 0) {
//                    count++;
//                    break;
//                }
//            }
//        }
//        return count;
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
        System.out.println("main 1 ---------------------------");
        BaseballElimination be = new BaseballElimination("/Users/nlu/Downloads/baseball/teams4a.txt");
        System.out.println("main 2 ---------------------------");
        String selectedTeam = "Ghaddafi";
        System.out.println("main 3 ---------------------------");
        FlowNetwork fn = be.createFlowNetwork(selectedTeam);
        System.out.println("main 4 ---------------------------");
        System.out.println(fn.toString());
        System.out.println("main 5 ---------------------------");

        if (be.isEliminated(selectedTeam)) {
            System.out.println("is eliminated");
        } else {
            System.out.println("not eliminated");
        }
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

//        LinkedList<Integer> availableNodes = new LinkedList<>();
//        availableNodes.add(1);
//        availableNodes.add(2);
//        System.out.println(availableNodes.size());
//        availableNodes.pop();
//        System.out.println(availableNodes.size());


    }
}

