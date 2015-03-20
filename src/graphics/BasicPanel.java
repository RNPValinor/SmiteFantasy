package graphics;

import data.Player;
import main.Team;
import main.Cruncher;
import main.TeamPicks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Rob on 09/03/2015.
 */
public class BasicPanel extends JPanel {

    private TeamPicks tp = null;
    private Cruncher c;
    private boolean computing = true;

    private JButton btnStart;
    private JProgressBar prog;
    private JScrollPane scrollPane;
    private JTable table;
    private Rectangle rect;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ExecutorService executor2 = Executors.newSingleThreadExecutor();

    public BasicPanel(Cruncher c) {
        super();
        this.c = c;

        btnStart = new JButton("Start.");
        btnStart.setSize(200, 50);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executor.submit(new crunchRunner());
                executor2.submit(new ProgressUpdater());
            }
        });

        prog = new JProgressBar(0, c.getLengthOfTask());
        prog.setValue(c.getTaskComplete());
        prog.setStringPainted(true);

        add(btnStart);
        add(prog);

        rect = new Rectangle(200, 200);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (!computing) {
            btnStart.setVisible(false);
            prog.setVisible(false);

            if (table == null) {
                String[] headers = {"Jungle", "Mid", "Solo", "Support", "Hunter"};
                Object[][] data = new Object[tp.getPicks().size()][5];
                for (int i = 0; i < tp.getPicks().size(); i++) {
                    System.out.println("Adding picks.");
                    Team t = tp.getPicks().get(i);
                    List<String> players = new LinkedList<String>();
                    for (Player p : t) {
                        players.add(p.getName());
                    }
                    data[i] = players.toArray();
                }

                table = new JTable(data, headers);
                scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);
                scrollPane.setSize(600, 600);
                add(scrollPane);

                System.out.println("Best score: " + (tp.getScore() / 100f));
            }
        }

        g2.setColor(Color.BLUE.darker());
        g2.draw(rect);

    }

    private class crunchRunner implements Runnable {

        @Override
        public void run() {
            BasicPanel.this.tp = BasicPanel.this.c.compute();
            BasicPanel.this.computing = false;
            BasicPanel.this.repaint();
        }

    }

    private class ProgressUpdater implements Runnable {

        @Override
        public void run() {
            long lastCall = 0;
            Cruncher c = BasicPanel.this.c;
            while (BasicPanel.this.computing) {
                while ((System.currentTimeMillis() - lastCall) < 1000) Thread.yield();
                lastCall = System.currentTimeMillis();
                BasicPanel.this.prog.setValue(c.getTaskComplete());
                System.out.println("Completed: " + c.getTaskComplete() + "/" + c.getLengthOfTask());
            }
        }

    }

}
