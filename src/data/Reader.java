package data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by Rob on 07/03/2015.
 */
public class Reader {

    private String filePath;
    private int window;
    private DB db;

    public Reader(String filePath, int window) {
        this.filePath = filePath;
        this.window = window;
        this.db = DB.getInstance();
    }

    public void parseFile() throws IOException {

        File f = new File(filePath);
        Document d = Jsoup.parse(f, null);

        Elements es = d.getElementById("page").children().first().children().last().children();
        while (es.size() == 1) {
            es = es.first().children();
        }
        es = es.first().children().last().children().last().children();

        for (Element e : es) {
            if (e.tagName().equals("article")) {
                parseRow(e);
            }
        }
    }

    /**
     * Takes the given Element and creates a new player from the information.
     * @param row The Element containing the player information.
     */
    private void parseRow(Element row) {
        Player p = new Player();
        boolean found = false;
        setNameTeamPos(row, p);
        if (db.inDatabase(p.getName())) {
            p = db.load(p.getName());
            found = true;
        }

        updatePriceScore(row, p);

        if (found) {
            db.update(p);
        } else {
            db.add(p);
        }
    }

    /**
     * This will set the name, team and position fields of the provided Player object.
     * @param row The HTML Element to get the information from.
     * @param p The player object to update.
     */
    private void setNameTeamPos(Element row, Player p) {
        //Get the right element from the page.
        Element e = row.children().first().children().first().children().last().children().first();

        //This section contains the player name and position.
        Element playerInfo = e.children().first();
        String[] nameAndPos = playerInfo.text().split("-");
        p.setName(nameAndPos[0].trim());
        String position = nameAndPos[1].trim();

        if (position.equals("Solo")) {
            p.setPosition(Position.SOLO);
        } else if (position.equals("Jungle")) {
            p.setPosition(Position.JUNGLE);
        } else if (position.equals("Guardian")) {
            p.setPosition(Position.SUPPORT);
        } else if (position.equals("Hunter")) {
            p.setPosition(Position.ADC);
        } else if (position.equals("Mid")) {
            p.setPosition(Position.MID);
        }

        //This section contains the player team.
        Element teamInfo = e.children().last();
        String teamName = teamInfo.text();
        teamName = teamName.trim();

        if (teamName.equals("Team Dignitas")) {
            p.setTeam(Team.DIG);
        } else if (teamName.equals("Titan")) {
            p.setTeam(Team.TITAN);
        } else if (teamName.equals("Upcoming Stars")) {
            p.setTeam(Team.STARS);
        } else if (teamName.equals("The Name Changers")) {
            p.setTeam(Team.NC);
        } else if (teamName.equals("London Conspiracy")) {
            p.setTeam(Team.LC);
        } else if (teamName.equals("Denial eSports")) {
            p.setTeam(Team.DENIAL);
        } else if (teamName.equals("Melior Morior")) {
            p.setTeam(Team.MM);
        } else if (teamName.equals("Cognitive Gaming")) {
            p.setTeam(Team.COG);
        } else if (teamName.equals("BUSTERS")) {
            p.setTeam(Team.ENEMY);
        } else if (teamName.equals("Cloud 9")) {
            p.setTeam(Team.C9);
        } else if (teamName.equals("SK Gaming")) {
            p.setTeam(Team.FNATIC);
        } else if (teamName.equals("TRIG esports")) {
            p.setTeam(Team.TRIG);
        }
    }

    private void updatePriceScore(Element row, Player p) {
        String value = row.children().get(1).children().first().children().first().children().first().text().trim();
        value = value.substring(1);
        p.setValue(Integer.valueOf(value), window + 1);

        if (window >= 0) {
            String score = row.children().get(1).children().last().children().first().text();
            if (score.equals("-")) {
                p.setScore(0, window);
            } else {
                int scoreVal = (int) Math.round(Double.valueOf(score) * 100);
                if (window > 0) {
                    scoreVal -= p.getScore(window - 1);
                }
                p.setScore(scoreVal, window);
            }
        }
    }

}
