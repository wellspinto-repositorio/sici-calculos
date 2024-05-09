package Funcoes;

import static Funcoes.StringManager.ConvStr;
import Sici.Partida.Collections;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.activation.FileDataSource;
import javax.mail.Message;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.Recipient;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

/**
 *
 * @author supervisor
 */
public class JEmail {
    Db conn = VariaveisGlobais.conexao;
    String jEmailEmp = ""; String jSenhaEmail = ""; boolean jPop = false; boolean jAutentica = false;
    String jEndPopImap = ""; String jPortPopImap = ""; String jSmtp = ""; String jPortSmtp = "";
    String jAssunto = ""; String jMsgEmail = ""; String jFTP_Conta = ""; String jFTP_Porta = "";
    String jFTP_Usuario = ""; String jFTP_Senha = "";

    public String SendEmail(String contrato, String[] anexo, Object SubJect, Object MSG) throws MalformedURLException, SQLException {
        try {
            LerEmailSettings();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Object[][] EmailLocaDados = null;
        String EmailLoca = null;
        String LocaNome = null;
        if (LerValor.isNumeric(contrato)) {
            EmailLocaDados = conn.ReadFieldsTable(new String[] {"nomerazao","email"}, "locatarios", "contrato = '" + contrato + "'");
            EmailLoca = EmailLocaDados[1][3].toString();
            LocaNome = EmailLocaDados[0][3].toString();
        } else {
            EmailLoca = contrato;
            LocaNome = contrato;
        }
           
        String retorno = "";

        Collections gVar = VariaveisGlobais.dCliente;
       
        Collection<Recipient> cr = new ArrayList<>();
        //cr.add(new Recipient("Wellington de Souza Pinto", "wellspinto@gmail.com", Message.RecipientType.TO));
       
        String emailSeparator = EmailLoca.contains(",") ? "," : EmailLoca.contains(";") ? ";" : EmailLoca.contains(" ") ? " " : ",";
        String[] aEmailLoca = EmailLoca.split(emailSeparator);
        for (String iEmail : aEmailLoca) {
            cr.add(new Recipient(LocaNome, iEmail.trim(), Message.RecipientType.TO));
        }
       
        String tSubJect = "";
        if (SubJect != null) {
            tSubJect = (String)SubJect;
        } else tSubJect = "Boleto " + anexo;

        String tMsg = "";
        if (MSG != null) {
            tMsg = (String)MSG;
        } else tMsg = jAssunto.trim();

        List<AttachmentResource> lista = new ArrayList<>();
        if (anexo != null) {
            for (String item : anexo) lista.add(new AttachmentResource("Anexo", new FileDataSource(item)));
        }

        // Create the email message
        try {
       
            Email email = EmailBuilder.startingBlank()
                .from(ConvStr(gVar.get("empresa").trim()), jEmailEmp.trim())
                .to(cr)
                .withSubject(tSubJect)
                //.withPlainText("We should meet up! ;)")
                .withHTMLText(tMsg)
                .withAttachments(lista)
                //.withDispositionNotificationTo()
                //.withReturnReceiptTo()
                .buildEmail();

            Mailer mailer = null;
            if (VariaveisGlobais.withPool) {
                mailer = MailerBuilder
                  .withSMTPServer(jSmtp.trim(), Integer.valueOf(jPortSmtp), jEmailEmp.trim(), jSenhaEmail.trim())
                  .withTransportStrategy(TransportStrategy.SMTP_TLS)
                  //.withProxy("socksproxy.host.com", 1080, "proxy user", "proxy password")
                  .withSessionTimeout(10 * 1000)
                  .withDebugLogging(VariaveisGlobais.debug)
                  .withThreadPoolSize(1)
                  .withConnectionPoolCoreSize(1)
                  .withConnectionPoolMaxSize(1)      
                  .buildMailer();
            } else {
                mailer = MailerBuilder
                  .withSMTPServer(jSmtp.trim(), Integer.valueOf(jPortSmtp), jEmailEmp.trim(), jSenhaEmail.trim())
                  .withTransportStrategy(TransportStrategy.SMTP_TLS)
                  .withSessionTimeout(10 * 1000)
                  .withDebugLogging(VariaveisGlobais.debug)
                  .buildMailer();
            }
            
            mailer.sendMail(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            retorno = ex.getMessage();
        }
           
        return retorno;
    }

    private void LerEmailSettings() throws SQLException {
        jEmailEmp = conn.ReadParameters("EMAIL");
        jSenhaEmail = conn.ReadParameters("EMAILSENHA");
        jPop = ("TRUE".equals(conn.ReadParameters("POP")) ? true : false);
        jAutentica = ("TRUE".equals(conn.ReadParameters("EMAILAUTENTICA")) ? true : false);
        jEndPopImap = conn.ReadParameters("POPIMAP");
        jPortPopImap = conn.ReadParameters("POPIMAPPORT");
        jSmtp = conn.ReadParameters("SMTP");
        jPortSmtp = conn.ReadParameters("SMTPPORT");

        jFTP_Conta = conn.ReadParameters("FTPCONTA");
        jFTP_Porta = conn.ReadParameters("FTPPORTA");
        jFTP_Usuario = conn.ReadParameters("FTPUSUARIO");
        jFTP_Senha = conn.ReadParameters("FTPSENHA");
    }
   
}