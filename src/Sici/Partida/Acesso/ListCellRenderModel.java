package Sici.Partida.Acesso;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRenderModel implements ListCellRenderer {
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    private JLabel label;
    
    public Component getListCellRendererComponent(
            JList list, 
            Object value, 
            int index, 
            boolean isSelected, 
            boolean cellHasFocus) {
        
        label = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setOpaque(true);

        classUsuarios _value = (classUsuarios)value;
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/usuarios.svg",16,16);
        
        String texto = _value.getNome();
        label.setText(texto);
        label.setIcon(icone);
        
        return label;
    }           
}
