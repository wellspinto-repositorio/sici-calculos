package BancosDigital.Config;

import java.awt.Color;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author desenvolvimento-pc
 */
public class ConfigTableModel extends AbstractTableModel { 
    private final static String[] COLUMN_NAMES = {
                                                    "id", "banco", "bancodv", "agencia", "agenciadv", "conta", "carteira",
                                                    "beneficiario", "operador", "nnumero", "path", "crt", "key", "clientid", "clientsecret"
                                                 };
    
    private List<BancosDigitalDados> bancosDados;
    
    public ConfigTableModel(List<BancosDigitalDados> bancosDados) {
        super();
        this.bancosDados = bancosDados;
        
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
        return bancosDados.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String retorno = null;
        switch (columnIndex) {
            case 0: 
                retorno = String.valueOf( bancosDados.get(rowIndex).getId() );
                break;
            case 1:
                retorno = bancosDados.get(rowIndex).getBanco();
                break;
            case 2:
                retorno = bancosDados.get(rowIndex).getBancodv();
                break;
            case 3:
                retorno = bancosDados.get(rowIndex).getAgencia();
                break;
            case 4:
                retorno = bancosDados.get(rowIndex).getAgenciadv();
                break;                
            case 5:
                retorno = bancosDados.get(rowIndex).getConta();
                break;                
            case 6:
                retorno = bancosDados.get(rowIndex).getCarteira();
                break;                
            case 7:
                retorno = bancosDados.get(rowIndex).getBeneficiario();
                break;                
            case 8:
                retorno = bancosDados.get(rowIndex).getOperador();
                break;                
            case 9:
                retorno = bancosDados.get(rowIndex).getNnumero();
                break;                
            case 10:
                retorno = bancosDados.get(rowIndex).getPath();
                break;                
            case 11:
                retorno = bancosDados.get(rowIndex).getCrtfile();
                break;                
            case 12:
                retorno = bancosDados.get(rowIndex).getKeyfile();
                break;                
            case 13:
                retorno = bancosDados.get(rowIndex).getClientid();
                break;                
            case 14:
                retorno = bancosDados.get(rowIndex).getClientsecret();
                break;                
        }
        return retorno;
    }    
}
