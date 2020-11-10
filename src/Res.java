import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

public class Res {
    public static Connection getConnection() throws Exception {
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/usersDB"; // Works for localhost

            // Default creds for phpmyadmin
            String username = "root";
            String password = "";

            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url, username, password);
            // System.out.println("Successfully connected to " + url + " as " + username);
            return conn;
        } catch (Exception e){
            System.out.println(e);
        }

        return null; // This will only hit if there's an error
    }

    public static ArrayList<String> getDirtyUsers() throws Exception {
        try {
            Connection conn = getConnection();

            String sql = "SELECT * FROM dirtyUsers";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            ArrayList<String> resultIds = new ArrayList<>();

            while(resultSet.next()) {
                // System.out.println(resultSet.getString("userId"));
                resultIds.add(resultSet.getString("userId"));
            }

            System.out.println("All records have been selected");
            return resultIds;

        } catch (Exception e){
            System.out.println(e);
        }

        return null;
    }

    public static Hashtable<Integer, String> getValuesToUpdate(String userId) {
        try {
            Connection conn = getConnection();

            String sql = "SELECT * FROM answers WHERE userId=" + userId;
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            Hashtable<Integer, String> resultTable = new Hashtable<>();

            while(resultSet.next()){
                Integer questionId = Integer.parseInt(resultSet.getString("questionId"));
                String answer = (resultSet.getString("binaryAnswer"));
                resultTable.put(questionId, answer);
            }
            return resultTable;
        }catch (Exception e){
            System.out.println(e);
        }

        return null;

    }

    public static Hashtable<Integer, String> getQuestions() {
        try {
            Connection conn = getConnection();

            String sql = "SELECT * FROM questions";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            Hashtable<Integer, String> resultTable = new Hashtable<>();

            while(resultSet.next()){
                int questionId = Integer.parseInt(resultSet.getString("id"));
                String questionText = resultSet.getString("questionText");
                resultTable.put(questionId, questionText);
            }

            return resultTable;

        }catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static void deleteDirtyUser(User user) {
        try {
            Connection conn = Res.getConnection();
            String sql = "DELETE FROM dirtyusers WHERE userId=" + user.id; // TODO: not working
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            System.out.println("Successfully deleted user " + user + " from dirtyUsers");

        }catch (Exception e){
            System.out.println(e);
        }
    }

    

    public static void updateCategoryScoreTableBinary(User user, ScoreCategory scoreCategory, String answer, double delta){
        // For a yes/no question, update the scoretable
        
        // This assumes that a yes will always make the score go up, and a no will always make the score go down
        if(Integer.parseInt(answer) == 0){  // The user said no
            delta *= -1; 
        }
        user.categoryScoreTable.put(scoreCategory, user.categoryScoreTable.get(ScoreCategory.FINANCE) + delta);
    }

    public static void removeOldScores(User user){
        try {
            Connection conn = getConnection();
            String sql = "DELETE FROM scores WHERE userId=" + user.id +";";
            PreparedStatement statement = conn.prepareStatement(sql);
                statement.executeUpdate();
                System.out.println("Successfully deleted old scores for user " + user.id);

        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void insertScores(User user){
        int userId = user.id;
        for(ScoreCategory category : user.categoryScoreTable.keySet()){
            try {
                Connection conn = getConnection();
                double scoreValue = user.categoryScoreTable.get(category);

                // DEFAULT
                double confidence = 0;

                // TODO: Steralize SQL
                String sql = "INSERT INTO scores (userId, category, value, confidence) VALUES ("+ 
                userId + ",\'" +
                category.toString() + "\'," + 
                scoreValue + "," +
                confidence+ ");";

                PreparedStatement statement = conn.prepareStatement(sql);
                statement.executeUpdate();
    
                System.out.println("Successfully added " + category.toString() +" score for user " + userId);
    
            } catch (Exception e){
                System.out.println(e);
            }
        }
        
    }
    
}
