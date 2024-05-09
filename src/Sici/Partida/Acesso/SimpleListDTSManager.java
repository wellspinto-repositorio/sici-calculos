package Sici.Partida.Acesso;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class SimpleListDTSManager extends TransferHandler {
 protected static final DataFlavor FLAVOR = new DataFlavor(List.class, "List of items");
  private final List<Integer> indices = new ArrayList<>();
  private int addIndex = -1; 
  private int addCount; 
  private String sourceName = "";
  
   protected SimpleListDTSManager() {
     super();
   }

  @Override protected Transferable createTransferable(JComponent c) {
    JList<?> source = (JList<?>) c;
    
    sourceName = source.toString();
    
    for (int i : source.getSelectedIndices()) {
      indices.add(i);
    }
    return new Transferable() {
      @Override public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {FLAVOR};
      }

      @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
        return Objects.equals(FLAVOR, flavor);
      }

      @Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (isDataFlavorSupported(flavor)) {
          return source.getSelectedValuesList();
        } else {
          throw new UnsupportedFlavorException(flavor);
        }
      }
    };
  }

  @Override public boolean canImport(TransferHandler.TransferSupport info) {
    return info.isDrop() && info.isDataFlavorSupported(FLAVOR);
  }

  @Override public int getSourceActions(JComponent c) {
    return TransferHandler.MOVE;
  }

  @SuppressWarnings("unchecked")
  @Override public boolean importData(TransferHandler.TransferSupport info) {
    TransferHandler.DropLocation tdl = info.getDropLocation();
    if (!(tdl instanceof JList.DropLocation)) {
      indices.clear();
      addCount = 0;
      addIndex = -1;
      return false;
    }
    
    JList.DropLocation dl = (JList.DropLocation) tdl;
    JList<?> target = (JList<?>) info.getComponent();
    DefaultListModel<Object> listModel = (DefaultListModel<Object>) target.getModel();
    int max = listModel.getSize();
    int index = dl.getIndex();
    index = index >= 0 && index < max ? index : max;
    addIndex = index;
    try {
      List<?> values = (List<?>) info.getTransferable().getTransferData(FLAVOR);
      for (Object o : values) {
        int i = index++;
        listModel.add(i, o);
        target.addSelectionInterval(i, i);
      }
      addCount = values.size();
      return true;
    } catch (UnsupportedFlavorException | IOException ex) {
      indices.clear();
      addCount = 0;
      addIndex = -1;
      return false;
    }
  }

  @Override protected void exportDone(JComponent c, Transferable data, int action) {
    cleanup(c, action == MOVE);
  }

  private void cleanup(JComponent c, boolean remove) {
    if (remove && !indices.isEmpty()) {
      if (addCount > 0) {
        for (int i = 0; i < indices.size(); i++) {
          if (indices.get(i) >= addIndex) {
            indices.set(i, indices.get(i) + (sourceName.equals(c.toString()) ? 0 : addCount));
          }
        }
      }
      JList<?> src = (JList<?>) c;
      DefaultListModel<?> model = (DefaultListModel<?>) src.getModel();
      for (int i = indices.size() - 1; i >= 0; i--) {
        model.remove(indices.get(i));
      }
    }
    indices.clear();
    addCount = 0;
    addIndex = -1;

  }
}