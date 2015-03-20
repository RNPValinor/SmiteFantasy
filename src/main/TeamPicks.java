package main;

import java.util.LinkedList;

/**
 * Created by Rob on 08/03/2015.
 */
public class TeamPicks {

    private int score = 0;
    private LinkedList<Team> picks = new LinkedList<Team>();

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LinkedList<Team> getPicks() {
        return picks;
    }

    public void addPick(Team team) {
        picks.addLast(team);
    }

    public void addScore(int score) {
        this.score += score;
    }

    @Override
    public String toString() {
        String str = "";
        for (Team t : picks) {
            str += String.format(t + "%n");
        }
        str += String.format("Score: " + score + "%n");
        return str;
    }

    @Override
    public TeamPicks clone() {
        TeamPicks tp = new TeamPicks();
        tp.score = this.score;
        tp.picks = new LinkedList<Team>(picks);
        return tp;
    }

}
