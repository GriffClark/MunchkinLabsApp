import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Hashtable;

public class User {
        int id;
        Hashtable<Integer, String> questionAnswerTable = new Hashtable<>(); // questionId:answer. Answer stored as a string to handle all answer types
        Hashtable<String, Double> categoryScoreTable = new Hashtable<>();  // category:score

        public User() {

            // Init the categories we can use to score our users
            for(ScoreCategory scoreCategory : ScoreCategory.values()) {
                categoryScoreTable.put(scoreCategory.toString(), 0.5);
            }
        }

        public Hashtable<Integer, String> getQuestionAnswerTable() {
            return questionAnswerTable;
        }

        public void setQuestionAnswerTable(Hashtable<Integer, String> questionAnswerTable) {
            this.questionAnswerTable = questionAnswerTable;
        }

        public void update(){
            removeOldScores();
            insertScores();
            deleteDirtyUser();
        }

        private void removeOldScores(){
            try {
                //TODO: This should update scores instead of deleting them. Init each user with default scores
                Connection conn = Res.getConnection(); //TODO: create a connection pool
                String sql = "DELETE FROM scores WHERE userId=" + id +";";
                PreparedStatement statement = conn.prepareStatement(sql);
                    statement.executeUpdate();
                    System.out.println("Successfully deleted old scores for user " + id);
    
            } catch (Exception e){
                System.out.println(e);
            }
            
        }

        private void deleteDirtyUser() {
            try {
                Connection conn = Res.getConnection();
                String sql = "DELETE FROM dirtyusers WHERE userId=" + id; 
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.executeUpdate();
                System.out.println("Successfully deleted user " + id + " from dirtyUsers");
    
            }catch (Exception e){
                System.out.println(e);
            }
        }

        private void insertScores(){
            for(String category : categoryScoreTable.keySet()){
                try {
                    Connection conn = Res.getConnection();
                    double scoreValue = categoryScoreTable.get(category);
    
                    // DEFAULT
                    double confidence = 0;
    
                    // TODO: Steralize SQL
                    String sql = "INSERT INTO scores (userId, category, value, confidence) VALUES ("+ 
                    id + ",\'" +
                    category + "\'," + 
                    scoreValue + "," +
                    confidence+ ");";
    
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.executeUpdate();
        
                    System.out.println("Successfully added " + category.toString() +" score for user " + id);
        
                } catch (Exception e){
                    System.out.println(e);
                }
            }
            
        }
    }