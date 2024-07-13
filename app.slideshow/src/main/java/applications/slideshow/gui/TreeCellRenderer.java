package applications.slideshow.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import applications.slideshow.model.Directory;

public class TreeCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;
    ImageIcon slideShow;
    ImageIcon directory;

    public TreeCellRenderer(ImageIcon slideshow, ImageIcon directory) {
        this.slideShow = slideshow;
        this.directory = directory;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (isDirectory(value)) {
            setIcon(directory);
            setForeground(Color.orange);
        } else {
            setIcon(slideShow);
            setForeground(new Color(0, 150, 0));
        }
        return this;
    }

    private boolean isDirectory(Object value) {
        Directory directory = (Directory) value;
        return directory.isDirectory();
    }
}
