import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Justin on 10/18/2014.
 */
public class JIMachine extends JFrame
{
    private double screenWidth;
    private double screenHeight;
    private double zoomLevel = 1.00;
    private final double ZOOM = .25;
    private final double MIN_ZOOM = .15;
    private final double MAX_ZOOM = 4.00;
    private BufferedImage image = null;
    private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    private static final int DEFAULT_W = 1000;
    private static final int DEFAULT_H = 667;
    private JPanel buttonPanel = null;
    private JComponent picPane = null;
    private final int INTRAVEL = 1;
    private final int ZERO = 0;
    private File curDir = new File(System.getProperty("user.dir"));

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

                g.drawImage(JIMachine.this.image, 0, 0, JIMachine.this.getImageZoomWidth(), JIMachine.this.getImageZoomHeight(), this);
            }

            @Override
            public Dimension getPreferredSize()
            {
                return new Dimension(DEFAULT_W,DEFAULT_H);
            }

        }

        this.picPane = new PicturePane();
        add(this.picPane, BorderLayout.CENTER);

        // Add buttons
        JButton open = new JButton("Open");
        open.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(JIMachine.this.curDir);

                int result = chooser.showOpenDialog(JIMachine.this);

                // if file selected then load image
                if(result == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        JIMachine.this.image = ImageIO.read(chooser.getSelectedFile());
                        JIMachine.this.curDir = new File(chooser.getSelectedFile().getParent());
                        loadDirectory();
                        syncImage();
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
                if(JIMachine.this.zoomLevel < JIMachine.this.MAX_ZOOM)
                {
                    JIMachine.this.zoomLevel += (JIMachine.this.zoomLevel * JIMachine.this.ZOOM);
                    repaint();
                }
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
                if(JIMachine.this.zoomLevel > JIMachine.this.MIN_ZOOM)
                {
                    JIMachine.this.zoomLevel -= (JIMachine.this.zoomLevel * JIMachine.this.ZOOM);
                    repaint();
                }
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

        JButton prev = new JButton("Previous");
        Action prevAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!JIMachine.this.images.isEmpty())
                {
                    if (JIMachine.this.images.indexOf(JIMachine.this.image) == ZERO)
                    {
                        JIMachine.this.image = JIMachine.this.images.get(JIMachine.this.images.size() - INTRAVEL);
                    } else
                    {
                        JIMachine.this.image = JIMachine.this.images.get(JIMachine.this.images.indexOf(JIMachine.this.image) - INTRAVEL);
                    }
                    repaint();
                }

            }
        };
        prev.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "prevleft");
        prev.getActionMap().put("prevleft", prevAction);
        prev.addActionListener(prevAction );


        JButton next = new JButton("Next");
        Action nextAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!JIMachine.this.images.isEmpty())
                {
                    if (JIMachine.this.images.indexOf(JIMachine.this.image) == JIMachine.this.images.size() - INTRAVEL )
                    {
                        JIMachine.this.image = JIMachine.this.images.get(ZERO);
                    } else
                    {
                        JIMachine.this.image = JIMachine.this.images.get(JIMachine.this.images.indexOf(JIMachine.this.image) + INTRAVEL);
                    }
                    repaint();
                }
            }
        };

        next.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "nextright");
        next.getActionMap().put("nextright", nextAction);
        next.addActionListener(nextAction );


        // Add buttons to jpanel
        this.buttonPanel = new JPanel();
        this.buttonPanel.add(open);
        this.buttonPanel.add(zoomIn);
        this.buttonPanel.add(reset);
        this.buttonPanel.add(zoomOut);
        this.buttonPanel.add(prev);
        this.buttonPanel.add(next);
        this.buttonPanel.add(quit);
        add( this.buttonPanel, BorderLayout.SOUTH);
        loadDirectory();
        setImage();

    }

    private int getImageZoomWidth()
    {

         double width = (this.image.getWidth() * this.zoomLevel);
         return (int) width;
    }

    private int getImageZoomHeight()
    {
        double height = (this.image.getHeight() * this.zoomLevel);
        return (int) height;
    }

    private void loadDirectory()
    {
        ArrayList<BufferedImage> ims = new ArrayList<BufferedImage>();
        File[] f = JIMachine.this.curDir.listFiles();
        for (File file : f)
        {
            if (file != null && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg")))
            {
                try
                {
                    ims.add(ImageIO.read(file));
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        if(!ims.isEmpty())
        {
            this.images = ims;
        }
    }

    private void setImage()
    {
        if(!this.images.isEmpty())
        {
            this.image = this.images.get(ZERO);
            repaint();
        }
    }

    private void syncImage()
    {
        boolean match = false;
        if(!this.images.isEmpty())
        {
            Iterator<BufferedImage> i = this.images.iterator();
            while(i.hasNext())
            {
                BufferedImage img = i.next();
                if (bufferedImagesEqual(this.image, img))
                {
                   i.remove();
                   match = true;
                }
            }
        }
        if(match)
        {
            this.images.add(this.image);
        }
    }

    private boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
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
