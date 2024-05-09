/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.Timer;

/**
 *
 * @author supervisor
 */
public class BackGroundDeskTopPane extends JDesktopPane {
     private ImageIcon image;
     private MediaTracker tracker;
     private int imagePos = 0;
     
     public BackGroundDeskTopPane(String caminhoImagem) {

         image = new ImageIcon(this.getClass().getResource(caminhoImagem));
         tracker = new MediaTracker(this);
         tracker.addImage(image.getImage(), 0);

         try {
             tracker.waitForID(0);
         } catch (InterruptedException exception) {
             exception.printStackTrace();
         }
    }

    public BackGroundDeskTopPane() {
        boolean isExternal = false;
         
        if (VariaveisGlobais.myLogos.size() == 0) {
            if (!new File(VariaveisGlobais.myLogo).exists()) {
                VariaveisGlobais.myLogo = "/Figuras/fundoimobilis.png";
                isExternal = false;
            } else isExternal = true;
        } else {
            isExternal = true;
            VariaveisGlobais.myLogo = VariaveisGlobais.myLogos.get(0);

            if (VariaveisGlobais.imgTimer != 0) {
                ActionListener action = new ActionListener() {
                    public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                        tracker.removeImage(image.getImage(), 0);

                        if (imagePos > VariaveisGlobais.myLogos.size() - 1) imagePos = 0;
                        VariaveisGlobais.myLogo = VariaveisGlobais.myLogos.get(imagePos++);                     
                        image = new ImageIcon((String)VariaveisGlobais.myLogo);

                        // Mescar com Logo
                        image = MesclaLogo(image.getImage(),4,5,5);

                        tracker.addImage(image.getImage(), 0);

                        try {
                            tracker.waitForID(0);
                            repaint();
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }                     
                   }
                };
                Timer t = new Timer((int)(VariaveisGlobais.imgTimer * 1000), action);
                t.start();            
           }             
        }

        if (!new File(VariaveisGlobais.myLogo).exists()) {
            VariaveisGlobais.myLogo = "/Figuras/fundoimobilis.png";
            isExternal = false;
        } 
         
        if (!isExternal) {
            image = new ImageIcon(this.getClass().getResource((String)VariaveisGlobais.myLogo));
        } else {
            image = new ImageIcon((String)VariaveisGlobais.myLogo);
        }         

        // Mescar com Logo
        image = MesclaLogo(image.getImage(),4,5,5);

        tracker = new MediaTracker(this);
        tracker.addImage(image.getImage(), 0);

        try {
            tracker.waitForID(0);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

     public BackGroundDeskTopPane(Object caminhoImagem, boolean isExternal) {
         if (!isExternal) {
            image = new ImageIcon(this.getClass().getResource((String)caminhoImagem));
         } else {
            image = new ImageIcon((String)caminhoImagem);
         }
         tracker = new MediaTracker(this);
         tracker.addImage(image.getImage(), 0);

         try {
             tracker.waitForID(0);
         } catch (InterruptedException exception) {
             exception.printStackTrace();
         }
    }

     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dDesktop = this.getSize();

        double width = dDesktop.getWidth();
        double height = dDesktop.getHeight();

        Image background = new ImageIcon(image.getImage().getScaledInstance(
                (int) width, (int) height, 1)).getImage();

        g.drawImage(background, 0, 0, this);
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    public ImageIcon MesclaLogo(Image img, int allign, int Xmargim, int Ymargim) {
        Image marca = new ImageIcon(this.getClass().getResource("/Figuras/marca.png")).getImage();
        
        int widthImg = img.getWidth(null);
        int heightImg = img.getHeight(null);
        
        int widthMarca = marca.getWidth(null);
        int heightMarca = marca.getHeight(null);
        
        BufferedImage bil = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_ARGB);
        
        Graphics g = bil.getGraphics();
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        
        // Coloca a imagem principal 
        g.drawImage(img, 0, 0, null);

        // 0 - top left; 1 - top right; 2 - center; 3 - bottom left; 4 - bottom right
        int x = 0; int y = 0;
        if (allign == 0) {
            x = 0 + Xmargim;
            y = 0 + Ymargim;
        } else if (allign == 1) {
            x = widthImg - widthMarca - Xmargim;
            y = 0 + Ymargim;
        } else if (allign == 2) {
            x = (widthImg - widthMarca + Xmargim) / 2;
            y = (heightImg - heightMarca + Ymargim) / 2;
        }  else if (allign == 3) {
            x = 0 + Xmargim;
            y = heightImg - heightMarca - Ymargim;
        }  else if (allign == 4) {
            x = widthImg - widthMarca - Xmargim;
            y = heightImg - heightMarca - Ymargim;
        }

        // Desenha na figura alinhada a marca d´agua
        g.drawImage(marca, x, y, null);
        
        return new ImageIcon(bil);
    }
}
