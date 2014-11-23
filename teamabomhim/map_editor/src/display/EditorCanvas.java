package display;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JPanel;

/**
 * @author Benjamin Sladewski
 *
 * TODO: Comment me.
 */
public class EditorCanvas extends JPanel {

    /**
     * TODO: Comment me.
     */
    private static final long serialVersionUID = -2593841092399324456L;
    
    /**
     * TODO: Comment me.
     */
    private Stack<BufferedImage> undoBuffer;
    
    /**
     * TODO: Comment me.
     */
    private Stack<BufferedImage> redoBuffer;
    
    /**
     * TODO: Comment me.
     */
    private BufferedImage map;
    
    /**
     * TODO: Comment me.
     */
    private Path2D.Float stroke;
    
    /**
     * TODO: Comment me.
     */
    private boolean isDrawing;
    
    /**
     * TODO: Comment me.
     */
    public EditorCanvas(int width, int height) {
        // TODO: Comment me.
        undoBuffer = new Stack<BufferedImage>();
        redoBuffer = new Stack<BufferedImage>();
        map = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setOpaque(false);
        stroke = new Path2D.Float();
        setDrawing(false);

        // TODO: Comment me.
        addMouseListener(new MouseListener() {

            // TODO: Comment me.
            @Override
            public void mouseReleased(MouseEvent arg0) {
                setDrawing(false);
            }

            // TODO: Comment me.
            @Override
            public void mousePressed(MouseEvent arg0) {
                stroke.moveTo(arg0.getX(), arg0.getY());
                setDrawing(true);
            }

            // TODO: Comment me.
            @Override
            public void mouseExited(MouseEvent arg0) {
                setDrawing(false);
            }

            // TODO: Comment me.
            @Override
            public void mouseEntered(MouseEvent arg0) {}

            // TODO: Comment me.
            @Override
            public void mouseClicked(MouseEvent arg0) {}

        });

        // TODO: Comment me.
        addMouseMotionListener(new MouseMotionListener() {

            // TODO: Comment me.
            @Override
            public void mouseMoved(MouseEvent arg0) {}

            // TODO: Comment me.
            @Override
            public void mouseDragged(MouseEvent arg0) {
                draw(arg0.getX(), arg0.getY());
            }
        });
    }

    /**
     * TODO: Comment me.
     * 
     * @param x
     * @param y
     */
    private void draw(int x, int y) {
        if(isDrawing()) {
            stroke.lineTo(x, y);
            repaint();
        }
    }

    /**
     * TODO: Comment me.
     * 
     * @return
     */
    public boolean isDrawing() {
        return isDrawing;
    }

    /**
     * TODO: Comment me.
     * 
     * @param isDrawing
     */
    public void setDrawing(boolean isDrawing) {
        if(this.isDrawing == isDrawing)
            return;
        else if(isDrawing == false) {
            processStroke();
        }
        this.isDrawing = isDrawing;
        repaint();
    }
    
    public void undo() {
        if(!undoBuffer.isEmpty()) {
            redoBuffer.push(duplicateImage(map));
            map = undoBuffer.pop();
        }
        repaint();
    }
    
    public void redo() {
        if(!redoBuffer.isEmpty()) {
            undoBuffer.push(duplicateImage(map));
            map = redoBuffer.pop();
        }
        repaint();
    }
    
    /**
     * TODO: Comment me.
     * 
     * @param bi
     * @return
     */
    public BufferedImage duplicateImage(BufferedImage bi){
        BufferedImage bn = new BufferedImage(bi.getWidth(), bi.getHeight(), 
                                             bi.getType());
        Graphics g = bn.getGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return bn;
    }

    /**
     * TODO: Comment me.
     */
    private void processStroke() {
        if(!redoBuffer.isEmpty())
            redoBuffer.clear();
        undoBuffer.push(duplicateImage(map));
        float tx = ((float) map.getWidth()) / ((float) this.getWidth());
        float ty = ((float) map.getHeight()) / ((float) this.getHeight());
        drawStroke(map.createGraphics(), tx, ty);
        stroke.reset();
    }
    
    /**
     * TODO: Comment me.
     * 
     * @param g
     */
    private void drawStroke(Graphics g, float tx, float ty) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = g2d.getTransform();
        g2d.transform(AffineTransform.getScaleInstance(tx, ty));
        g2d.setPaint(Color.WHITE);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                         ((float) MapEditor.opacity) / 100.0f));
        g2d.setStroke(new BasicStroke(MapEditor.brushSize,
                      BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(stroke);
        g2d.setTransform(at);
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image toDraw = map.getScaledInstance(this.getWidth(), this.getHeight(),
                       Image.SCALE_FAST);
        g.drawImage(toDraw, 0, 0, null);
        drawStroke(g, 1.0f, 1.0f);
    }

}
