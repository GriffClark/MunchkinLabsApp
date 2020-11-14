import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class ReccomendEndpoint {
    
    
    public static void main(String[] args) throws Exception{
        // args will contian user ids
        String[] testIds = {"12","14"};

        // This will contain each user's distance from a vector as a distanceVector

        ArrayList<User> users = Res.getUsers(testIds);
        ArrayList<Endpoint> endpoints = Res.getAllEndpoints();

        // Find the user distance from each vector
        for(User user : users){
            for(Endpoint endpoint : endpoints){
                double oldValue = 0;
                user.vectorDistanceTable.put(endpoint.id, (user.personalityVector.getDistanceFrom(endpoint.vector)+ oldValue)); //all values here should be positive
            }
        }

        // We'll use the first user's table 
        Hashtable<Integer, Double> idDistanceTable = new Hashtable<>(); // Stores each vector with the combined distance from both users

        for(User user : users){
            user.vectorDistanceTable.forEach((key, value) -> idDistanceTable.merge(key, value, (v1, v2) -> (v1 == v2) ? v1 : v1 + v2)); 
        }

        Set<Integer> keys = idDistanceTable.keySet();
        int lowestVectorId = -1;
        for(int id : keys){
            if(lowestVectorId == -1){
                lowestVectorId = id;
            }
            else if (idDistanceTable.get(id) < idDistanceTable.get(lowestVectorId)) {
                lowestVectorId = id;
            }
        }

        System.out.println("\nYou should go to: " + endpoints.get(lowestVectorId).name);
    }



}