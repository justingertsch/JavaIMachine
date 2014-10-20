import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Justin on 10/18/2014.
 */
public class JIMachine extends JFrame
{
    private double screenWidth;
    private double screenHeight;
    private double zoomLevel = 1.00;
    private final double ZOOM = .25;
    private BufferedImage image = null;
    private static final int DEFAULT_W = 1200;
    private static final int DEFAULT_H = 800;
    private JPanel buttonPanel = null;
    private JComponent picPane = null;

    public JIMachine()
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.screenWidth = screenSize.getWidth() / 2;
        this.screenHeight = screenSize.getHeight() / 2;
        setSize((int)this.screenWidth, (int)this.screenHeight);
        setLocationByPlatform(true);

        // Add jcomponent with inner class
        class PicturePane extends JComponent
        {

            @Override
            protected void paintComponent(Graphics g)
            {
                if(JIMachine.this.image == null)
                    return;

                g.drawImage(JIMachine.this.image, 0, 0, JIMachine.this.getImageZoomWidth(), JIMachine.this.getImageZoomHeight(), null);
            }

            @Override
            public Dimension getPreferredSize()
            {
                return new Dimension(DEFAULT_W,DEFAULT_H);
            }

        }

        this.picPane = new PicturePane();
        add(this.picPane);

        // Add buttons
        JButton open = new JButton("Open");
        open.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));

                int result = chooser.showOpenDialog(JIMachine.this);

                // if file selected then load image
                if(result == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        JIMachine.this.image = ImageIO.read(chooser.getSelectedFile());
                        repaint();
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }

                }
            }
        });

        JButton zoomIn = new JButton("Zoom In");
        zoomIn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JIMachine.this.image == null)
                    return;
                JIMachine.this.zoomLevel += (JIMachine.this.zoomLevel * JIMachine.this.ZOOM);
                repaint();
            }
        });

        JButton reset = new JButton("100%");
        reset.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JIMachine.this.image == null)
                    return;
                JIMachine.this.zoomLevel = 1.00;
                repaint();
            }
        });

        JButton zoomOut = new JButton("Zoom Out");
        zoomOut.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JIMachine.this.image == null)
                    return;
                JIMachine.this.zoomLevel -= (JIMachine.this.zoomLevel * JIMachine.this.ZOOM);
                repaint();
            }
        });

        JButton quit = new JButton("Quit");
        quit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });


        // Add buttons to jpanel
        this.buttonPanel = new JPanel();
        this.buttonPanel.add(open);
        this.buttonPanel.add(zoomIn);
        this.buttonPanel.add(reset);
        this.buttonPanel.add(zoomOut);
        this.buttonPanel.add(quit);
        add( this.buttonPanel, BorderLayout.SOUTH);

    }

    private int getImageZoomWidth()
    {

         double width = this.image.getWidth() * this.zoomLevel;
         return (int) width;
    }

    private int getImageZoomHeight()
    {
        double width = this.image.getHeight() * this.zoomLevel;
        return (int) width;
    }



    public static void main(String[] args)
    {
           EventQueue.invokeLater(new Runnable()
           {
              @Override
              public void run()
              {
                  try
                  {
                      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                  }
                  catch (Exception e)
                  {

                  }
                  JFrame frame = new JIMachine();
                  frame.setTitle("JIMachine");
                  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                  frame.setVisible(true);
              }
           });
    }
}
