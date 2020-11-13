import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class ReccomendEndpoint {
    
    
    public static void main(String[] args) throws Exception{
        // args will contian user ids
        String[] testIds = {"11","7"};

        // This will contain each user's distance from a vector as a distanceVector
        Hashtable<Integer, Double> idDistanceTable = new Hashtable<>(); // We don't care which user it's distanced from, just which vector it is

        ArrayList<User> users = Res.getUsers(testIds);
        ArrayList<Endpoint> endpoints = Res.getAllEndpoints();

        // Find the user distance from each vector
        for(User user : users){
            for(Endpoint endpoint : endpoints){
                double oldValue = 0;
                if(idDistanceTable.get(endpoint.id) != null){
                    oldValue = idDistanceTable.get(endpoint.id);
                }
                idDistanceTable.put(endpoint.id, (user.personalityVector.getDistanceFrom(endpoint.vector)+ oldValue)); //all values here should be positive
            }
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

        System.out.println(endpoints.get(lowestVectorId).name);
    }



}