package Bancos;

import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.Pad;
import Funcoes.VariaveisGlobais;

public class bb {
    private bancos bco = new bancos(VariaveisGlobais.conexao);          
    
    public String CodBar(String vencimento,String valor,String nossonumero) {
        String part1; String part2; String dv;
        part1 = bco.getBanco() + bco.getMoeda();
        part2 = FatorVencimento("10/07/1997", vencimento) + bco.Valor4Boleta(valor) + nossonumero.substring(0, 11) + 
                bco.getAgencia() + FuncoesGlobais.StrZero(bco.getCtaDv(),8) + bco.getCarteira();
        dv = CalcDig11N(part1 + part2); 
        return part1 + dv + part2;
    }

    public String LinhaDigitavel(String codbar, String codbardv, String vencimento, String valortitulo) {
        String campo1, campo2, campo3, campo4, campo5;
        
        campo1 = bco.getBanco() + bco.getMoeda() + codbar.substring(19, 24);
        campo1 += bco.CalcDig10(campo1);
        campo1 = campo1.substring(0, 5) + "." + campo1.substring(5,10);
        
        campo2 = codbar.substring(24, 34);
        campo2 += bco.CalcDig10(campo2);
        campo2 = campo2.substring(0,5) + "." + campo2.substring(5, 11);
        
        campo3 = codbar.substring(34, 44);
        campo3 += bco.CalcDig10(campo3);
        campo3 = campo3.substring(0, 5) + "." + campo3.substring(5, 11);
        
        campo4 = codbardv;
        
        campo5 = FatorVencimento("10/07/1997", vencimento) + bco.Valor4Boleta(valortitulo);
        
        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
    }    
    
    public String NossoNumeroBB(String conta, String value) {
        String valor1 = new Pad(conta,6).RPad() + new Pad(value,5).RPad();
        String valor2 = CalcDig11N(valor1);
        return valor1 + valor2;
    }
    
    public String FatorVencimento(String fator, String vencimento) {
        String retorno = "0000";
        if (vencimento.length() < 8) {retorno = "0000";} else {
            retorno = String.valueOf(Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "MM/dd/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
        }
        
        return retorno;
    }

    public String CalcDig11N(String cadeia) {
        int total= 0; int mult = 2;
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.parseInt(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        int soma = total; // * 10;
        int resto = (soma % 11);
        if (resto == 0 || resto == 1) { 
            resto = 0; 
        } else if (resto >= 10) {
            resto = 1; 
        } else {
            resto = 11 - resto;
        }
        return String.valueOf(resto);
    }
    
}

