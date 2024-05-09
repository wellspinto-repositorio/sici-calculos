package Funcoes;

import static Funcoes.FuncoesGlobais.backlashReplace;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import org.apache.pdfbox.pdmodel.PDDocument;

public class toPrint {
    public toPrint(String outFileName, String prn, String PrnMode) {
        if (!VariaveisGlobais.statPrinter) return;        
        
        String defaultIpPrinter = null;
        String defaultNamePrinter = null;
        String defaultNamePrinterShare = null;
        String defaultFileName = null;
        
        if (prn.equalsIgnoreCase("THERMICA")) {
            String[] printers = VariaveisGlobais.Thermica.split(",");
            for (String print : printers) {
                String tprint[] = print.split(":");
                if (tprint.length == 4) {
                    defaultIpPrinter = tprint[0];
                    defaultNamePrinter = tprint[1];
                    defaultNamePrinterShare = tprint[2];
                }
            }
        } else {
            String[] printers = VariaveisGlobais.Printer.split(",");
            for (String print : printers) {
                String tprint[] = print.split(":");
                if (tprint.length == 4) {
                    defaultIpPrinter = tprint[0];
                    defaultNamePrinter = tprint[1];
                    defaultNamePrinterShare = tprint[2];
                }
            }
        }
        
        if (!System.getProperty("os.name").toUpperCase().trim().equals("LINUX")) {
            defaultFileName = backlashReplace(outFileName);
        } else defaultFileName = LinuxTags(outFileName);
        
        String cmdPrint = null;
        cmdPrint = VariaveisGlobais.Externo;
        if (cmdPrint == null) cmdPrint = "";
        
        try {
            cmdPrint = cmdPrint.replace("[IP]", defaultIpPrinter);
            cmdPrint = cmdPrint.replace("[PRINTER]", defaultNamePrinter);
            cmdPrint = cmdPrint.replace("[PRINTERSHARE]", defaultNamePrinterShare);
            cmdPrint = cmdPrint.replace("[FILENAME]", defaultFileName);
        } catch (Exception e) {e.printStackTrace();}
        
        if (PrnMode.equalsIgnoreCase("EXTERNA")) {
            try {
                ComandoExterno.ComandoExterno(cmdPrint);
            } catch (Exception e) {}
            System.out.println(cmdPrint);
        } else {
            try {
                PrintService impressora = null;
                PrintService[] pservices = PrinterJob.lookupPrintServices();

                if (pservices.length > 0) {
                    for (PrintService ps : pservices) {
                        System.out.println("Impressora Encontrada: " + ps.getName());

                        if (ps.getName().trim().contains(defaultNamePrinter.trim())) {
                            System.out.println("Impressora Selecionada: " + ps.getName());
                            impressora = ps;
                            break;
                        }
                    }
                }
                if (impressora != null) {
                    PrinterJob pjob = null;
                    pjob = PrinterJob.getPrinterJob();
                    pjob.setPrintService(impressora);

                    PDDocument pdf=PDDocument.load(defaultFileName.replace("\\ ", " "));
                    pdf.silentPrint(pjob);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }    
    
    private String LinuxTags(String outFileName) {
        outFileName = outFileName.replace(" ", "\\ ");
        outFileName = outFileName.replace("(", "\\(");
        outFileName = outFileName.replace(")", "\\)");
        
        return outFileName;
    }
    
    public String[] ListarImp() {
        String[] ret = {};
        PrintService[] pservices = PrinterJob.lookupPrintServices();

        if (pservices.length > 0) {
            for (PrintService ps : pservices) {
                ret = FuncoesGlobais.ArrayAdd(ret, ps.getName());
            }
        }
        
        return ret;
    }
    
}
