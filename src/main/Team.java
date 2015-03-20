package main;

import data.Player;

import java.util.LinkedList;

/**
 * Created by Rob on 08/03/2015.
 */
public class Team extends LinkedList<Player> {

    @Override
    public String toString() {
        String str = "";
        for (Player p : this) {
            str += p.getName() + ", ";
        }
        str = str.substring(0, str.length() - 2);

        return str;
    }

}
