import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class WaterFlowSystem extends JFrame {
    // Capacity Matrix
    private final int[][] capacity;
    private final int[][] flow;
    private final String[] nodes = {"S", "C1", "C2", "C3", "C4", "C5", "C6", "T"};
    private final int n = nodes.length;
    
    // Node Coordinates for Drawing
    private final int[][] coords = {
        {50, 200}, {180, 50}, {180, 200}, {350, 50}, 
        {350, 350}, {500, 200}, {500, 350}, {650, 200}
    };

    public WaterFlowSystem() {
        setTitle("Metropolitan Water Flow Visualizer");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        capacity = new int[n][n];
        flow = new int[n][n];
        initializeNetwork();
        
        GraphPanel panel = new GraphPanel();
        add(panel, BorderLayout.CENTER);
        
        JButton btnSolve = new JButton("Run Ford-Fulkerson");
        btnSolve.addActionListener(e -> solve(panel));
        add(btnSolve, BorderLayout.SOUTH);
    }

    private void initializeNetwork() {
        // Map S=0, C1=1, C2=2, C3=3, C4=4, C5=5, C6=6, T=7
        capacity[0][1] = 12; // S -> C1
        capacity[0][2] = 8;  // S -> C2
        capacity[0][4] = 15; // S -> C4
        capacity[1][3] = 9;  // C1 -> C3
        capacity[1][5] = 6;  // C1 -> C5
        capacity[2][3] = 7;  // C2 -> C3
        capacity[2][5] = 10; // C2 -> C5
        capacity[2][4] = 5;  // C2 -> C4
        capacity[2][6] = 5;  // C2 -> C6
        capacity[3][7] = 11; // C3 -> T
        capacity[4][6] = 18; // C4 -> C6
        capacity[5][7] = 14; // C5 -> T
        capacity[6][7] = 9;  // C6 -> T
    }

    private void solve(GraphPanel panel) {
        new Thread(() -> {
            int maxFlow = 0;
            int[] parent = new int[n];
            
            while (bfs(parent)) {
                // Find bottleneck
                int pathFlow = Integer.MAX_VALUE;
                List<Integer> path = new ArrayList<>();
                for (int v = n - 1; v != 0; v = parent[v]) {
                    int u = parent[v];
                    pathFlow = Math.min(pathFlow, capacity[u][v] - flow[u][v]);
                    path.add(v);
                }
                path.add(0);
                Collections.reverse(path);

                // Update residual graph
                for (int v = n - 1; v != 0; v = parent[v]) {
                    int u = parent[v];
                    flow[u][v] += pathFlow;
                    flow[v][u] -= pathFlow;
                }
                maxFlow += pathFlow;
                
                System.out.println("Augmenting Path found: " + path + " | Bottleneck: " + pathFlow);
                panel.setHighlightPath(path);
                panel.repaint();
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }
            JOptionPane.showMessageDialog(this, "Calculation Complete!\nMaximum Water Flow: " + maxFlow + " million liters/day");
        }).start();
    }

    private boolean bfs(int[] parent) {
        Arrays.fill(parent, -1);
        Queue<Integer> q = new LinkedList<>();
        q.add(0);
        parent[0] = 0;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < n; v++) {
                if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                    parent[v] = u;
                    q.add(v);
                    if (v == n - 1) return true;
                }
            }
        }
        return false;
    }

    class GraphPanel extends JPanel {
        private List<Integer> highlightPath = new ArrayList<>();

        public void setHighlightPath(List<Integer> path) { this.highlightPath = path; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Edges
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (capacity[i][j] > 0) {
                        boolean inPath = false;
                        for (int k = 0; k < highlightPath.size() - 1; k++) {
                            if (highlightPath.get(k) == i && highlightPath.get(k+1) == j) inPath = true;
                        }
                        
                        g2.setColor(inPath ? Color.RED : Color.GRAY);
                        g2.setStroke(new BasicStroke(inPath ? 3 : 1));
                        g2.drawLine(coords[i][0]+20, coords[i][1]+20, coords[j][0]+20, coords[j][1]+20);
                        
                        // Label Flow/Capacity
                        g2.setColor(Color.BLACK);
                        int midX = (coords[i][0] + coords[j][0]) / 2 + 15;
                        int midY = (coords[i][1] + coords[j][1]) / 2;
                        g2.drawString(flow[i][j] + "/" + capacity[i][j], midX, midY);
                    }
                }
            }

            // Draw Nodes
            for (int i = 0; i < n; i++) {
                g2.setColor(new Color(255, 165, 0));
                g2.fillOval(coords[i][0], coords[i][1], 40, 40);
                g2.setColor(Color.BLACK);
                g2.drawOval(coords[i][0], coords[i][1], 40, 40);
                g2.drawString(nodes[i], coords[i][0]+15, coords[i][1]+25);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WaterFlowSystem().setVisible(true));
    }
}
