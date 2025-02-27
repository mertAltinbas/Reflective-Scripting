import javax.swing.*;
import java.awt.*;

class StyledListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel) {
            JLabel label = (JLabel) c;
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            if (!isSelected) {
                label.setForeground(Color.BLACK);
            }
        }
        return c;
    }
}
