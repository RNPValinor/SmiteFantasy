package graphics;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rob on 09/03/2015.
 */
public class BasicFrame extends JFrame {

    private BasicPanel panel;
    private Dimension size;

    public BasicFrame(String name, BasicPanel panel) {
        this(name, panel, 1280, 720);
    }

    public BasicFrame(String name, BasicPanel panel, int width, int height) {
        super(name);
        this.panel = panel;
        size = new Dimension(width, height);
        initFrame();
    }

    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(size);
        add(panel);
        setVisible(true);
    }

}
