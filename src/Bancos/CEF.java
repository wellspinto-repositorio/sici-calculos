package Bancos;

import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;

/**
 *
 * @author supervisor
 */
public class CEF {    
    private bancos bco = new bancos(VariaveisGlobais.conexao);          
    
    public String CodBar(String vencimento,String valor,String nossonumero) {
        String strcodbar;
        strcodbar = bco.getBanco() + bco.getMoeda() + 
                    DigitoVGeral(vencimento, valor, nossonumero) + 
                    FatorVencimento("07/03/2000", vencimento) + 
                    bco.Valor4Boleta(valor) + 
                    campolivre(nossonumero);
        return strcodbar;
    }
    
    public String FatorVencimento(String fator, String vencimento) {
        String retorno = "0000";
        // 07/03/2000 - CEF
        // 07/10/1997 - Itau, Santander
        
        if (vencimento.length() < 8) {retorno = "0000";} else {
            int fat1 = 1000;
            retorno = String.valueOf(fat1 + Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
        }
        
        return retorno;
    }
    
    public String DigitoVGeral(String vencimento, String valor, String nossonumero) {
        String tmpcalc = "";

        tmpcalc = bco.getBanco() + bco.getMoeda() + 
                  FatorVencimento("07/03/2000", vencimento) +
                  bco.Valor4Boleta(valor) + bco.getCarteira() + 
                  bco.getConta() + "9" +
                  fmtnossonumero(nossonumero, 17);

        return CalcDig11(tmpcalc, 9, 2);
    }
    
    public String CalcDig11(String cadeia, int limitesup, int lflag) {

        int mult; int total; int nresto; int ndig; int pos;
        String retorno = "";
        
        mult = 1 + (cadeia.length() % (limitesup - 1));
        if (mult == 1) { mult = limitesup; }
        
        total = 0;
        for (pos=0;pos<=cadeia.length()-1;pos++) {
            total += Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
            mult -= 1;
            if (mult == 1) mult = limitesup;
        }
        
        nresto = (total % 11);
        if (lflag == 1) { retorno = String.valueOf(nresto); } else {
            if (nresto == 0 || nresto == 1) {
                ndig = 0; 
            } else if (nresto > 9) { 
                ndig = 1; 
            } else ndig = 11 - nresto;
            retorno = String.valueOf(ndig);
        }
        return retorno;
    }
    
    public String NossoNumero(String value, int tam) {
        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
                        Integer.valueOf(value).toString().trim(),tam - 1);
        String valor2 = CalcDig11(valor1,9,2);
        return valor1 + valor2;
    }
    
    public String campolivre(String nossonumero) {
        String fixo1 = "1";
        String fixo9 = "9";
        String digito = "";

        digito = fixo1 + bco.getConta() + fixo9 + fmtnossonumero(nossonumero, 16);

        return digito;
    }
    
    public String fmtnossonumero(String nossonumero, int lgth) {
        String tmpcalc;

        tmpcalc =  FuncoesGlobais.StrZero("0", lgth) + 
                   Integer.valueOf(nossonumero).toString().trim();

        return StringManager.Right(tmpcalc, lgth);
    }
    
    public String codbar(String nossonumero, String vencimento, String valor) {
        String strcodbar;

        strcodbar = bco.getBanco() + bco.getMoeda() + 
                    DigitoVGeral(vencimento, valor, nossonumero) +
                    FatorVencimento("07/03/2000", vencimento) + 
                    bco.Valor4Boleta(valor) + campolivre(nossonumero);

        return strcodbar;
    }
    
    public String linhadigitavel(String nossonumero, String vencimento, String valor) {
        String tmpcalc;
        String cmplivre;
        String campo1;
        String campo2;
        String campo3;
        String campo4;
        String campo5;

        //* Campo livre
        cmplivre = campolivre(nossonumero);

        tmpcalc = bco.getBanco() + bco.getMoeda() + StringManager.Mid(cmplivre,1,5);
        campo1 = tmpcalc + bco.CalcDig10(tmpcalc);
        campo1 = StringManager.Left(campo1, 5) + "." + StringManager.Right(campo1, 5);

        tmpcalc = StringManager.Mid(cmplivre, 6, 10);
        campo2 = tmpcalc + bco.CalcDig10(tmpcalc);
        campo2 = StringManager.Left(campo2, 5) + "." + StringManager.Right(campo2, 6);

        tmpcalc = StringManager.Mid(cmplivre, 16, 10);
        campo3 = tmpcalc + bco.CalcDig10(tmpcalc);
        campo3 = StringManager.Left(campo3, 5) + "." + StringManager.Right(campo3, 6);

        campo4 = DigitoVGeral(vencimento, valor, nossonumero);

        campo5 = FatorVencimento("07/03/2000", vencimento) + 
                 bco.Valor4Boleta(valor);

        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
     }    
}