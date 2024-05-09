package Funcoes;

import static Funcoes.FuncoesGlobais.backlashReplace;

public class toPreview {
    public toPreview(String outFileName) {
        String defaultFileName = null;
        if (!System.getProperty("os.name").toUpperCase().trim().equals("LINUX")) {
            defaultFileName = backlashReplace(outFileName);
        } else defaultFileName = LinuxTags(outFileName);
        if (VariaveisGlobais.PreviewType.toUpperCase().trim() == "INTERNO") {
            new pdfViewer.pdfViewer(outFileName);
        } else {
            String cmdPrint = VariaveisGlobais.Preview;
            cmdPrint = cmdPrint.replace("[FILENAME]", defaultFileName);
            try {
                ComandoExterno.ComandoExterno(cmdPrint);
            } catch (Exception e) {}
            System.out.println(cmdPrint);
        }
    }
    
    private String LinuxTags(String outFileName) {
        outFileName = outFileName.replace(" ", "\\ ");
        outFileName = outFileName.replace("(", "\\(");
        outFileName = outFileName.replace(")", "\\)");
        
        return outFileName;
    }
    
}
