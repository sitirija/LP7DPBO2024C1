import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int  frameWidth = 360;
    int frameHeight = 640;
    boolean gameStarted = false;
    JButton startButton;
    private int score = 0; // Variabel skor

    //image attributes
    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;

    //player
    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;
    Player player;

    //pipes attributes
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    int pipeGap = 200; // Jarak antara pasangan pipa
    ArrayList<Pipe> pipes;

    //game logic
    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    //constructor
    public FlappyBird(){
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.blue);

        //load images
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        // Initialize player and pipes
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        //pipes cooldown timer
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipesCooldown.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        // Add start button
        startButton = new JButton("Mulai");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // Atur tata letak tombol "Mulai"
        int buttonWidth = 100; // Lebar tombol
        int buttonHeight = 40; // Tinggi tombol
        int buttonX = (frameWidth - buttonWidth) / 2; // Hitung posisi X agar berada di tengah horizontal
        int buttonY = (frameHeight - buttonHeight) / 2; // Hitung posisi Y agar berada di tengah vertikal
        startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        startButton.setFont(new Font("Arial", Font.PLAIN, 20)); // Atur ukuran font lebih besar

        add(startButton);
    }

    public void placePipes(){
        int randomPipePosY = (int) (pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
//        int openingSpace = 350; // Atur jarak antara pasangan pipa sesuai kebutuhan
        int openingSpace = frameHeight/4;

        Pipe upperPipe = new Pipe(pipeStartPosX, pipeStartPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPipePosY + pipeHeight + openingSpace, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw (Graphics g){
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);

        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }

        drawScore(g); // Gambar skor
    }

    public void move(){
        // Memindahkan burung ke bawah berdasarkan gravitasi
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        // Memindahkan pipa ke kiri
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVeloncityX());

            // Cek tabrakan dengan pipa
            if (player.getPosX() + player.getWidth() > pipe.getPosX() &&
                    player.getPosX() < pipe.getPosX() + pipe.getWidth() &&
                    (player.getPosY() < pipe.getPosY() || player.getPosY() + player.getHeight() > pipe.getBottomPipePosY())) {
                endGame(); // Panggil metode endGame() jika terjadi tabrakan
            }
        }

        // Cek jatuh ke bawah
        if (player.getPosY() + player.getHeight() >= frameHeight || player.getPosY() <= 0) {
            endGame(); // Panggil metode endGame() jika burung jatuh ke bawah atau ke atas layar
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) {
            move();
            checkPassingPipes(); // Panggil metode untuk memeriksa pipa yang dilewati
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Metode untuk memeriksa apakah burung melewati sepasang pipa
    private void checkPassingPipes() {
        for (int i = 0; i < pipes.size(); i += 2) {
            Pipe upperPipe = pipes.get(i);
            Pipe lowerPipe = pipes.get(i + 1);

            if (!upperPipe.isPassed() && upperPipe.getPosX() + upperPipe.getWidth() < player.getPosX()) {
                upperPipe.setPassed(true); // Tandai pipa atas yang telah dilewati
                lowerPipe.setPassed(true); // Tandai pipa bawah yang telah dilewati
                score++; // Tambah skor hanya sekali untuk sepasang pipa
            }
        }
    }

    // Metode untuk menggambar skor saat game over
    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Skor: " + score, frameWidth / 2 - 50, frameHeight / 2 + 30);
    }

    private void startGame() {
        resetGame(); // Panggil resetGame() saat memulai permainan baru
        startButton.setVisible(false); // Sembunyikan tombol "Mulai"

        gameStarted = true;
//        remove(startButton);
        requestFocusInWindow();
        gameLoop.start(); // Start the game loop immediately when the button is clicked
        pipesCooldown.start(); // Start the pipes cooldown timer immediately when the button is clicked
    }

    private void endGame() {
        gameStarted = false;
        gameLoop.stop();
        pipesCooldown.stop();
        JOptionPane.showMessageDialog(this, "SKOR ANDA : " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        resetGame(); // Panggil resetGame() saat game over
    }

    private void resetGame() {
        gameStarted = false;
        score = 0;
        player.setPosY(playerStartPosY);
        pipes.clear();
        gameLoop.stop();
        pipesCooldown.stop();
        startButton.setVisible(true);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameStarted && e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setVelocityY(-10);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }
}