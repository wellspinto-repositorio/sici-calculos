package pdfViewer;

import javax.swing.*;
import java.awt.Dialog;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

public class pdfViewer {
    public pdfViewer(String filePath) {
        // build a controller
        SwingController controller = new SwingController();

        // Build a SwingViewFactory configured with the controller
        SwingViewBuilder factory = new SwingViewBuilder(controller);

        // Use the factory to build a JPanel that is pre-configured
        //with a complete, active Viewer UI.
        JPanel viewerComponentPanel = factory.buildViewerPanel();

        // add copy keyboard command
        ComponentKeyBinding.install(controller, viewerComponentPanel);

        // add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
             new org.icepdf.ri.common.MyAnnotationCallback(
                    controller.getDocumentViewController()));

        // Create a JFrame to display the panel in
        JFrame window = new JFrame();        
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        window.setBounds(bounds);
        window.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        window.setTitle("Pré Visualizazão do Documento...");
        window.getContentPane().add(viewerComponentPanel);
        //window.setAlwaysOnTop(true);
        window.setVisible(true);
        window.pack();
        
        // Open a PDF document to view
        controller.openDocument(filePath);                    
        window.setExtendedState(MAXIMIZED_BOTH);        
    }
}
