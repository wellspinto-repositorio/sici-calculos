package Sici.Partida.Config;

import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrintersList {

    public PrintersList() {}

    public List<PrintersListClass> list() {
        // Obtém todas as impressoras disponíveis no sistema
        PrintService[] impressoras = PrintServiceLookup.lookupPrintServices(null, null);

        // Obtém a impressora padrão
        PrintService impressoraPadrao = PrintServiceLookup.lookupDefaultPrintService();

        // Lista das impressoras no sistema
        List<PrintersListClass> printers = new ArrayList<>();
        
        // Itera sobre todas as impressoras
        for (PrintService impressora : impressoras) {

            // Verifica se a impressora é de rede
            boolean ehDeRede = false;
            String enderecoIP = "";
            if (impressora.getAttribute(javax.print.attribute.standard.PrinterIsAcceptingJobs.class) != null
                    && impressora.getAttribute(javax.print.attribute.standard.PrinterIsAcceptingJobs.class)
                            .equals(true)
                    && impressora.getAttribute(javax.print.attribute.standard.PrinterLocation.class) != null
                    && impressora.getAttribute(javax.print.attribute.standard.PrinterLocation.class)
                            .toString().contains("://")) {
                ehDeRede = true;

                // Obtém o endereço IP da impressora
                enderecoIP = impressora.getAttribute(javax.print.attribute.standard.PrinterLocation.class)
                        .toString().substring(2);
            }

            // Obtém o nome da impressora
            String nomeImpressora = impressora.getName();

            // Obtém o nome compartilhado da impressora, se existir
            String nomeCompartilhado = null;
            if (impressora.getAttribute(javax.print.attribute.standard.PrinterName.class) != null) {
                nomeCompartilhado = impressora.getAttribute(javax.print.attribute.standard.PrinterName.class)
                        .toString();
            }

            // Verifica se a impressora é a padrão
            boolean ehPadrao = impressora.equals(impressoraPadrao);

            // Imprime as informações da impressora
            printers.add(new PrintersListClass(
                    ehDeRede ? enderecoIP : "127.0.0.1",
                    nomeImpressora,
                    nomeCompartilhado != null ? nomeCompartilhado : nomeImpressora,
                    ehPadrao
            ));
        }
        return printers;
    }
}
