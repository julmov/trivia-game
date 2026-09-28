import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().show());
    }
    private static final Color SPACE = new Color(8, 14, 30);
    private static final Color PANEL = new Color(18, 29, 52);
    private static final Color GOLD = new Color(246, 190, 61);
    private static final Color ANSWER_BLUE = new Color(27, 42, 70);
    private static final Color CORRECT_GREEN = new Color(42, 150, 92);
    private static final Color WRONG_RED = new Color(190, 65, 68);

    private final JFrame frame = new JFrame("Galaxy Trivia");
    private final JLabel questionLabel = new JLabel();
    private final JLabel progressLabel = new JLabel();
    private final JLabel scoreLabel = new JLabel();
    private final JRadioButton[] answers = new JRadioButton[4];
    private List<Question> questions;
    private int questionIndex;
    private int score;

    private void show() {
        try {
            questions = new PostgresQuestionRepository().findAll();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null,
                    "Could not load questions from PostgreSQL.\n" + exception.getMessage(),
                    "Database connection error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The database has no questions yet.", "No questions", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(760, 520));
        frame.setContentPane(new SpacePanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        loadQuestion();
    }

    private void loadQuestion() {
        Question question = questions.get(questionIndex);
        questionLabel.setText("<html><div style='width:560px'>" + question.text + "</div></html>");
        progressLabel.setText("QUESTION " + (questionIndex + 1) + " / " + questions.size());
        scoreLabel.setText("SCORE  " + score);
        for (int i = 0; i < answers.length; i++) {
            answers[i].setText(question.choices[i]);
            answers[i].setSelected(false);
            answers[i].setEnabled(true);
            answers[i].setBackground(ANSWER_BLUE);
        }
    }

    private void submitAnswer(ActionEvent event) {
        int selected = -1;
        for (int i = 0; i < answers.length; i++) {
            if (answers[i].isSelected()) {
                selected = i;
                break;
            }
        }
        if (selected < 0) {
            JOptionPane.showMessageDialog(frame, "Choose an answer before submitting.", "No answer", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int correctAnswer = questions.get(questionIndex).correctAnswer;
        boolean isCorrect = selected == correctAnswer;
        if (isCorrect) {
            score++;
        }
        answers[selected].setBackground(isCorrect ? CORRECT_GREEN : WRONG_RED);
        if (!isCorrect) {
            answers[correctAnswer].setBackground(CORRECT_GREEN);
        }
        for (JRadioButton answer : answers) {
            answer.setEnabled(false);
        }
        Timer feedbackTimer = new Timer(700, nextQuestion -> {
            questionIndex++;
            if (questionIndex == questions.size()) {
                JOptionPane.showMessageDialog(frame, "Mission complete! You scored " + score + " / " + questions.size() + ".", "Final score", JOptionPane.INFORMATION_MESSAGE);
                questionIndex = 0;
                score = 0;
            }
            loadQuestion();
        });
        feedbackTimer.setRepeats(false);
        feedbackTimer.start();
    }

    private class SpacePanel extends JPanel {
        private SpacePanel() {
            setLayout(new BorderLayout(28, 20));
            setBorder(BorderFactory.createEmptyBorder(28, 42, 30, 42));
            setOpaque(false);

            JLabel title = new JLabel("GALAXY TRIVIA", SwingConstants.LEFT);
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Serif", Font.BOLD, 34));
            add(title, BorderLayout.NORTH);

            JPanel card = new JPanel(new BorderLayout(18, 20));
            card.setBackground(PANEL);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(74, 105, 153), 1),
                    BorderFactory.createEmptyBorder(24, 28, 24, 28)));

            JPanel metadata = new JPanel(new BorderLayout());
            metadata.setOpaque(false);
            progressLabel.setForeground(GOLD);
            progressLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            scoreLabel.setForeground(new Color(176, 199, 226));
            scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            metadata.add(progressLabel, BorderLayout.WEST);
            metadata.add(scoreLabel, BorderLayout.EAST);
            card.add(metadata, BorderLayout.NORTH);

            questionLabel.setForeground(Color.WHITE);
            questionLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            card.add(questionLabel, BorderLayout.CENTER);

            JPanel choices = new JPanel(new GridLayout(2, 2, 12, 12));
            choices.setOpaque(false);
            ButtonGroup group = new ButtonGroup();
            for (int i = 0; i < answers.length; i++) {
                answers[i] = new JRadioButton();
                answers[i].setForeground(new Color(224, 232, 245));
                answers[i].setBackground(ANSWER_BLUE);
                answers[i].setFont(new Font("SansSerif", Font.PLAIN, 15));
                answers[i].setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
                group.add(answers[i]);
                choices.add(answers[i]);
            }
            card.add(choices, BorderLayout.SOUTH);
            add(card, BorderLayout.CENTER);

            JButton submit = new JButton("SUBMIT ANSWER");
            submit.setBackground(GOLD);
            submit.setForeground(new Color(25, 28, 40));
            submit.setFont(new Font("SansSerif", Font.BOLD, 14));
            submit.setFocusPainted(false);
            submit.addActionListener(Main.this::submitAnswer);
            JPanel footer = new JPanel(new BorderLayout());
            footer.setOpaque(false);
            footer.add(new JLabel("Test your knowledge across the stars."), BorderLayout.WEST);
            footer.add(submit, BorderLayout.EAST);
            footer.getComponent(0).setForeground(new Color(164, 182, 207));
            add(footer, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, SPACE, getWidth(), getHeight(), new Color(21, 45, 79)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 180));
            for (int i = 0; i < 45; i++) {
                int x = (i * 83 + 19) % Math.max(1, getWidth());
                int y = (i * 47 + 31) % Math.max(1, getHeight());
                int size = i % 5 == 0 ? 3 : 1;
                g.fillOval(x, y, size, size);
            }
            g.setColor(new Color(91, 211, 233, 130));
            g.setStroke(new BasicStroke(2));
            g.drawOval(getWidth() - 170, 55, 120, 120);
            g.setColor(new Color(91, 211, 233, 35));
            g.fillOval(getWidth() - 165, 60, 110, 110);
            g.dispose();
        }
    }

}