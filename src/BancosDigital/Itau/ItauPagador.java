package BancosDigital.Itau;

import Funcoes.Dates;

public class ItauPagador {
    private String etapa_processo_boleto = "validacao"; // validacao ou efetivacao
    private BeneficiarioItau beneficiario;
    private DadosBoletoItau dado_boleto ;    
    private Pagador pagador ;
    private Pessoa pessoa;
    private TipoPessoa tipopessoa;
    private Endereco endereco;
    private DadosIndividuaisBoleto dados_individuais_boleto;
    private String texto_seu_numero;
    private String desconto_expresso = "false";
    private Juros juros;
    private Multa multa;
    private RecebimentoDivergente recebimento_divergente;
    private Protesto protesto;
    private Negativacao negativacao;
    private String message1 = "";
    private String message2 = "";
    private String message3 = "";
    private String message4 = "";
    private String linha_Digitavel;
    private String codigo_Barras;
    
    public ItauPagador() { }
    public String getEtapa_processo_boleto() { return etapa_processo_boleto; }
    public void setEtapa_processo_boleto(String etapa_processo_boleto) { this.etapa_processo_boleto = etapa_processo_boleto; }

    public BeneficiarioItau getBeneficiario() { return beneficiario; }
    public void setBeneficiario(BeneficiarioItau beneficiario) { this.beneficiario = beneficiario; }

    public DadosBoletoItau getDado_boleto() { return dado_boleto; }
    public void setDado_boleto(DadosBoletoItau dado_boleto) { this.dado_boleto = dado_boleto; }

    public Pagador getPagador() { return pagador; }
    public void setPagador(Pagador pagador) { this.pagador = pagador; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public DadosIndividuaisBoleto getDados_individuais_boleto() { return dados_individuais_boleto; }
    public void setDados_individuais_boleto(DadosIndividuaisBoleto dados_individuais_boleto) { this.dados_individuais_boleto = dados_individuais_boleto; }

    public String getTexto_seu_numero() { return texto_seu_numero; }
    public void setTexto_seu_numero(String texto_seu_numero) { this.texto_seu_numero = texto_seu_numero; }

    public String getDesconto_expresso() { return desconto_expresso; }
    public void setDesconto_expresso(String desconto_expresso) { this.desconto_expresso = desconto_expresso; }

    public Juros getJuros() { return juros; }
    public void setJuros(Juros juros) { this.juros = juros; }

    public Multa getMulta() { return multa; }
    public void setMulta(Multa multa) { this.multa = multa; }

    public RecebimentoDivergente getRecebimento_divergente() { return recebimento_divergente; }
    public void setRecebimento_divergente(RecebimentoDivergente recebimento_divergente) { this.recebimento_divergente = recebimento_divergente; }

    public Protesto getProtesto() { return protesto; }
    public void setProtesto(Protesto protesto) { this.protesto = protesto; }

    public Negativacao getNegativacao() { return negativacao; }
    public void setNegativacao(Negativacao negativacao) { this.negativacao = negativacao; }

    public Pessoa getPessoa() { return pessoa; }
    public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }

    public String getMessage1() { return message1; }
    public void setMessage1(String message1) { this.message1 = message1; }

    public String getMessage2() { return message2; }
    public void setMessage2(String message2) { this.message2 = message2; }

    public String getMessage3() { return message3; }
    public void setMessage3(String message3) { this.message3 = message3; }

    public String getMessage4() { return message4; }
    public void setMessage4(String message4) { this.message4 = message4; }

    public String getLinha_Digitavel() { return linha_Digitavel; }
    public void setLinha_Digitavel(String linha_Digitavel) { this.linha_Digitavel = linha_Digitavel; }

    public String getCodigo_Barras() { return codigo_Barras; }
    public void setCodigo_Barras(String codigo_Barras) { this.codigo_Barras = codigo_Barras; }
        
    public String build() {
        return "{" +
                    "\"data\": {" +
                        "\"etapa_processo_boleto\": \"" + etapa_processo_boleto + "\", " +
                        "\"beneficiario\": {" +
                            "\"id_beneficiario\": \"" + beneficiario.getId_beneficiario() + "\"" +
                        "}" +
                    "}," +
                    "\"dado_boleto\": {" +
                        "\"descricao_instrumento_cobranca\": \"" + dado_boleto.getDescricao_instrumento_cobranca() + "\"," +
                        "\"tipo_boleto\": \"" + dado_boleto.getTipo_boleto() + "\"," +
                        "\"codigo_carteira\": \"" + dado_boleto.getCodigo_carteira() + "\"," +
                        "\"valor_total_titulo\": \"" + dado_boleto.getValor_total_titulo() + "\"," +
                        "\"codigo_especie\": \"" + dado_boleto.getCodigo_especie() + "\"," +
                        "\"pagador\": {" +
                            "\"pessoa\": {" + 
                                "\"nome_pessoa\": \"" + pagador.getPessoa().getNome_pessoa() + "\"," +
                                "\"tipo_pessoa\": {" +
                                    "\"codigo_tipo_pessoa\": \"" + pagador.getPessoa().getTipo_pessoa().getCodigo_tipo_pessoa() + "\"," +
                                    (pagador.getPessoa().getTipo_pessoa().getCodigo_tipo_pessoa().equalsIgnoreCase("F") 
                                    ? "\"numero_cadastro_pessoa_fisica\": \""
                                    : "\"numero_cadastro_nacional_pessoa_juridica\": \""
                                    ) + pagador.getPessoa().getTipo_pessoa().getNumero_cadastro() + "\"" +
                                "}" +
                            "}," +
                        "\"endereco\": {" +
                            "\"nome_logradouro\": \"" + endereco.getNome_logradouro() + "\"," +
                            "\"nome_bairro\": \"" + endereco.getNome_bairro() + "\"," +
                            "\"nome_cidade\": \"" + endereco.getNome_cidade() + "\"," + 
                            "\"sigla_UF\": \"" + endereco.getSigla_UF() + "\"," +
                            "\"numero_CEP\": \"" + endereco.getNumero_CEP() + "\"" +
                        "}" +
                    "}," +
                    "\"dados_individuais_boleto\": [" +
                        "{" +
                            "\"numero_nosso_numero\": \"" + dados_individuais_boleto.getNumero_nosso_numero() + "\"," +
                            "\"data_vencimento\": \"" + dados_individuais_boleto.getData_vencimento() + "\"," +
                            "\"valor_titulo\": \"" + dados_individuais_boleto.getValor_titulo() + "\"," +
                            (dados_individuais_boleto.getData_limite_pagamento() != null
                            ? "\"data_limite_pagamento\": \"" + Dates.DateFormata("yyyy-MM-dd", Dates.DateAdd(Dates.DIA, Integer.parseInt(dados_individuais_boleto.getData_limite_pagamento()), Dates.StringtoDate(dados_individuais_boleto.getData_vencimento(), "yyyy-MM-dd"))) + "\"," : "") +
                            "\"mensagens_cobranca\": [" +
                                "{" +
                                    "\"mensagem\": \"" + message1 + "\"" +
                                "}," +
                                "{" +
                                    "\"mensagem\": \"" + message2 + "\"" +
                                "}," +
                                "{" +
                                    "\"mensagem\": \"" + message3 + "\"" +
                                "}," +
                                "{" +
                                    "\"mensagem\": \"" + message4 + "\"" +
                                "}" +
                            "]" +
                        "}" +
                    "]," +
                    "\"texto_seu_numero\": \"" + texto_seu_numero + "\"," +
                    "\"multa\": {" +
                        "\"codigo_tipo_multa\": \"" + multa.getCodigo_tipo_multa() + "\"," +
                        "\"quantidade_dias_multa\": " + multa.getQuantidade_dias_multa() + "," +
                        "\"percentual_multa\": \"" + multa.getPercentual_multa() + "\"" +
                    "}," +
                    "\"juros\": {" +
                        "\"codigo_tipo_juros\": " + juros.getCodigo_tipo_juros() + "," +
                        "\"quantidade_dias_juros\": " + juros.getData_juros() + "," +
                        "\"percentual_juros\": \"" + juros.getPercentual_juros() + "\"" +
                    "}," +
                    "\"recebimento_divergente\": {" +
                        "\"codigo_tipo_autorizacao\": \"" + recebimento_divergente.getCodigo_tipo_autorizacao() + "\"" +
                    "}," +
                    "\"instrucao_cobranca\": [" +
                        "{" +
                            "\"codigo_instrucao_cobranca\": \"4\"" +
                        "}," +
                        "{" +
                            "\"codigo_instrucao_cobranca\": \"5\"" +
                        "}," +
                        "{" +
                            "\"codigo_instrucao_cobranca\": \"7\"," +
                            "\"quantidade_dias_instrucao_cobranca\": " + (dados_individuais_boleto.getData_limite_pagamento() != null ? dados_individuais_boleto.getData_limite_pagamento() : "30") + "," +
                            "\"dia_util\": true" +
                        "}," +
                        "{" +
                            "\"codigo_instrucao_cobranca\": \"8\"," +
                            "\"quantidade_dias_instrucao_cobranca\": " + (dados_individuais_boleto.getData_limite_pagamento() != null 
                                          ? Integer.parseInt(dados_individuais_boleto.getData_limite_pagamento()) + 1 : "30") + "," +
                            "\"dia_util\": true" +
                        "}" +
                    "]," +
                    "\"protesto\": {" +
                        "\"protesto\": " + protesto.getProtesto() + "" +
                    "}," +
                    "\"desconto_expresso\": " + desconto_expresso + "" +
                "}" +
            "}" +
        "";                
    }
}

    class BeneficiarioItau {
        private String id_beneficiario; // Agência (4 dígitos) + Conta (7 dígitos) + DAC (1 dígito). Exemplo: 150000123450
        public BeneficiarioItau() {}
        public String getId_beneficiario() { return id_beneficiario; }
        public void setId_beneficiario(String id_beneficiario) { this.id_beneficiario = id_beneficiario; }                
    }
    
    class DadosBoletoItau {
        private String descricao_instrumento_cobranca = "boleto";
        private String tipo_boleto = "a vista"; // a vista ou proposta
        private String codigo_carteira = "109";
        private String valor_total_titulo = "00000000000000000"; //9(15)9(2)
        private String codigo_especie = "1"; // 1 - DM; 5 - RC;

        public DadosBoletoItau() { }
        public String getDescricao_instrumento_cobranca() { return descricao_instrumento_cobranca; }
        public void setDescricao_instrumento_cobranca(String descricao_instrumento_cobranca) { this.descricao_instrumento_cobranca = descricao_instrumento_cobranca; }

        public String getTipo_boleto() { return tipo_boleto; }
        public void setTipo_boleto(String tipo_boleto) { this.tipo_boleto = tipo_boleto; }

        public String getCodigo_carteira() { return codigo_carteira; }
        public void setCodigo_carteira(String codigo_carteira) { this.codigo_carteira = codigo_carteira; }

        public String getValor_total_titulo() { return valor_total_titulo; }
        public void setValor_total_titulo(String valor_total_titulo) { this.valor_total_titulo = valor_total_titulo; }

        public String getCodigo_especie() { return codigo_especie; }
        public void setCodigo_especie(String codigo_especie) { this.codigo_especie = codigo_especie; }                
    }
    
    class Pagador {
        private Pessoa pessoa;         

        public Pagador() { }

        public Pessoa getPessoa() { return pessoa;  }
        public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }                
    }
    
    class Pessoa {
        private String nome_pessoa; // x(50)
        private TipoPessoa tipo_pessoa;

        public Pessoa() { }

        public String getNome_pessoa() { return nome_pessoa; }
        public void setNome_pessoa(String nome_pessoa) { this.nome_pessoa = nome_pessoa; }

        public TipoPessoa getTipo_pessoa() { return tipo_pessoa; }
        public void setTipo_pessoa(TipoPessoa tipo_pessoa) { this.tipo_pessoa = tipo_pessoa; }

    }

    class TipoPessoa {
        private String codigo_tipo_pessoa = "F"; // Tipo de pessoa do pagador Pessoa Física - 'F' Pessoa Jurídica - 'J'
        private String numero_cadastro; // CPF do pagador - Obrigatório caso tipo_pessoa = F com 11 numeros (sem pontos, traços ou barras); não informar o campo caso tipo_pessoa = J. Exemplo: 12345678910

        public TipoPessoa() { }
        public String getCodigo_tipo_pessoa() { return codigo_tipo_pessoa; }
        public void setCodigo_tipo_pessoa(String codigo_tipo_pessoa) { this.codigo_tipo_pessoa = codigo_tipo_pessoa; }

        public String getNumero_cadastro() { return numero_cadastro; }
        public void setNumero_cadastro(String numero_cadastro) { this.numero_cadastro = numero_cadastro; }
    }

    class Endereco {
        private String nome_logradouro;
        private String nome_bairro;
        private String nome_cidade;
        private String sigla_UF;
        private String numero_CEP;

        public Endereco() { }
        public String getNome_logradouro() { return nome_logradouro; }
        public void setNome_logradouro(String nome_logradouro) { this.nome_logradouro = nome_logradouro; }

        public String getNome_bairro() { return nome_bairro; }
        public void setNome_bairro(String nome_bairro) { this.nome_bairro = nome_bairro; }

        public String getNome_cidade() { return nome_cidade; }
        public void setNome_cidade(String nome_cidade) { this.nome_cidade = nome_cidade; }

        public String getSigla_UF() { return sigla_UF; }
        public void setSigla_UF(String sigla_UF) { this.sigla_UF = sigla_UF; }

        public String getNumero_CEP() { return numero_CEP; }
        public void setNumero_CEP(String numero_CEP) { this.numero_CEP = numero_CEP; }        
    }
    
    class DadosIndividuaisBoleto {
        private String numero_nosso_numero;
        private String data_vencimento;
        private String valor_titulo;
        private String data_limite_pagamento = null;
        
        public DadosIndividuaisBoleto() { }
        public String getNumero_nosso_numero() { return numero_nosso_numero; }
        public void setNumero_nosso_numero(String numero_nosso_numero) { this.numero_nosso_numero = numero_nosso_numero; }

        public String getData_vencimento() { return data_vencimento; }
        public void setData_vencimento(String data_vencimento) { this.data_vencimento = data_vencimento; }

        public String getValor_titulo() { return valor_titulo; }
        public void setValor_titulo(String valor_titulo) { this.valor_titulo = valor_titulo; }        

        public String getData_limite_pagamento() { return data_limite_pagamento; }
        public void setData_limite_pagamento(String data_limite_pagamento) { this.data_limite_pagamento = data_limite_pagamento; }
        
        
    }
    
    class Juros {
        private String codigo_tipo_juros;
        private String valor_juros;
        private String percentual_juros;
        private String data_juros;

        public Juros() { }
        public String getCodigo_tipo_juros() { return codigo_tipo_juros; }
        public void setCodigo_tipo_juros(String codigo_tipo_juros) { this.codigo_tipo_juros = codigo_tipo_juros; }

        public String getValor_juros() { return valor_juros; }
        public void setValor_juros(String valor_juros) { this.valor_juros = valor_juros; }

        public String getPercentual_juros() { return percentual_juros; }
        public void setPercentual_juros(String percentual_juros) { this.percentual_juros = percentual_juros; }

        public String getData_juros() { return data_juros; }
        public void setData_juros(String data_juros) { this.data_juros = data_juros; }        
    }
    
    class Multa {
        private String codigo_tipo_multa;
        private String valor_multa;
        private String percentual_multa;
        private String quantidade_dias_multa;

        public Multa() { }
        public String getCodigo_tipo_multa() { return codigo_tipo_multa; }
        public void setCodigo_tipo_multa(String codigo_tipo_multa) { this.codigo_tipo_multa = codigo_tipo_multa; }

        public String getValor_multa() { return valor_multa; }
        public void setValor_multa(String valor_multa) { this.valor_multa = valor_multa; }

        public String getPercentual_multa() { return percentual_multa; }
        public void setPercentual_multa(String percentual_multa) { this.percentual_multa = percentual_multa; }

        public String getQuantidade_dias_multa() { return quantidade_dias_multa; }
        public void setQuantidade_dias_multa(String quantidade_dias_multa) { this.quantidade_dias_multa = quantidade_dias_multa; }        
    }
    
    class RecebimentoDivergente {
        private String codigo_tipo_autorizacao = "03";

        public RecebimentoDivergente() { }
        public String getCodigo_tipo_autorizacao() { return codigo_tipo_autorizacao; }
        public void setCodigo_tipo_autorizacao(String codigo_tipo_autorizacao) { this.codigo_tipo_autorizacao = codigo_tipo_autorizacao; }        
    }
    
    class Protesto {
        private String protesto = "false";

        public Protesto() { }
        public String getProtesto() { return protesto; }
        public void setProtesto(String protesto) { this.protesto = protesto; }        
    }
    
    class Negativacao {
        private String negativacao = "false";

        public Negativacao() { }
        public String getNegativacao() { return negativacao; }
        public void setNegativacao(String negativacao) { this.negativacao = negativacao; }                
    }

