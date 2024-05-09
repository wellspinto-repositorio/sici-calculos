package BancosDigital.Santander;

import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.StringManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author SAMIC
 */
public class SantanderPagador {
    private String nsuCode; // [TST] qunado for teste + remessa 20 DIGITOS
    private String nsuDate = "0001-01-01"; // Data da remessa YYYY-MM-DD
    private String environment = "TESTE"; // TESTE/PRODUCAO
    private String covenantCode = "0"; // Código do Convênio (Beneficiario) 9(9)
    private String payer_documentType = "CPF"; // CPF/CNPJ
    private String payer_documentNumber = "000000000000000"; // Número do CPF/CNPJ 9(15)
    private String payer_name = ""; // nome do pagador X(40)
    private String payer_address = ""; // endereço X(40)
    private String payer_neighborhood = ""; // bairro X(30)
    private String payer_city = ""; // cidade X(20)
    private String payer_state = ""; // Estado X(2)
    private String payer_zipCode = ""; // Cep 99999-999
    private String bankNumber = "0"; // Nosso Número 9(13)
    private String clientNumber = "0"; // seu número (contrato) X(15)
    private String dueDate = "01-01-0001"; // Data de Vencimento do boleto YYYY-MM-DD
    private String issueDate = "01-01-0001"; // Data emissão do boleto YYYY-MM-DD
    private BigDecimal nominalValue = new BigDecimal(0); // Valor do boleto 9(13)V99
    private String documentKind =  "DUPLICATA_MERCANTIL"; // Espécie do documento (DUPLICATA_MERCANTIL)
    private BigInteger finePercentage = new BigInteger("0"); // Precentual de multa no mes 9(03)V99
    private String fineQuantityDays = "01"; //Quantidade de dias para cobrar multa 9(02)
    private BigDecimal interestPercentage = new BigDecimal(0); //Percentual de juros ao mes 9(03)V99
    private String writeOffQuantityDays = "30"; // Quantidade de dias para baixa automática 9(02)
    private String paymentType = "REGISTRO"; // pagamento total da boleta
    private String key_type = null; // Tipo de chave QRCODE (CPF/CNPJ/TELEFONE/EMAIL/TEMPID)
    private String key_dictKey = null; // Numero da chave QRCODE X(77)
    private String msg1 = null;
    private String msg2 = null;
    private String msg3 = null;
    private String msg4 = null;
    private String msg5 = null;
    private String msg6 = null;
    private String msg7 = null;
    private String msg8 = null;

    public SantanderPagador() { }
    
    /**
     * 
     * @return Número sequencial único com zeros a esquerda e 20 posições.<br>
     * Quando em fase de teste retorna TST + 17 pocições.
     */
    public String getNsuCode() {
        return (getEnvironment().equalsIgnoreCase("TESTE") ? "TST" : "000") + String.format("%17d", Integer.valueOf(nsuCode)).replace(" ", "0");
    }

    /**
     *
     * @param nsuCode <b>[String]</b> Número sequencial único
     */
    public void setNsuCode(String nsuCode) {
        this.nsuCode = nsuCode;
    }

    public String getNsuDate() {
        return nsuDate;
    }

    public void setNsuDate(String nsuDate) {
        this.nsuDate = Dates.StringtoString(nsuDate, "dd-MM-yyyy", "yyyy-MM-dd");
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCovenantCode() {
        return covenantCode;
    }

    public void setCovenantCode(String covenantCode) {
        this.covenantCode = String.format("%9d", Integer.parseInt(covenantCode));
    }

    public String getPayer_documentType() {
        return payer_documentType.toUpperCase().trim();
    }

    public void setPayer_documentType(String payer_documentType) {
        this.payer_documentType = payer_documentType;
    }

    public String getPayer_documentNumber() {
        return payer_documentNumber.toUpperCase().trim();
    }

    public void setPayer_documentNumber(String payer_documentNumber) {
        this.payer_documentNumber = String.format("%15d", Integer.parseInt(payer_documentNumber.toUpperCase().trim()));
    }

    public String getPayer_name() {
        return (payer_name.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 40)).substring(0, 40);
    }

    public void setPayer_name(String payer_name) {
        this.payer_name = payer_name;
    }

    public String getPayer_address() {
        return (payer_address + FuncoesGlobais.Repete(" ", 40)).substring(0, 40);
    }

    public void setPayer_address(String payer_address) {
        this.payer_address = payer_address.toUpperCase();
    }

    public String getPayer_neighborhood() {
        return (payer_neighborhood.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 30)).substring(0, 30);
    }

    public void setPayer_neighborhood(String payer_neighborhood) {
        this.payer_neighborhood = payer_neighborhood;
    }

    public String getPayer_city() {
        return (payer_city.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 20)).substring(0, 20);
    }

    public void setPayer_city(String payer_city) {
        this.payer_city = payer_city;
    }

    public String getPayer_state() {
        return (payer_state.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 2)).substring(0, 2);
    }

    public void setPayer_state(String payer_state) {
        this.payer_state = payer_state;
    }

    public String getPayer_zipCode() {
        return (payer_zipCode.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 5).substring(0, 5) + "-" + 
                                                     FuncoesGlobais.Repete(" ", 3).substring(0, 3));
    }

    public void setPayer_zipCode(String payer_zipCode) {
        this.payer_zipCode = payer_zipCode;
    }

    public String getBankNumber() {
        return String.format("%13d", Integer.valueOf(bankNumber)).replace(" ", "0");
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getClientNumber() {
        return (clientNumber.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 15)).substring(0, 15);
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getDueDate() {
        return Dates.StringtoString(dueDate, "dd-MM-yyyy", "yyyy-MM-dd");
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getIssueDate() {
        return Dates.StringtoString(issueDate, "dd-MM-yyyy", "yyyy-MM-dd");
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getNominalValue() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        //decimalFormatSymbols.setGroupingSeparator( );
        return new DecimalFormat("###0.00", decimalFormatSymbols).format(nominalValue);
    }

    public void setNominalValue(BigDecimal nominalValue) {
        this.nominalValue = nominalValue;
    }

    public String getDocumentKind() {
        return documentKind;
    }

    public void setDocumentKind(String documentKind) {
        this.documentKind = documentKind.toUpperCase().trim();
    }

    public String getFinePercentage() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        return new DecimalFormat("#,##0.00", decimalFormatSymbols).format(finePercentage);
    }

    public void setFinePercentage(BigInteger finePercentage) {
        this.finePercentage = finePercentage;
    }

    public String getFineQuantityDays() {
        return StringManager.Left(FuncoesGlobais.Repete("0", 2) + fineQuantityDays, 2);
    }

    public void setFineQuantityDays(String fineQuantityDays) {
        this.fineQuantityDays = fineQuantityDays;
    }

    public String getInterestPercentage() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        return new DecimalFormat("#,##0.00", decimalFormatSymbols).format(interestPercentage);
    }

    public void setInterestPercentage(BigDecimal interestPercentage) {
        this.interestPercentage = interestPercentage;
    }

    public String getWriteOffQuantityDays() {
        return StringManager.Left(FuncoesGlobais.Repete("0", 2) + writeOffQuantityDays, 2);
    }

    public void setWriteOffQuantityDays(String writeOffQuantityDays) {
        this.writeOffQuantityDays = writeOffQuantityDays;
    }

    public String getPaymentType() {
        return paymentType.toUpperCase().trim();
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getKey_type() {
        return key_type == null ? null : key_type.toUpperCase().trim(); 
    }

    public void setKey_type(String key_type) {
        this.key_type = key_type;
    }

    public String getKey_dictKey() {
        return StringManager.Right(FuncoesGlobais.Repete("0", 77) + key_dictKey.toUpperCase().trim(), 77); 
    }

    public void setKey_dictKey(String key_dictKey) {
        this.key_dictKey = key_dictKey;
    }

    public String getMsg1() {
        return msg1 == null ? null : StringManager.Left(msg1.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg1(String msg1) {
        this.msg1 = msg1;
    }

    public String getMsg2() {
        return msg2 == null ? null : StringManager.Left(msg2.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg2(String msg2) {
        this.msg2 = msg2;
    }

    public String getMsg3() {
        return msg3 == null ? null : StringManager.Left(msg3.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg3(String msg3) {
        this.msg3 = msg3;
    }

    public String getMsg4() {
        return msg4 == null ? null : StringManager.Left(msg4.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg4(String msg4) {
        this.msg4 = msg4;
    }

    public String getMsg5() {
        return msg5 == null ? null : StringManager.Left(msg5.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg5(String msg5) {
        this.msg5 = msg5;
    }

    public String getMsg6() {
        return msg6 == null ? null : StringManager.Left(msg6.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg6(String msg6) {
        this.msg6 = msg6;
    }

    public String getMsg7() {
        return msg7 == null ? null : StringManager.Left(msg7.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg7(String msg7) {
        this.msg7 = msg7;
    }

    public String getMsg8() {
        return msg8 == null ? null : StringManager.Left(msg8.toUpperCase().trim() + FuncoesGlobais.Repete(" ", 100), 100); 
    }

    public void setMsg8(String msg8) {
        this.msg8 = msg8;
    }
    
    public String build() {
        String xbuild  = "{";
               xbuild += "\"nsuCode\": \"" + getNsuCode() + "\",";        
               xbuild += "\"nsuDate\": \"" + getNsuDate() + "\",";        
               xbuild += "\"environment\": \"" + getEnvironment() + "\",";        
               xbuild += "\"covenantCode\": \"" + getCovenantCode() + "\",";        
               xbuild += "\"issueDate\": \"" + getIssueDate() + "\",";        
               xbuild += "\"dueDate\": \"" + getDueDate() + "\",";        
               xbuild += "\"bankNumber\": \"" + getBankNumber() + "\",";        
               xbuild += "\"clientNumber\": \"" + getClientNumber() + "\",";        
               xbuild += "\"nominalValue\": \"" + getNominalValue() + "\",";        
               xbuild += "\"payer\": {";        
               xbuild += "\"name\": \"" + getPayer_name() + "\",";        
               xbuild += "\"documentType\": \"" + getPayer_documentType() + "\",";        
               xbuild += "\"documentNumber\": \"" + getPayer_documentNumber() + "\",";        
               xbuild += "\"address\": \"" + getPayer_address() + "\",";        
               xbuild += "\"neighborhood\": \"" + getPayer_neighborhood() + "\",";        
               xbuild += "\"city\": \"" + getPayer_city() + "\",";        
               xbuild += "\"state\": \"" + getPayer_state() + "\",";        
               xbuild += "\"zipCode\": \"" + getPayer_zipCode() + "\"";        
               xbuild += "},";        
               xbuild += "\"documentKind\": \"" + getDocumentKind() + "\",";        
               xbuild += "\"paymentType\": \"" + getPaymentType() + "\",";        
               xbuild += "\"messages\": [";        
               if (getMsg1() != null) xbuild += getMsg1();        
               if (getMsg2() != null) xbuild += getMsg2();        
               if (getMsg3() != null) xbuild += getMsg3();        
               if (getMsg4() != null) xbuild += getMsg4();        
               if (getMsg5() != null) xbuild += getMsg5();        
               if (getMsg6() != null) xbuild += getMsg6();        
               if (getMsg7() != null) xbuild += getMsg7();        
               if (getMsg8() != null) xbuild += getMsg8();        
               xbuild += "],";        
               if (getKey_type() != null) {
                   xbuild += "\"key\": {";
                   xbuild += "\"type\": \"" + getKey_type() + "\",";        
                   xbuild += "\"dictKey\": \"" + getKey_dictKey() + "\"";        
                   xbuild += "},";        
               }        
               xbuild += "\"finePercentage\": \"" + getFinePercentage() + "\",";        
               xbuild += "\"FineDate\": \"" + getFineQuantityDays() + "\",";    // retornar vencimento + dias    
               xbuild += "\"interestPercentage\": \"" + getInterestPercentage() + "\",";        
               xbuild += "\"writeOffQuantityDays\": \"" + getWriteOffQuantityDays() + "\"";        
               xbuild += "}";        
        return xbuild;
    }
}
