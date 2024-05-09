package Funcoes;

public class Capitular {
    private String _value = null;

    public String getValue() { return _value; }
    
    public Capitular(String value) {
        String[] palavras = value.toLowerCase().split(" ");
	for (int i=0; i<palavras.length; i++) {
            palavras[i] = palavras[i].substring(0,1).toUpperCase() + palavras[i].substring(1);
	}        
        String texto = ""; for (String var : palavras) {texto += var + " ";}
        // exeçoes
        texto = texto.replaceAll(" Do ", " do ");
        texto = texto.replaceAll(" Dos ", " dos ");
        texto = texto.replaceAll(" Da ", " da ");
        texto = texto.replaceAll(" Das ", " das ");
        texto = texto.replaceAll(" De ", " de ");
        texto = texto.replaceAll(" E ", " e ");
        texto = texto.replaceAll(" Es ", " es ");
        texto = texto.replaceAll(" O ", " o ");
        texto = texto.replaceAll(" Os ", " os ");
        texto = texto.replaceAll(" A ", " a ");
        texto = texto.replaceAll(" As ", " as ");        
        this._value = texto.trim();
    }
    
}