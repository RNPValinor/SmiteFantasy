package main;

import data.Reader;
import graphics.BasicFrame;
import graphics.BasicPanel;

import java.io.File;
import java.io.IOException;

/**
 * Created by Rob on 07/03/2015.
 */
public class main {

    public static void main(String[] args) throws IOException {

        Reader r = new Reader("C:\\Users\\Rob\\IdeaProjects\\Smite Fantasy\\initial.html", -1);
        r.parseFile();

        int i = 0;
        File f = new File("C:\\Users\\Rob\\IdeaProjects\\Smite Fantasy\\window" + i + ".html");

        while (f.exists()) {
            r = new Reader("C:\\Users\\Rob\\IdeaProjects\\Smite Fantasy\\window" + i + ".html", i++);
            r.parseFile();

            f = new File("C:\\Users\\Rob\\IdeaProjects\\Smite Fantasy\\window" + i + ".html");
        }

        Cruncher c = new Cruncher(5500, 2);

        BasicPanel panel = new BasicPanel(c);
        BasicFrame frame = new BasicFrame("Smite Fantasy", panel);

    }

}
