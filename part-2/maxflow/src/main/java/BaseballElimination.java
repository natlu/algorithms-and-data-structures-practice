
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
    private ArrayList<String> teamsWithGames;

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
        return Arrays.asList(teams).indexOf(team);
    }
    private boolean teamExists(String team) {
        for (String t : teams) {
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
         if (isTriviallyEliminated(team)) return true;

         FlowNetwork fn = createFlowNetwork(team);
         fordFulkerson = new FordFulkerson(fn, sourceNode, targetNode);

          for (FlowEdge e : fn.adj(sourceNode)) {
              if (e.flow() < e.capacity()) return true;
          }

          return false;
     }

    public Iterable<String> certificateOfElimination(String team) { // subset R of teams that eliminates given team; null if not eliminated
        validateTeam(team);
        if (!isEliminated(team)) return null;

        ArrayList<String> teamSubset = new ArrayList<>();

        int teamPotential = wins(team) + remaining(team);
        if (isTriviallyEliminated(team)) {
            for (String otherTeam: teams) {
                if (otherTeam.equals(team)) continue;
                if (wins(otherTeam) > teamPotential) teamSubset.add(otherTeam);
            }
            return teamSubset;
        } else {
            for (int i = 0; i < teamsWithGames.size(); i++) {
                if (fordFulkerson.inCut(i)) {
                    teamSubset.add(teamsWithGames.get(i));
                }
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


        boolean[] teamHasMatch = new boolean[numberOfTeams];

        sourceNode = numberOfTeamsWithGames;
        targetNode = numberOfTeamsWithGames + 1;

//        System.out.println("numberOfTeamsWithGames: " + numberOfTeamsWithGames);
//        System.out.println("numberOfVertices: " + numberOfVertices);
//        System.out.println("numberOfMatchUps: " + numberOfMatchUps(teamIndex));
//        System.out.println("sourceNode: " + sourceNode);
//        System.out.println("targetNode: " + targetNode);


        int numberOfMatchUps = targetNode;
        for (int i = 0; i < numberOfTeamsWithGames; i++) {
            String teamI = teamsWithGames.get(i);
            for (int j = i + 1; j < numberOfTeamsWithGames; j++) {
                String teamJ = teamsWithGames.get(j);
                double capacity = g[teamIndex(teamI)][teamIndex(teamJ)];
                if (capacity > 0) {
                    numberOfMatchUps++;
                    int matchUpNode = numberOfMatchUps;
//                    System.out.println("game node: " + matchUpNode);
//                    System.out.println(sourceNode + "-(" + capacity + ")->" + matchUpNode);
                    flowNetwork.addEdge(new FlowEdge(sourceNode, matchUpNode, capacity));
//                    System.out.println(matchUpNode + "-(oo)->" + i);
                    flowNetwork.addEdge(new FlowEdge(matchUpNode, i, Double.POSITIVE_INFINITY));
//                    System.out.println(matchUpNode + "-(oo)->" + j);
                    flowNetwork.addEdge(new FlowEdge(matchUpNode, j, Double.POSITIVE_INFINITY));

                    if (!teamHasMatch[i]) {
//                        double c1 = teamPotential - w[teamIndex(teamI)];
//                        System.out.println(i + "-(" + c1 + ")->" + targetNode);
                        flowNetwork.addEdge(new FlowEdge(i, targetNode, teamPotential - w[teamIndex(teamI)]));
                    }
                    if (!teamHasMatch[j]) {
//                        double c2 = teamPotential - w[teamIndex(teamJ)];
//                        System.out.println(j + "-(" + c2 + ")->" + targetNode);
                        flowNetwork.addEdge(new FlowEdge(j, targetNode, teamPotential - w[teamIndex(teamJ)]));
                    }
                    teamHasMatch[i] = true;
                    teamHasMatch[j] = true;
                }
            }
        }

        return flowNetwork;
    }

    private ArrayList<String> teamsWithGames(int teamIndex) {
        teamsWithGames = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) continue;
            for (int j = 0; j < numberOfTeams; j++) {
                if (j == teamIndex) continue;
                if (g[i][j] > 0) {
                    teamsWithGames.add(teams[i]);
                    break;
                }
            }
        }
        return teamsWithGames;
    }

    private int numberOfTeamsWithGames(int teamIndex) {
        teamsWithGames = teamsWithGames(teamIndex);
        return teamsWithGames.size();
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

    public static void main(String[] args) {
        String file = "/Users/nlu/Downloads/baseball/teams4b.txt";
        In input = new In(file);
        int n = Integer.parseInt(input.readLine());
        String[] teams = new String[n];
        int i = 0;
        while (input.hasNextLine()) {
            String line = input.readLine();
            if (line.isBlank()) {
                break;
            }
            line = line.strip();
            String[] fields = line.split("\\s+");
            teams[i] = fields[0];
            i += 1;
        }

        BaseballElimination be = new BaseballElimination(file);

//        FlowNetwork fn = be.createFlowNetwork("Princeton");

        for (String team : teams) {
            System.out.println("team: " + team);
//            FlowNetwork fn = be.createFlowNetwork(team);
//            System.out.println(fn.toString());

            System.out.println("check elimination");
            if (be.isEliminated(team)) {
                System.out.println("is eliminated");
                for (String t : be.certificateOfElimination(team)) {
                    System.out.println(t);
                }
            } else {
                System.out.println("not eliminated");
            }
        }

    }
}

