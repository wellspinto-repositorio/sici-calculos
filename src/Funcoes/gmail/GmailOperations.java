package Funcoes.gmail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

/**
 *
 * @author YOGA 510
 */
public class GmailOperations {

        public static void EmailSetUp() {
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
        }
        
	public static void sendMessage(Gmail service, String userId, MimeMessage email)
			throws MessagingException, IOException {
		Message message = createMessageWithEmail(email);
		message = service.users().messages().send(userId, message).execute();

		System.out.println("Message id: " + message.getId());
		System.out.println(message.toPrettyString());
	}

	public static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		email.writeTo(baos);
		String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
		Message message = new Message();
		message.setRaw(encodedEmail);
		return message;
	}

	public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException, IOException {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

		email.setFrom(new InternetAddress(from)); //me
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to)); //
		email.setSubject(subject); 

        email.setText(bodyText);
        
		return email;
	}
	
	
	public static MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText, File file) throws MessagingException, IOException {
		EmailSetUp();
                
                Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

		email.setFrom(new InternetAddress(from)); //me
                
                if (to.contains(";") || to.contains(",")) {
                    String sep = "";
                    if (to.contains(";")) sep = ";"; else sep = ",";
                    String[] aTo = to.split(sep);
                    for (String item : aTo) {
                        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(item.replace(sep, ""))); 
                    }
                } else {
                    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to)); //
                }
		email.setSubject(subject); 
        
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		mimeBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);

		mimeBodyPart.setDataHandler(new DataHandler(source));
		mimeBodyPart.setFileName(file.getName());
		

		multipart.addBodyPart(mimeBodyPart);
		email.setContent(multipart,"text/html");
        
        
		return email;
	}
	
	public static MimeMessage createEmailWithAttachments(String to, String from, String subject, String bodyText, String[] file) throws MessagingException, IOException {
		EmailSetUp();
                
                Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

		email.setFrom(new InternetAddress(from)); //me
                
                if (to.contains(";") || to.contains(",")) {
                    String sep = "";
                    if (to.contains(";")) sep = ";"; else sep = ",";
                    String[] aTo = to.split(sep);
                    for (String item : aTo) {
                        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(item.replace(sep, ""))); 
                    }
                } else {
                    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to)); //
                }
		email.setSubject(subject); 
        
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText, "text/html");

                
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

                for (String _file : file) {
                    File fFile = new File(_file);
                    
                    mimeBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(fFile);

                    mimeBodyPart.setDataHandler(new DataHandler(source));
                    mimeBodyPart.setFileName(fFile.getName());

        	    multipart.addBodyPart(mimeBodyPart);
                }

		email.setContent(multipart,"text/html");
        
        
		return email;
	}
	
	public static void sendEmail() throws IOException, GeneralSecurityException, MessagingException {
		
		Gmail service = GmailAPI.getGmailService();
		MimeMessage Mimemessage = createEmail("wellspinto@hotmail.com","me","This my demo test subject","This is my body text");
	
		Message message = createMessageWithEmail(Mimemessage);
		
		message = service.users().messages().send("me", message).execute();
		
		System.out.println("Message id: " + message.getId());
		System.out.println(message.toPrettyString());
	}
	
	public static void sendEmailWithAttachment() throws IOException, GeneralSecurityException, MessagingException {
		
		Gmail service = GmailAPI.getGmailService();
		MimeMessage Mimemessage = createEmailWithAttachment("wellspinto@hotmail.com","me","This my demo test subject","This is my body text",new File("./result.html"));
	
		Message message = createMessageWithEmail(Mimemessage);
		
		message = service.users().messages().send("me", message).execute();
		
		System.out.println("Message id: " + message.getId());
		System.out.println(message.toPrettyString());
	}
	

	public static MimeMessage createHTMLEmailBodyWithAttachment(String to, String subject, String html, String htmlReportPath) throws AddressException, MessagingException {
        
		
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        
        email.setFrom(new InternetAddress("me"));
         
      //For Multiple Email with comma separated ...
        
        String[] split = to.split(",");
        for(int i=0;i<split.length;i++) { 
        	email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(split[i]));
        }
    
        email.setSubject(subject);

        Multipart multiPart = new MimeMultipart("mixed");
        
        //HTML Body 
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=utf-8");  
        multiPart.addBodyPart(htmlPart,0);
       
        
        //Attachments ...
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(new File(htmlReportPath));

		mimeBodyPart.setDataHandler(new DataHandler(source));
		mimeBodyPart.setFileName("results.html");
		multiPart.addBodyPart(mimeBodyPart,1);
		
        
        email.setContent(multiPart);
        return email;
    }
	
	public static void sendEmailWithHTMLBodyAndAttachment() throws IOException, AddressException, MessagingException, GeneralSecurityException {
		
		//HTML parse
        Document doc = Jsoup.parse(new File("./result.html"), "utf-8"); 
        
        Elements Tags = doc.getElementsByTag("html");
        
        String body = Tags.first().html();
     
		String htmlText = "<html>"+ body +"</html>";
	
		
		Gmail service = GmailAPI.getGmailService();
		MimeMessage Mimemessage = createHTMLEmailBodyWithAttachment("wellspinto@hotmail.com,abc@gmail.com", "This is a subject test", htmlText, "./result.html");
	
		Message message = createMessageWithEmail(Mimemessage);
		
		message = service.users().messages().send("me", message).execute();
		
		System.out.println("Message id: " + message.getId());
		System.out.println(message.toPrettyString());
		
		
		
		
		
	}

	public static void main(String[] args) throws IOException, GeneralSecurityException, MessagingException {
		          //sendEmail();
                          //sendEmailWithAttachment();
		sendEmailWithHTMLBodyAndAttachment();
		
	}
}