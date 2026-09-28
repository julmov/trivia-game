CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(80) NOT NULL,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'easy',
    question_text TEXT NOT NULL,
    answer_a TEXT NOT NULL,
    answer_b TEXT NOT NULL,
    answer_c TEXT NOT NULL,
    answer_d TEXT NOT NULL,
    correct_answer SMALLINT NOT NULL CHECK (correct_answer BETWEEN 0 AND 3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS questions_category_idx ON questions(category);
CREATE INDEX IF NOT EXISTS questions_difficulty_idx ON questions(difficulty);

INSERT INTO questions (category, difficulty, question_text, answer_a, answer_b, answer_c, answer_d, correct_answer)
SELECT 'Planets', 'easy', 'Which planet is known as the ocean world?', 'Tatooine', 'Kamino', 'Hoth', 'Naboo', 1
WHERE NOT EXISTS (SELECT 1 FROM questions);

INSERT INTO questions (category, difficulty, question_text, answer_a, answer_b, answer_c, answer_d, correct_answer)
SELECT 'Ships', 'easy', 'What is the name of Han Solo''s ship?', 'X-wing', 'Slave I', 'Millennium Falcon', 'Razor Crest', 2
WHERE (SELECT COUNT(*) FROM questions) = 1;

INSERT INTO questions (category, difficulty, question_text, answer_a, answer_b, answer_c, answer_d, correct_answer)
SELECT 'Orders', 'easy', 'Which order protects the galaxy with the Force?', 'The Jedi Order', 'The Trade Federation', 'The Empire', 'The Senate', 0
WHERE (SELECT COUNT(*) FROM questions) = 2;