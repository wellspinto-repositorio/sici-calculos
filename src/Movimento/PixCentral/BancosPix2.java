/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Movimento.PixCentral;

import java.util.List;

/**
 *
 * @author softelet
 */
public class BancosPix2 {
    private String banco;
    private String nomeBanco;
    private List<PessoasPix> pessoasPix;
    private boolean tag;
    
    public BancosPix2(String banco, String nomeBanco, List<PessoasPix> pessoasPix) {
        super();
        this.banco = banco;
        this.nomeBanco = nomeBanco;
        this.pessoasPix = pessoasPix;
    }
    
    public BancosPix2(String banco, String nomeBanco, List<PessoasPix> pessoasPix, boolean tag) {
        super();
        this.banco = banco;
        this.nomeBanco = nomeBanco;
        this.pessoasPix = pessoasPix;
        this.tag = tag;
    }
    
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    
    public String getNomeBanco() { return nomeBanco; }
    public void setNomeBanco(String banco) { this.nomeBanco = nomeBanco; }
    
    public List<PessoasPix> getPessoasPix() { return pessoasPix; }
    public void setPessoasPix(List<PessoasPix> pessoasPix) { this.pessoasPix = pessoasPix; }
    
    public boolean getTag() { return tag; }
    public void setTag(boolean tag) { this.tag = tag; }
}
