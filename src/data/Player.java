package data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rob on 08/03/2015.
 */
public class Player {

    private String name;
    private Team team;
    private List<Integer> value;
    private List<Integer> score;
    private Position position;

    public Player() {
        value = new LinkedList<Integer>();
        score = new LinkedList<Integer>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Integer> getValue() {
        return value;
    }

    public int getValue(int i) {
        return value.get(i);
    }

    public void setValue(int newValue, int window) {
        try {
            value.set(window, newValue);
        } catch (IndexOutOfBoundsException e) {
            value.add(window, newValue);
        }

    }

    public void setValues(List<Integer> values) {
        this.value = values;
    }

    public Integer getScore(int i) {
        return score.get(i);
    }

    public List<Integer> getScores() {
        return score;
    }

    public void setScore(Integer newScore, int window) {
        try {
            score.set(window, newScore);
        } catch (IndexOutOfBoundsException e) {
            score.add(window, newScore);
        }
    }

    public void setScores(List<Integer> scores) {
        this.score = scores;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return name + " - " + position + ", " + team;
    }

}
