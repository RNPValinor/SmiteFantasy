package main;

import data.DB;
import data.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Rob on 08/03/2015.
 */
public class Cruncher {

    private List<Player> jungleList = new LinkedList<Player>();
    private List<Player> midList = new LinkedList<Player>();
    private List<Player> soloList = new LinkedList<Player>();
    private List<Player> supportList = new LinkedList<Player>();
    private List<Player> adcList = new LinkedList<Player>();
    private DB db = DB.getInstance();

    private int initialFunds;
    private int numWindows;

    private int taskLength = 1;
    private int taskComplete = 0;

    public Cruncher(int initialFunds, int numWindows) {
        this.initialFunds = initialFunds;
        this.numWindows = numWindows;

        initCruncher();
    }

    private void initCruncher() {
        List<Player> players = db.loadAllPlayers();
        for (Player p : players) {
            switch(p.getPosition()) {
                case JUNGLE:
                    jungleList.add(p);
                    break;
                case MID:
                    midList.add(p);
                    break;
                case SOLO:
                    soloList.add(p);
                    break;
                case SUPPORT:
                    supportList.add(p);
                    break;
                case ADC:
                    adcList.add(p);
                    break;
            }
        }

        taskLength = jungleList.size() * midList.size() * soloList.size() * supportList.size() * adcList.size();
    }

    public int getLengthOfTask() {
        return taskLength;
    }

    public int getTaskComplete() {
        return taskComplete;
    }


    public TeamPicks compute() {
        TeamPicks bestPicks = new TeamPicks();
        bestPicks.setScore(0);

        ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<TeamPicks>> futures = new LinkedList<Future<TeamPicks>>();

        for (int i = 0; i < jungleList.size(); i++) {
            for (int j = 0; j < midList.size(); j++) {
                for (int k = 0; k < soloList.size(); k++) {
                    for (int l = 0; l < supportList.size(); l++) {
                        for (int m = 0; m < adcList.size(); m++) {
                            Player jun = jungleList.get(i);
                            Player mid = midList.get(j);
                            Player sol = soloList.get(k);
                            Player sup = supportList.get(l);
                            Player adc = adcList.get(m);
                            int teamValue = (jun.getValue(0) + mid.getValue(0) + sol.getValue(0) + sup.getValue(0) + adc.getValue(0));
                            int cash = initialFunds - teamValue;
                            if (cash >= 0) {
                                Team t = new Team();
                                t.addAll(Arrays.asList(jun, mid, sol, sup, adc));
                                TeamPicks tp = new TeamPicks();
                                tp.addPick(t);
                                TeamComputer tc = new TeamComputer(0, tp, cash);
                                futures.add(e.submit(tc));
                            }
                        }
                    }
                }
            }
        }

        for (Future<TeamPicks> future : futures) {
            TeamPicks tp;
            try {
                tp = future.get();
            } catch (InterruptedException exep) {
                exep.printStackTrace();
                continue;
            } catch (ExecutionException exep) {
                exep.printStackTrace();
                continue;
            }

            if (tp.getScore() > bestPicks.getScore()) {
                bestPicks = tp;
            }

            taskComplete++;
        }

        return bestPicks;
    }

    private class TeamComputer implements Callable<TeamPicks> {

        private int startWindow;
        private TeamPicks tp;
        private int cash;

        private int minValue = 4000;
        private int maxValue = 7500;

        public TeamComputer(int startWindow, TeamPicks tp, int cash) {
            this.startWindow = startWindow;
            this.tp = tp;
            this.cash = cash;
        }

        @Override
        public TeamPicks call() {
            return compute(tp, startWindow, cash);
        }

        /**
         * A recursive function to calculate the best team picks.
         * @param picks The picks calculated up to this point in the tree.
         * @param window Which transfer window we are considering.
         * @param startCash The amount of spare cash carried forward.
         * @return The best team picks for the current tree branch.
         */
        private TeamPicks compute(TeamPicks picks, int window, int startCash) {

            if (window >= numWindows - 1) {
                return picks;
            } else {
                int[] scores = new int[(maxValue - minValue) / 5];
                Arrays.fill(scores, 0);

                Team lastTeam = picks.getPicks().peekLast();
                for (Player p : lastTeam) {
                    startCash += p.getValue(window);
                }
                TeamPicks bestPicks = new TeamPicks();
                bestPicks.setScore(0);

                int newWindow = window + 1;
                for (int i = 0; i < jungleList.size(); i++) {
                    for (int j = 0; j < midList.size(); j++) {
                        for (int k = 0; k < soloList.size(); k++) {
                            for (int l = 0; l < supportList.size(); l++) {
                                for (int m = 0; m < adcList.size(); m++) {
                                    Player jun = jungleList.get(i);
                                    Player mid = midList.get(j);
                                    Player sol = soloList.get(k);
                                    Player sup = supportList.get(l);
                                    Player adc = adcList.get(m);
                                    int teamValue = (jun.getValue(newWindow) + mid.getValue(newWindow) +
                                            sol.getValue(newWindow) + sup.getValue(newWindow) + adc.getValue(newWindow));
                                    int teamScore = (jun.getScore(window) + mid.getScore(window) + sol.getScore(window)
                                            + sup.getScore(window) + adc.getScore(window));
                                    if (isSuperseded(scores, teamScore, teamValue)) continue;
                                    scores[(teamValue - minValue) / 5] = teamScore;
                                    int spareCash = startCash - teamValue;
                                    if (spareCash >= 0) {
                                        Team t = new Team();
                                        t.addAll(Arrays.asList(jun, mid, sol, sup, adc));
                                        TeamPicks ntp = picks.clone();
                                        ntp.addPick(t);
                                        ntp.addScore(teamScore);
                                        ntp = compute(ntp, newWindow, spareCash);
                                        if (ntp.getScore() > bestPicks.getScore()) {
                                            bestPicks = ntp;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return bestPicks;
            }
        }

        /**
         * This calculates whether a lower value team has a higher score than the provided score/value combo.
         * @param scores The array of previously calculated scores.
         * @param score The score to be checked.
         * @param value The value to be checked.
         * @return True if there is a team with v <= value and s >= score, false otherwise.
         */
        private boolean isSuperseded(int[] scores, double score, int value) {
            int index = (value - minValue) / 5;
            while (index >= 0) {
                if (scores[index] >= score) {
                    return true;
                }
                index--;
            }
            return false;
        }

    }

}
