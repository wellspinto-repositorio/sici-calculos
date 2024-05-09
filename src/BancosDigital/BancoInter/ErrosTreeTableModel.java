package BancosDigital.BancoInter;

import java.awt.Color;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

public class ErrosTreeTableModel extends AbstractTableModel { 
    private final static String[] COLUMN_NAMES = {"Contrato", "Nome", "Vencimento", "Codigo", "Mensagem"};
    
    private List<BancosErros> bancoBoletaErros;
    
    public ErrosTreeTableModel(List<BancosErros> bancoBoletaErros) {
        super();
        this.bancoBoletaErros = bancoBoletaErros;
        
        UIManager.put("Table.alternateRowColor", new Color(204,204,255));
        UIManager.put("Table.foreground", new Color(0, 0, 0));  
        UIManager.put("Table.selectionBackground", Color.YELLOW);
        UIManager.put("Table.selectionForeground", Color.RED);
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
      return getValueAt(0, column).getClass();
    }

    @Override
    public int getRowCount() {
        return bancoBoletaErros.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String retorno = null;
        switch (columnIndex) {
            case 0: 
                retorno = bancoBoletaErros.get(rowIndex).getContrato();
                break;
            case 1:
                retorno = bancoBoletaErros.get(rowIndex).getNome();
                break;
            case 2:
                retorno = bancoBoletaErros.get(rowIndex).getVencimento();
                break;
            case 3:
                retorno = bancoBoletaErros.get(rowIndex).getCodigo();
                break;
            case 4:
                retorno = bancoBoletaErros.get(rowIndex).getMessage();
                break;                
        }
        return retorno;
    }    
}
