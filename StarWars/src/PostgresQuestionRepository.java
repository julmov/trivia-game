import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresQuestionRepository {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/galaxy_trivia";
    private static final String DEFAULT_USER = "trivia_user";
    private static final String DEFAULT_PASSWORD = "trivia_password";

    private static final String SELECT_QUESTIONS = """
            SELECT question_text, answer_a, answer_b, answer_c, answer_d, correct_answer
            FROM questions
            ORDER BY id
            """;

    public List<Question> findAll() throws SQLException {
        String url = environmentOrDefault("TRIVIA_DB_URL", DEFAULT_URL);
        String user = environmentOrDefault("TRIVIA_DB_USER", DEFAULT_USER);
        String password = environmentOrDefault("TRIVIA_DB_PASSWORD", DEFAULT_PASSWORD);
        List<Question> questions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(SELECT_QUESTIONS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                questions.add(new Question(
                        resultSet.getString("question_text"),
                        new String[]{
                                resultSet.getString("answer_a"),
                                resultSet.getString("answer_b"),
                                resultSet.getString("answer_c"),
                                resultSet.getString("answer_d")
                        },
                        resultSet.getInt("correct_answer")));
            }
        }
        return questions;
    }

    private String environmentOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}