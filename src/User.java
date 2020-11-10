import java.util.Hashtable;

public class User {
        int id;
        Hashtable<Integer, String> questionAnswerTable; // questionId:answer. Answer stored as a string to handle all answer types
        Hashtable<ScoreCategory, Double> categoryScoreTable = new Hashtable<>();  // category:score

        public User() {

            // Init the categories we can use to score our users
            for(ScoreCategory scoreCategory : ScoreCategory.values()) {
                categoryScoreTable.put(scoreCategory, 0.5);
            }
        }

        public Hashtable<Integer, String> getQuestionAnswerTable() {
            return questionAnswerTable;
        }

        public void setQuestionAnswerTable(Hashtable<Integer, String> questionAnswerTable) {
            this.questionAnswerTable = questionAnswerTable;
        }
    }