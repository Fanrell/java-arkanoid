import javax.lang.model.type.NullType;
import javax.swing.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.sql.Time;
import java.util.Random;
import java.util.Timer;

class Kulka extends Ellipse2D.Float
{
    Plansza p;
    int dx,dy;
    int score=0;
    boolean end = false;
    int speed;

    Kulka(Plansza p,int x,int y,int dx,int dy)
    {
        this.x=x;
        this.y=y;
        this.width=10;
        this.height=10;
        this.speed = 10;
        this.p=p;
        this.dx=dx;
        this.dy=dy;
    }

    void nextKrok()
    {
        x+=dx;
        y+=dy;

        if(getMinX()<0 || getMaxX()>p.getWidth())  dx=-dx;
        if(getMinY()<0 || getMaxY()>p.getHeight()) dy=-dy;

        p.repaint();
    }

    boolean posCheck(Belka b)
    {
        if(b.y+30 < this.y) {
            end = true;
            dx = 0;
            dy= 0;
        }
        return end;
    }

    public String toString() {
        return("Score: "+this.score);
    }
}


class SilnikKulki extends Thread
{
    Kulka a;

    SilnikKulki(Kulka a)
    {
        this.a=a;
        start();
    }

    public void run()
    {
        try
        {
            while(true)
            {
                a.nextKrok();
                sleep(a.speed);
//                if(a.dx>1 || a.dx*(-1) > 1)
//                {
//                    a.dx = 1;
//                }
            }
        }
        catch(InterruptedException e){}
    }
}

class Cegielka extends Rectangle2D.Float
{
    int col;
    int speed_ac;
    Cegielka(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.col = 3;
        this.speed_ac = 0;
    }

    void collision(Kulka k)
    {
        if(this.intersects(k.x,k.y,k.height,k.width))
        {
            Random y = new Random();
            int rotate;
            if(y.nextInt(255)%2 == 0) rotate = 1;
            else rotate = -1;
            k.dx*=rotate;
            k.dy*=-1;
            k.score+=10;
            if(this.speed_ac < k.score / 100 && k.speed > 5) {
                this.speed_ac = k.score / 100;
                k.speed -= this.speed_ac;
            }

            if(this.col>1)
            {
                this.col--;
            }
            else {
                this.x = -1024;
                this.y = -1024;
            }

        }
    }

    Color givColor()
    {
        switch (this.col)
        {
            case(3):
                return Color.blue;
            case(2):
                return  Color.yellow;
            case(1):
                return Color.red;
        }
        return Color.white;
    }
}

class Belka extends Rectangle2D.Float
{
    int rotate;
    Belka(int x)
    {
        this.x=x;
        this.y=170+(170/2);
        this.width=60;
        this.height=10;
    }

    void setX(int x)
    {
        this.x=x;
    }

    void collision(Kulka k)
    {
        if(this.intersects(k.x,k.y,k.height,k.width))
        {
            Random y = new Random();
            int rotate;
            if(y.nextInt(255)%2 == 0) rotate = -1;
            else rotate = 1;
            k.dx*=rotate;
            k.dy*=-1;
        }
    }
}

class Plansza extends JPanel implements MouseMotionListener
{
    Belka b;
    Kulka a;
    SilnikKulki s;
    Cegielka[][] blocks;
    int si = 6;
    Plansza()
    {
        super();
        addMouseMotionListener(this);
        reset();
    }

    void reset()
    {
        b=new Belka(100);
        a=new Kulka(this,100,200,1,1);
        s=new SilnikKulki(a);
        blocks = new Cegielka[si][si];

        for(int i=0;i<si;i++)
        {
            for(int j=0;j<si;j++)
            {
                blocks[i][j] = new Cegielka(i*62+15,j*20,60,10);
            }
        }
    }

    public void paintComponent(Graphics g)
    {
        int end = 0;
        super.paintComponent(g);
        Graphics2D g2d=(Graphics2D)g;
        if(a.posCheck(b) == false) {
            for (int i = 0; i < this.si; i++) {
                for (int j = 0; j < this.si; j++) {
                    blocks[i][j].collision(a);
                    g2d.setColor(blocks[i][j].givColor());
                    g2d.fill(blocks[i][j]);
                    if(blocks[i][j].x == -1024) end++;
                }
            }
            g2d.setColor(Color.black);
            b.collision(a);
            g2d.fill(a);
            g2d.fill(b);
            g2d.drawLine(0, 300, 400, 300);
            g2d.drawString(a.toString(), 10, 320);
            if(end == si*si) a.end = true;
        }
        else
        {
            g2d.drawString("GameOver",150,170);
            g2d.drawString(a.toString(),150,190);


        }

    }

    public void mouseMoved(MouseEvent e)
    {
        b.setX(e.getX()-50);
        b.collision(a);
        repaint();
    }

    public void mouseDragged(MouseEvent e)
    {

    }
}

    public class Arcanoid {
        public static void main(String[] args)
        {
            javax.swing.SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    Plansza p;
                    p=new Plansza();

                    JFrame jf=new JFrame();
                    jf.add(p);

                    jf.setTitle("Arcanoid");
                    jf.setSize(400,370);
                    jf.setResizable(false);
                    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    jf.setVisible(true);
                }
            });
        }
    }
