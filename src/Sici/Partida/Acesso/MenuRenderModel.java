package Sici.Partida.Acesso;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MenuRenderModel implements ListCellRenderer {
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    private static final long serialVersionUID = -7799441088157759804L;
    private JLabel label;
    
    public Component getListCellRendererComponent(
            JList list, 
            Object value, 
            int index, 
            boolean isSelected, 
            boolean cellHasFocus) {
        
        label = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        label = new JLabel();
        label = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setOpaque(true);

        classMenu _value = (classMenu)value;
        String _botoes = _value.getBotoes();
        FlatSVGIcon icone = null;
        if (_value.getIcone() != null) {
            icone = new FlatSVGIcon(_value.getIcone(),16,16);
        } else {
            icone = new FlatSVGIcon("menuIcons/nopicture.svg",16,16);
        }
        
        String texto = _value.getDescricao().toString().trim();
        String tooltips = null;
        try { tooltips = _value.getTooltips().toString().trim(); } catch (Exception sqlEx) { tooltips = "Sem Contexto"; }
        if (icone != null) label.setIcon(icone);
        label.setText(texto);
        label.setToolTipText(tooltips);
        
        if (_botoes != null) label.setForeground(new Color(0,153,0));
        
        return label;
    }        
}
