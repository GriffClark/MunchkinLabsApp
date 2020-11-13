import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;


public class Res {
    public static Connection getConnection() throws Exception {
        // Used in Res
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

    public static ArrayList<String> getColFromTable(String tableName, String colName) throws Exception {
        // Return a column from a table TODO: see if this can be simplified
        try {
            ResultSet resultSet = execSQL("SELECT * FROM " + tableName + ";");

            ArrayList<String> result = new ArrayList<>();

            while(resultSet.next()) {
                result.add(resultSet.getString(colName));
            }

            System.out.println("All records have been selected");
            return result;

        } catch (Exception e){
            System.out.println(e);
        }

        return null;
    }

    public static ArrayList<User> getUsers(String[] ids) {
        try {
            ArrayList<User> results = new ArrayList<>();
            for(String id: ids){
                User user = new User();
                user.id = Integer.parseInt(id);
                ResultSet resultSet = execSQL("SELECT * FROM scores WHERE id=" + id + ";"); 
                while(resultSet.next()){ 
                    user.categoryScoreTable.put(resultSet.getString("category"), Double.parseDouble(resultSet.getString("value")));
                }
            }
            return results;
        } catch (Exception e) {
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

    public static ResultSet execSQL(String sql) throws Exception{
        Connection conn = getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);
        return statement.executeQuery();
    }

    public static ArrayList<Endpoint> getAllEndpoints() {
        try {
            ArrayList<Endpoint> endpoints = new ArrayList<>();
            ResultSet resultSet = execSQL("SELECT * FROM endpoints;");
            while(resultSet.next()) {
                int id = Integer.parseInt(resultSet.getString("id"));
                String url = resultSet.getString("url");
                String name = resultSet.getString("name");

                // now need to build the vector list

                Hashtable<String, Double> endpointVectors = new Hashtable<>();

                ResultSet endpointVectorRS = execSQL("SELECT * FROM endpointvectors WHERE vectorId="+id+";");
                while(endpointVectorRS.next()){
                    endpointVectors.put(endpointVectorRS.getString("vectorName"), Double.parseDouble(endpointVectorRS.getString("value")));
                }

                Endpoint endpoint = new Endpoint(id, name, url, endpointVectors);
                endpoints.add(endpoint);
            }

            return endpoints;

        } catch(Exception e) {
            System.out.println(e);
        }
        return null;
    }
    

    public static void updateCategoryScoreTableBinary(User user, String scoreCategory, String answer, double delta){
        // For a yes/no question, update the scoretable
        
        // This assumes that a yes will always make the score go up, and a no will always make the score go down
        if(Integer.parseInt(answer) == 0){  // The user said no
            delta *= -1; 
        }
        user.categoryScoreTable.put(scoreCategory, user.categoryScoreTable.get(scoreCategory) + delta);
    }

    

    
    
}
