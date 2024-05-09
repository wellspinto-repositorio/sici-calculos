package Sici.Partida.DragAndDrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class SimpleListDTSManagerStrings extends TransferHandler {
    @Override
    public int getSourceActions(JComponent c) {
        return (TransferHandler.COPY_OR_MOVE);
    }     
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Transferable createTransferable(JComponent c) {
            //11.1: Get List of Selection
            JList DragSrc = (JList) c;
            List<String> values = DragSrc.getSelectedValuesList();

            //11.2: Prepare Line of Strings. One Line for one item
            String data = "";
            for (String str: values)
            {
              data = data + str + "\n";
            }
            data = data.trim(); 
            return (new StringSelection(data)); 
    } 

    @Override
    public boolean canImport(TransferSupport support) {
            Boolean proceed = false;
            if (support.isDataFlavorSupported(DataFlavor.stringFlavor))
              proceed = true;
            return proceed;
    }     
    
    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    @Override
    public boolean importData(TransferSupport support) {
            //13.1 Get the Exported Data
            Transferable ExportTfr = support.getTransferable();
            String ExportedData = "";
            String[] ListData = {};
            try
            {
              ExportedData = (String) 
              ExportTfr.getTransferData(DataFlavor.stringFlavor);
              ListData = ExportedData.split("\n");
            }
            catch (UnsupportedFlavorException e){}
            catch (IOException e){}

            //13.2 Get Drop Traget List Box & Drop Location
            JList DropTarget = (JList) support.getComponent();
            JList.DropLocation DropLocation = 
                    (JList.DropLocation) support.getDropLocation();

            //13.3 Move/Insert? Also get the Index Position
            boolean isReplace =   !DropLocation.isInsert();
            int Index = DropLocation.getIndex();
            DefaultListModel TagetDLModel = 
            (DefaultListModel) DropTarget.getModel();
            if (isReplace)
            {
              for (String item: ListData)
                    TagetDLModel.set(Index++, item);
            }
            else
            {
              for (String item: ListData)
                    TagetDLModel.add(Index++, item);
            }

            return true;
    }    
        
    @SuppressWarnings("rawtypes")
    protected void exportDone(JComponent c, Transferable data, int action) {
            if (action == TransferHandler.MOVE)
            {
              JList srcList = (JList) c;
              int[] indices = srcList.getSelectedIndices();
              DefaultListModel SrcDLModel = 
              (DefaultListModel) srcList.getModel();
              for (int i = indices.length; i > 0 ; i--)
                    SrcDLModel.removeElementAt(indices[i-1]);
            }
    }    
}
