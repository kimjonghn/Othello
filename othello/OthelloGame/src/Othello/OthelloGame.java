package Othello;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OthelloGame extends JFrame {
    private static final int SIZE = 8;
    private int[][] board = new int[SIZE][SIZE]; // 0: empty, 1: black (player), 2: white (AI)
    private boolean playerTurn = true;

    public OthelloGame() {
        setTitle("Othello Game - Player vs AI");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // 초기 배치
        board[3][3] = 2;
        board[3][4] = 1;
        board[4][3] = 1;
        board[4][4] = 2;

        BoardPanel panel = new BoardPanel();
        add(panel);

        setVisible(true);
    }

    private class BoardPanel extends JPanel implements MouseListener {
        public BoardPanel() {
            setBackground(new Color(184, 134, 11)); // 갈색 배경
            addMouseListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cellSize = getWidth() / SIZE;

            g.setColor(Color.BLACK);
            for (int i = 0; i <= SIZE; i++) {
                g.drawLine(i * cellSize, 0, i * cellSize, getHeight());
                g.drawLine(0, i * cellSize, getWidth(), i * cellSize);
            }

            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    if (board[y][x] == 1) {
                        g.setColor(Color.BLACK);
                        g.fillOval(x * cellSize + 5, y * cellSize + 5, cellSize - 10, cellSize - 10);
                    } else if (board[y][x] == 2) {
                        g.setColor(Color.WHITE);
                        g.fillOval(x * cellSize + 5, y * cellSize + 5, cellSize - 10, cellSize - 10);
                    }
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int cellSize = getWidth() / SIZE;
            int x = e.getX() / cellSize;
            int y = e.getY() / cellSize;

            if (playerTurn && isValidMove(x, y, 1, true)) {
                board[y][x] = 1;
                flipDisks(x, y, 1);
                playerTurn = false;
                repaint();

                // AI 차례
                SwingUtilities.invokeLater(() -> {
                    aiMove();
                    playerTurn = true;
                    repaint();
                });
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }

    private void aiMove() {
        List<Point> validMoves = new ArrayList<>();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (isValidMove(x, y, 2, false)) {
                    validMoves.add(new Point(x, y));
                }
            }
        }

        if (!validMoves.isEmpty()) {
            Point move = validMoves.get(new Random().nextInt(validMoves.size()));
            board[move.y][move.x] = 2;
            flipDisks(move.x, move.y, 2);
        }
    }

    private boolean isValidMove(int x, int y, int player, boolean checkEmpty) {
        if (checkEmpty && board[y][x] != 0) return false;

        int opponent = (player == 1) ? 2 : 1;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int cx = x + dx, cy = y + dy;
                boolean hasOpponentBetween = false;
                while (cx >= 0 && cy >= 0 && cx < SIZE && cy < SIZE && board[cy][cx] == opponent) {
                    cx += dx;
                    cy += dy;
                    hasOpponentBetween = true;
                }
                if (hasOpponentBetween && cx >= 0 && cy >= 0 && cx < SIZE && cy < SIZE && board[cy][cx] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private void flipDisks(int x, int y, int player) {
        int opponent = (player == 1) ? 2 : 1;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                List<Point> toFlip = new ArrayList<>();
                int cx = x + dx, cy = y + dy;

                while (cx >= 0 && cy >= 0 && cx < SIZE && cy < SIZE && board[cy][cx] == opponent) {
                    toFlip.add(new Point(cx, cy));
                    cx += dx;
                    cy += dy;
                }

                if (cx >= 0 && cy >= 0 && cx < SIZE && cy < SIZE && board[cy][cx] == player) {
                    for (Point p : toFlip) {
                        board[p.y][p.x] = player;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OthelloGame::new);
    }
}


