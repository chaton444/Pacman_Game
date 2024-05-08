package main;

import characters.Ghost;
import characters.Pacman;
import componentes.Laberinto;
import componentes.Tablero;
import graficos.Graficos;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public final class GamePanel extends JPanel implements KeyListener {

    private final int width, height;
    public BufferedImage buffer;
    Graphics gBuffer;
    Image img;

    private final int pacmanMov = 10;
    private final int ghostMov = 10;
    private final int cambioDireccion = 3000;

    Thread thPacman;
    Thread thCambioDireccion;
    Thread thGhost1, thGhost2, thGhost3, thGhost4;

    Graficos graficos;
    Laberinto laberinto;
    Tablero tablero;
    Pacman pacman;
    Ghost ghost;

    public GamePanel(int w, int h) {
        this.width = w;
        this.height = h;
        if (buffer == null) {
            buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        this.ghost = new Ghost(buffer, laberinto);

        //Inicializacion de objetos
        makeGraficos();
        makeMaze();
        makeBoard();
        makePacman();
        makeGhost();

        iniciarHilos();

        // Movimiento continuo de pacman
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
    }

    public void makeGraficos() {
        graficos = new Graficos(width, height, buffer, this);
    }

    public void makeMaze() {
        laberinto = new Laberinto(buffer);
    }

    public void makeBoard() {
        tablero = new Tablero(buffer, graficos, pacman);
    }

    public void makePacman() {
        pacman = new Pacman(buffer, laberinto, ghost);
    }
    
    public void makeGhost() {
        ghost = new Ghost(buffer, laberinto);
    }

    private synchronized void iniciarHilos() {
        //Encarcados de movimiento de pacman
        thPacman = new Thread(this::runPacman);
        //Encargado de cambiar la direccion de los fantasmas cada 4 segundos
        thCambioDireccion = new Thread(this::cambioDireccion);
        //Encargado de movimiento de fantasma
        thGhost1 = new Thread(this::runGhost1);
//        thGhost2 = new Thread(this::runGhost2);
//        thGhost3 = new Thread(this::runGhost3);
//        thGhost4 = new Thread(this::runGhost4);

        thPacman.start();
        thCambioDireccion.start();
        thGhost1.start();
 //      thGhost2.start();
 //      thGhost3.start();
 //       thGhost4.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        img = createImage(getWidth(), getHeight());
        gBuffer = img.getGraphics();

        //Pinta el mapa
        drawBackground();
        drawCharacters();
        
        g.drawImage(buffer, 0, 0, this);
    }

    public void runPacman() {
        while (true) {
            pacman.moverPacman();
            pacman.vidaMenos();
            try {
                Thread.sleep(pacmanMov);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void runGhost1() {
        while (true) {
            ghost.moverFantasma();
            try {
                Thread.sleep(ghostMov);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /*public void runGhost2() {
        while (true) {
            ghost.moverFantasma();
            try {
                Thread.sleep(ghostMov);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void runGhost3() {
        while (true) {
            ghost.moverFantasma();
            try {
                Thread.sleep(ghostMov);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void runGhost4() {
        while (true) {
            ghost.moverFantasma();
            try {
                Thread.sleep(ghostMov);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

    public void cambioDireccion() {
        // Ghost direction changes in 500ms
        while (true) {
            ghost.cambioDeDireccion();
            try {
                Thread.sleep(cambioDireccion);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void drawBackground() {
        laberinto.drawBackground(graficos);
        laberinto.drawMaze(graficos);
        laberinto.drawPoints(graficos, pacman);
        drawBoard();
    }
    
    public void drawBoard() {
        printGameTitle();
        printCherry();
        printVidas();
    }

    public void printGameTitle() {
        tablero.printPacmanLetter(tablero.printP(), 380, 515);
        tablero.printPacmanLetter(tablero.printA(), 410, 515);
        tablero.printPacmanLetter(tablero.printC(), 440, 515);
        tablero.printPacmanLetter(tablero.printM(), 470, 515);
        tablero.printPacmanLetter(tablero.printA(), 500, 515);
        tablero.printPacmanLetter(tablero.printN(), 530, 515);
        repaint();
    }

    public void printCherry() {
        tablero.drawCherry(850, 530);
        tablero.drawCherry(900, 530);
    }

    public void printVidas() {
        int posX = 30;
        for (int i = 0; i < pacman.getVidas(); i++) {
            tablero.drawVidas(posX, 530);
            posX += 50;
        }
    }

    public void drawCharacters() {
        pacman.dibujarPacman(graficos);
        ghost.dibujarFantasma(graficos);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                pacman.teclaPresionada[0] = true;
                pacman.teclaPresionada[1] = false;
                pacman.teclaPresionada[2] = false;
                pacman.teclaPresionada[3] = false;
                break;
            case KeyEvent.VK_A:
                pacman.teclaPresionada[0] = false;
                pacman.teclaPresionada[1] = true;
                pacman.teclaPresionada[2] = false;
                pacman.teclaPresionada[3] = false;
                break;
            case KeyEvent.VK_S:
                pacman.teclaPresionada[0] = false;
                pacman.teclaPresionada[1] = false;
                pacman.teclaPresionada[2] = true;
                pacman.teclaPresionada[3] = false;
                break;
            case KeyEvent.VK_D:
                pacman.teclaPresionada[0] = false;
                pacman.teclaPresionada[1] = false;
                pacman.teclaPresionada[2] = false;
                pacman.teclaPresionada[3] = true;
                break;
            default:
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
