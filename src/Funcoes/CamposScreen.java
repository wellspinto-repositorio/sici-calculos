package Funcoes;

import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class CamposScreen {

    public CamposScreen() {}

    public CamposScreen(JTextField oTextField, String tipo) {
        if (tipo.trim().toUpperCase().equalsIgnoreCase("CPF")) {
            oTextField = RemoveActions(oTextField);
            MaskCPF(oTextField);
        } else if (tipo.trim().toUpperCase().equalsIgnoreCase("CEP")) {
            MaskCEP(oTextField);
        } else if (tipo.trim().toUpperCase().equalsIgnoreCase("CNPJ")) {
            oTextField = RemoveActions(oTextField);
            MaskCNPJ(oTextField);
        } else if (tipo.trim().toUpperCase().equalsIgnoreCase("DATA")) {
            MaskDATA(oTextField);
        } else if (tipo.trim().toUpperCase().equalsIgnoreCase("HORA")) {
            MaskHORA(oTextField);
        }
    }
    
    public JTextField RemoveActions(JTextField oTextField) {
        //FocusListener[] focusListeners = oTextField.getFocusListeners();
        //for (FocusListener item : focusListeners) oTextField.removeFocusListener(item);
        KeyListener[] releaseds = oTextField.getKeyListeners();
        for (KeyListener item : releaseds) oTextField.removeKeyListener(item);
        return oTextField;
    }
    
    public CamposScreen(JTextField oTextField, String tipo, int qtdeDigitos, int parteDecimal) {
        if (tipo.trim().toUpperCase().equalsIgnoreCase("VALOR")) {
            MaskVALOR(oTextField, qtdeDigitos, parteDecimal);
        }
    }

    public void MaskCPF(JTextField cpfTextField) {        
        cpfTextField.setHorizontalAlignment(JTextField.LEFT);
         cpfTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {                
                String cpf = cpfTextField.getText();
                String digito = Character.toString(evt.getKeyChar());
                if (!"0123456789".contains(digito)) {
                    try { cpf = cpf.substring(0, cpf.length() - 1);} catch (StringIndexOutOfBoundsException ex) {}
                }
                if (cpf.length() >= 14) {
                    try {cpf = cpf.substring(0,14); } catch (StringIndexOutOfBoundsException ex) {}
                    evt.consume();                    
                } 
                String cpfFormatado = formatarCampoCPF(cpf);
                cpfTextField.setText(cpfFormatado);               
            }
        });
         
        cpfTextField.addFocusListener( new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (cpfTextField.getText().isEmpty()) return;
                
                if (cpfTextField.getText().replaceAll("[^0-9]", "").length() != 11) return;
                
                if (!validarCPF(cpfTextField.getText())) {
                    JOptionPane.showMessageDialog(null, "Cpf digitado é inválido!", "Verifica CPF",JOptionPane.ERROR_MESSAGE);
                    cpfTextField.requestFocus();
                }
            }
            
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {}
        });
    }
    
    private String formatarCampoCPF(String cpfTextField) {
        if (cpfTextField.isEmpty()) return "";
        
        String textoSemFormato = cpfTextField.replace(".", "").replace("-", ""); // cpfTextField.getText().replaceAll("[^0-9]", "");
                
        StringBuilder cpfFormatado = new StringBuilder();
        for (int i = 0; i < textoSemFormato.length(); i++) {
            cpfFormatado.append(textoSemFormato.charAt(i));
            if (i == 2 || i == 5) {
                cpfFormatado.append(".");
            } else if (i == 8) {
                cpfFormatado.append("-");
            }
        }

        return cpfFormatado.toString();
    }
    
    private boolean validarCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se o CPF tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito > 9) {
            primeiroDigito = 0;
        }

        // Calcula o segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito > 9) {
            segundoDigito = 0;
        }

        // Verifica se os dígitos calculados são iguais aos dígitos informados
        return Character.getNumericValue(cpf.charAt(9)) == primeiroDigito &&
               Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }    
    
    public void MaskCEP(JTextField cepTextField) {       
        cepTextField.setHorizontalAlignment(JTextField.LEFT);
        cepTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {                
                String cep = cepTextField.getText();
                String digito = Character.toString(evt.getKeyChar());
                if (!"0123456789".contains(digito)) {
                    cep = cep.substring(0, cep.length() - 1);
                }
                if (cep.length() >= 9) {
                    cep = cep.substring(0,9);
                    evt.consume();                    
                } 
                String cepFormatado = formatarCampoCEP(cep);
                cepTextField.setText(cepFormatado);               
            }
        });
         
        cepTextField.addFocusListener( new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
            }
            
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {}
        });
    }

    private String formatarCampoCEP(String cepTextField) {
        if (cepTextField.isEmpty()) return "";
        
        String textoSemFormato = cepTextField.replace(".", "").replace("-", ""); // cpfTextField.getText().replaceAll("[^0-9]", "");
                
        StringBuilder cepFormatado = new StringBuilder();
        for (int i = 0; i < textoSemFormato.length(); i++) {
            cepFormatado.append(textoSemFormato.charAt(i));
            if (i == 4) {
                cepFormatado.append("-");
            }
        }

        return cepFormatado.toString();
    }

    public void MaskCNPJ(JTextField cnpjTextField) {    
        cnpjTextField.setHorizontalAlignment(JTextField.LEFT);
        cnpjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {                
                String cnpj = cnpjTextField.getText();
                String digito = Character.toString(evt.getKeyChar());
                if (!"0123456789".contains(digito)) {
                    try { cnpj = cnpj.substring(0, cnpj.length() - 1); } catch (StringIndexOutOfBoundsException ex) {}
                }
                if (cnpj.length() >= 18) {
                    try { cnpj = cnpj.substring(0,18); } catch (StringIndexOutOfBoundsException ex) {}
                    evt.consume();                    
                } 
                String cnpjFormatado = formatarCampoCNPJ(cnpj);
                cnpjTextField.setText(cnpjFormatado);               
            }
        });
         
        cnpjTextField.addFocusListener( new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (cnpjTextField.getText().replaceAll("[^0-9]", "").length() != 14) return;
                
                if (!validarCNPJ(cnpjTextField.getText())) {
                    JOptionPane.showMessageDialog(null, "Cnpj digitado é inválido!", "Verifica CNPJ",JOptionPane.ERROR_MESSAGE);
                    cnpjTextField.requestFocus();
                }
            }
            
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {}
        });
    }

    private String formatarCampoCNPJ(String cnpjTextField) {
        if (cnpjTextField.isEmpty()) return "";
        
        String textoSemFormato = cnpjTextField.replace(".", "").replace("-", "").replace("/", "");
                
        StringBuilder cnpjformatado = new StringBuilder();
        for (int i = 0; i < textoSemFormato.length(); i++) {
            cnpjformatado.append(textoSemFormato.charAt(i));
             if (i == 1 || i == 4) {
                cnpjformatado.append(".");
            } else if (i == 7) {
                cnpjformatado.append("/");
            } else if (i == 11) {
                cnpjformatado.append("-");
            }
        }

        return cnpjformatado.toString();
    }
    
    private boolean validarCNPJ(String cnpj) {
        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("[^0-9]", "");
        
        // Verifica se o CNPJ tem 14 dígitos
        if (cnpj.length() != 14) return false;

        // Verifica se todos os numeros são iguais
        if (cnpj.matches("(\\d)\\1*")) return false;
        
        // Calcula o primeiro dígito verificador
         int soma = 0;
        int peso = 2;
        for (int i = 11; i >= 0; i--) {
            soma += Integer.parseInt(cnpj.substring(i, i + 1)) * peso;
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int digito1 = 11 - (soma % 11);
        if (digito1 > 9)
            digito1 = 0;

        // Calcula o segundo dígito verificador
        soma = 0;
        peso = 2;
        for (int i = 12; i >= 0; i--) {
            soma += Integer.parseInt(cnpj.substring(i, i + 1)) * peso;
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int digito2 = 11 - (soma % 11);
        if (digito2 > 9)
            digito2 = 0;

        // Verifica se os dígitos calculados correspondem aos dígitos informados
        return (Integer.parseInt(cnpj.substring(12, 13)) == digito1)
                && (Integer.parseInt(cnpj.substring(13, 14)) == digito2);
    }

    public void MaskDATA(JTextField dataTextField) {        
        dataTextField.setHorizontalAlignment(JTextField.CENTER);
         dataTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {                
                String data = dataTextField.getText();
                String digito = Character.toString(evt.getKeyChar());
                if (!"0123456789".contains(digito)) {
                    data = data.substring(0, data.length() - 1);
                }
                if (data.length() >= 10) {
                    data = data.substring(0,10);
                    evt.consume();                    
                } 
                String dataFormatado = formatarCampoDATA(data);
                dataTextField.setText(dataFormatado);               
            }
        });
         
        dataTextField.addFocusListener( new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (!validarData(dataTextField.getText())) {
                    JOptionPane.showMessageDialog(null, "Data digitado é inválido!", "Verifica DATA",JOptionPane.ERROR_MESSAGE);
                    dataTextField.requestFocus();
                }
            }
            
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {}
        });
    }

    private String formatarCampoDATA(String dataTextField) {
        if (dataTextField.isEmpty()) return "";
        
        String textoSemFormato = dataTextField.replace(".", "").replace("-", "").replace("/", "");
                
        StringBuilder dataformatado = new StringBuilder();
        for (int i = 0; i < textoSemFormato.length(); i++) {
            dataformatado.append(textoSemFormato.charAt(i));
             if (i == 1 || i == 3) {
                dataformatado.append("/");
            }
        }

        return dataformatado.toString();
    }
    
    private boolean validarData(String data) {
        if(data.isEmpty()) return true;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);  // Não permite datas inválidas, como 31/02/2022

        try {
            Date parsedDate = sdf.parse(data);
            return true;  // Se chegou até aqui, a data é válida
        } catch (ParseException e) {
            return false; // Se ocorreu uma exceção, a data é inválida
        }
    }    

    private void MaskVALOR(JTextField valorTextField, int qtdeDigitos, int parteDecimal) {      
        valorTextField.setHorizontalAlignment(JTextField.RIGHT);
        valorTextField.addKeyListener(new ValorMasc(valorTextField,qtdeDigitos,parteDecimal));
        valorTextField.addFocusListener( new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (valorTextField.getText().trim().isEmpty()) {
                    valorTextField.setText("0,00");                    
                }

                if (!validarValor(valorTextField.getText())) {
                    JOptionPane.showMessageDialog(null, "Valor digitado é negativo!", "Verifica Valor",JOptionPane.ERROR_MESSAGE);
                    valorTextField.requestFocus();
                }
            }
            
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                valorTextField.setText("0,00");
            }
        });
    }
    
    private boolean validarValor(String valor) {
        double valorformatado = 0;
        try {
            valorformatado = converterStringParaCurrency(valor);
        } catch (ParseException e) { valorformatado = -1; }
        return valorformatado >= 0;
    }    
    
    private double converterStringParaCurrency(String valorString) throws ParseException {
        // Remove os pontos do separador de milhares e substitui a vírgula por ponto
        String valorFormatado = valorString.replaceAll("\\.", "").replace(',', '.');

        // Faz o parse para obter o valor numérico
        return Double.parseDouble(valorFormatado);
    }    
    
    public void MaskHORA(JTextField horaTextField) {        
        horaTextField.setHorizontalAlignment(JTextField.CENTER);
         horaTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {                
                String hora = horaTextField.getText();
                String digito = Character.toString(evt.getKeyChar());
                if (!"0123456789".contains(digito)) {
                    hora = hora.substring(0, hora.length() - 1);
                }
                if (hora.length() >= 5) {
                    hora = hora.substring(0,5);
                    evt.consume();                    
                } 
                String horaFormatado = formatarCampoHORA(hora);
                horaTextField.setText(horaFormatado);               
            }
        });
         
        horaTextField.addFocusListener( new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (!validarHora(horaTextField.getText())) {
                    JOptionPane.showMessageDialog(null, "Hora digitado é inválida!", "Verifica HORA",JOptionPane.ERROR_MESSAGE);
                    horaTextField.requestFocus();
                }
            }
            
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {}
        });
    }

    private String formatarCampoHORA(String horaTextField) {
        if (horaTextField.isEmpty()) return "";
        
        String textoSemFormato = horaTextField.replace(".", "").replace("-", "").replace("/", "").replace(":", "");
                
        StringBuilder horaformatado = new StringBuilder();
        for (int i = 0; i < textoSemFormato.length(); i++) {
            horaformatado.append(textoSemFormato.charAt(i));
             if (i == 1 ) {
                horaformatado.append(":");
            }
        }

        return horaformatado.toString();
    }
    
    private boolean validarHora(String hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);  // Não permite datas inválidas, como 31/02/2022

        try {
            Date parsedDate = sdf.parse(hora);
            return true;  // Se chegou até aqui, a data é válida
        } catch (ParseException e) {
            return false; // Se ocorreu uma exceção, a data é inválida
        }
    }        
}
