package Funcoes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Criptografia {
      //private static SecretKey skey;  
    /**
     * CHAVE DEVE CONTER ATE 24 CARACTERES
     */
    public static final String ALGORITMO_TRIPLE_DES = "DESede";
    /**
     * CHAVE DEVE CONTER ATE 8 CARACTERES
     */
    public static final String ALGORITMO_DES = "DES";
    /**
     * CHAVE DEVE CONTER ATE 16 CARACTERES
     */
    public static final String ALGORITMO_BLOWFISH = "Blowfish";
    /**
     * CHAVE DEVE CONTER ATE 16 CARACTERES
     */
    public static final String ALGORITMO_AES = "AES";

    private static Map<String, Long> tamanhosChaves = new HashMap<>();

    static {
        tamanhosChaves.put(ALGORITMO_TRIPLE_DES, new Long(24));
        tamanhosChaves.put(ALGORITMO_DES, new Long(8));
        tamanhosChaves.put(ALGORITMO_BLOWFISH, new Long(16));
        tamanhosChaves.put(ALGORITMO_AES, new Long(16));

    }
    
    public static String encrypt(String text, String chave, String algoritmo) throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnsupportedEncodingException, InvalidKeySpecException {
        SecretKey skey = getSecretKey(chave, algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, skey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String text, String chave, String algoritmo) {
        try {
            SecretKey skey = getSecretKey(chave, algoritmo);
            Cipher cipher = Cipher.getInstance(algoritmo);
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[] decodedBytes = Base64.getDecoder().decode(text);
            return new String(cipher.doFinal(decodedBytes));
        } catch (Exception e) {
            return "Chave Incorreta";
        }
    }

    public static void encryptFile(String pathAqruivoOriginal, String pathArquivoDestino, String chave, String algoritmo) throws Exception {
        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;
        SecretKey skey = getSecretKey(chave, algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, skey);

        fis = new FileInputStream(pathAqruivoOriginal);
        cis = new CipherInputStream(fis, cipher);
        fos = new FileOutputStream(pathArquivoDestino);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = cis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        cis.close();
        fis.close();
    }

    public static void decryptFile(String pathAqruivoOriginal, String pathArquivoDestino, String chave, String algoritmo) throws Exception {
        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;
        SecretKey skey = getSecretKey(chave, algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.DECRYPT_MODE, skey);

        fis = new FileInputStream(pathAqruivoOriginal);
        cis = new CipherInputStream(fis, cipher);
        fos = new FileOutputStream(pathArquivoDestino);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = cis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        cis.close();
        fis.close();
    }
    
    /**
     * Utiliza SHA1, este metodo de criptografia nao pode serdesfeito, 
     * uma vez a senha criptografada, ela nao pode ser recuperada 
     * @param password
     * @return senha criptografada com SHA1
     */
    public static String encriptaSenha(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("SHA-512");
            md5.reset();
            md5.update(password.getBytes());
            byte[] digest = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * verifica se uma senha em texto claro é igual a uma senha criptografada com SHA1.
     * @return true se for iigual , false se for diferente
     */
    public static boolean comparaSenhaCriptografada(String passwordClear, String passwordEncriptado) {
        String senha1 = encriptaSenha(passwordClear);
        return senha1 != null && senha1.equals(passwordEncriptado);
    }
    
    public static SecretKey getSecretKey(String chave, String algoritmo) {
        byte[] keyBytes = new byte[Math.toIntExact(tamanhosChaves.get(algoritmo))];
        for (int i = 0; i < chave.length() && i < keyBytes.length; i++) {
            keyBytes[i] = (byte) chave.charAt(i);
        }
        return new SecretKeySpec(keyBytes, algoritmo);
    } 
}