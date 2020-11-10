import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

enum ScoreCategory {
    FINANCE,
    ACTIVE,
    ADVENTUROUS,
    LENGTH,
    BIRDWATCHER,
    GROUPSIZE,
    EXTRAVAGANT,
    CULTURE
}

public class UpdateDirtyUsers {
    public static double defaultScoreValue = 0.5;
    public static long sleepTime = 30000; //ms
    public static class User {
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

    public static void main (String[] args) throws Exception{
        while(true){
        ArrayList<String> dirtyUsers = getDirtyUsers(); // TODO: Handle duplicate users
        ArrayList<User> userList = new ArrayList<>();

        // For each user, figure out which values need to be updated
        for(String id: dirtyUsers) {
            try{
                User user = new User();
                user.id = Integer.parseInt(id);
                user.questionAnswerTable = (getValuesToUpdate(id));
                userList.add(user);
            } catch (Exception e){
                System.out.println(e);
            }
        }

        // Now that we know which users need to be updated and which questions the users have answered, we can update the scores
        for(User user : userList) {
            Set<Integer> questionIds = user.questionAnswerTable.keySet();
            for(int questionId: questionIds){
                // Use the questionId to find the statement
                // Statements assign category:score to user.categoryScoreTable
                double defaultDelta = 0.125;
                String answer = user.questionAnswerTable.get(questionId);
                switch(questionId){
                    //TODO: is there a way to not have to write out every case?
                    case 1:
                        // Do you wear a watch?
                        updateCategoryScoreTableBinary(user, ScoreCategory.FINANCE, answer, defaultDelta);
                        updateCategoryScoreTableBinary(user, ScoreCategory.EXTRAVAGANT, answer, defaultDelta);
                        break;

                    case 2:
                        // Do you enjoy camping?
                        updateCategoryScoreTableBinary(user, ScoreCategory.ADVENTUROUS, answer, defaultDelta);
                        updateCategoryScoreTableBinary(user, ScoreCategory.ACTIVE, answer, defaultDelta);
                        break;

                    case 3:
                        // Are you over 35 years old?
                        updateCategoryScoreTableBinary(user, ScoreCategory.EXTRAVAGANT, answer, defaultDelta);
                        break;

                    case 4:
                        // Do you enjoy traveling?
                        updateCategoryScoreTableBinary(user, ScoreCategory.ADVENTUROUS, answer, defaultDelta);
                        updateCategoryScoreTableBinary(user, ScoreCategory.CULTURE, answer, defaultDelta);
                        break;

                    case 5:
                        // Do you enjoy big groups?
                        updateCategoryScoreTableBinary(user, ScoreCategory.GROUPSIZE, answer, defaultDelta);
                        break;

                    case 6:
                        // Do you enjoy spending time in nature?
                        updateCategoryScoreTableBinary(user, ScoreCategory.BIRDWATCHER, answer, defaultDelta);
                        break;

                    case 7:
                        // Do you enjoy hiking in nature?
                        updateCategoryScoreTableBinary(user, ScoreCategory.ACTIVE, answer, defaultDelta);
                        updateCategoryScoreTableBinary(user, ScoreCategory.BIRDWATCHER, answer, defaultDelta);
                        break;

                    case 8:
                        // Do you generally enjoy spending lots of time with people?
                        updateCategoryScoreTableBinary(user, ScoreCategory.GROUPSIZE, answer, defaultDelta);
                        updateCategoryScoreTableBinary(user, ScoreCategory.LENGTH, answer, defaultDelta);
                        break;

                    case 9:
                        // Do you like animals?
                        updateCategoryScoreTableBinary(user, ScoreCategory.BIRDWATCHER, answer, defaultDelta);
                        break;

                    case 10:
                        // Do you enjoy meuseums?
                        updateCategoryScoreTableBinary(user, ScoreCategory.EXTRAVAGANT, answer, defaultDelta);
                        break;

                    case 11:
                        // Do you enjoy authentic foreign foods?
                        updateCategoryScoreTableBinary(user, ScoreCategory.CULTURE, answer, defaultDelta);
                        break;

                    

                    default:
                        System.out.println("FIX: Question " + questionId + " not handeled");
                        break;
                }

            }
        }

        // Merge user scores with each other, then add them to the database
        // To start, sum up all the scores, set confidence to 0, and add category

        for (User user : userList) {
            removeOldScores(user);
            insertScores(user);
            deleteDirtyUser(user);
        }
        System.out.println("Done updating dirty users. Sleeping for " + sleepTime/1000 + " seconds...");
        Thread.sleep(sleepTime);

    }} // End main method and while(true) loop

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
            Connection conn = getConnection();
            String sql = "DELETE FROM dirtyusers WHERE userId=" + user.id; // TODO: not working
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.executeUpdate();
            System.out.println("Successfully deleted user " + user + " from dirtyUsers");

        }catch (Exception e){
            System.out.println(e);
        }
    }

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
