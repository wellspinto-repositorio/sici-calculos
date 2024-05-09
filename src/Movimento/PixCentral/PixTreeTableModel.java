/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento.PixCentral;

import java.awt.Color;
import java.util.List;
import javax.swing.UIManager;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;


/**
 *
 * @author softelet
 */
public class PixTreeTableModel extends AbstractTreeTableModel {
    private final static String[] COLUMN_NAMES = {"Banco", "Nome", "Vencimento", "Pix", "Tipo", "Rgprp", "Rgimv", "Tag"};
    
    private List<BancosPix2> bancoBoleta;
    
    public PixTreeTableModel(List<BancosPix2> bancoBoleta) {
        super(new Object());
        this.bancoBoleta = bancoBoleta;
        
        UIManager.put("Table.alternateRowColor", new Color(204,204,255));
        UIManager.put("Table.foreground", new Color(0, 0, 0));  //new Color(255, 255, 255)

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
      Class clazz = String.class;
      switch (column) {
        case 7:
          clazz = Boolean.class;
          break;
      }
      return clazz;
    }
    
    @Override
    public boolean isCellEditable(Object node, int column) {
        if (node instanceof BancosPix2) {
            if (column != 7) return false;
        }
        if (node instanceof PessoasPix) {
            if (column != 3 && column != 7) return false;
        }
        return true;
    }

    private void CheckUnCheck(Object value, Object node, int column) {
        if (node instanceof BancosPix2) {
            if (column == 7) {
                BancosPix2 bancosBoleta = (BancosPix2) node;
                for (PessoasPix p : bancosBoleta.getPessoasPix()) {
                    p.setTag((Boolean)value);
                }
            }
        }
    }
    
    @Override
    public boolean isLeaf(Object node) {
        return node instanceof PessoasPix;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof BancosPix2) {
            BancosPix2 bancosBoleta = (BancosPix2) parent;
            return bancosBoleta.getPessoasPix().size();
        }
        return bancoBoleta.size();
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof BancosPix2) {
            BancosPix2 bancosBoleta = (BancosPix2) parent;
            return bancosBoleta.getPessoasPix().get(index);
        }
        return bancoBoleta.get(index);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        BancosPix2 bancosBoleta = (BancosPix2) parent;
        PessoasPix pessoasPix = (PessoasPix) child;
        return bancosBoleta.getPessoasPix().indexOf(pessoasPix);
    }
    
@Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof BancosPix2) {
            BancosPix2 bancosBoleta = (BancosPix2) node;
            switch (column) {
                case 0:
                    return bancosBoleta.getBanco();
                case 1:
                    return bancosBoleta.getNomeBanco();
                case 7:
                    return bancosBoleta.getTag();
            }
        } else if (node instanceof PessoasPix) {
            PessoasPix pessoasPix = (PessoasPix) node;
            switch (column) {
                case 0:
                    return pessoasPix.getContrato();
                case 1:
                    return pessoasPix.getNome();
                case 2:
                    return pessoasPix.getVencimentoRec();
                case 3:
                    return pessoasPix.getVencimentoBol();
                case 4:
                    return pessoasPix.getTipoEnvio();
                case 5:
                    return pessoasPix.getRgprp();
                case 6:
                    return pessoasPix.getRgimv();
                case 7:
                    return pessoasPix.getTag();
            }
        }
        return null;
    }    
    
    @Override
    public void setValueAt(Object value, Object node, int column) {
        if (node instanceof BancosPix2) {
            BancosPix2 bancosBoleta = (BancosPix2) node;
            switch (column) {
                case 0:
                    bancosBoleta.setBanco((String)value);
                    break;
                case 1:
                    bancosBoleta.setNomeBanco((String)value);
                    break;
                case 7:
                    bancosBoleta.setTag((Boolean)value);
                    CheckUnCheck(value, node, column);
            }
        } else if (node instanceof PessoasPix) {
            PessoasPix pessoasPix = (PessoasPix) node;
            switch (column) {
                case 0:
                    pessoasPix.setContrato((String)value);
                    break;
                case 1:
                    pessoasPix.setNome((String)value);
                    break;
                case 2:
                    pessoasPix.setVencimentoRec((String)value);
                    break;
                case 3:
                    pessoasPix.setVencimentoBol((String)value);
                    break;
                case 4:
                    pessoasPix.setTipoEnvio((String)value);
                    break;
                case 5:
                    pessoasPix.setRgprp((String)value);
                    break;
                case 6:
                    pessoasPix.setRgimv((String)value);
                    break;
                case 7:
                    pessoasPix.setTag((Boolean)value);
                    break;
            }
        }
    }
}
