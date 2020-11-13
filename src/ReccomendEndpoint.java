import java.util.ArrayList;
import java.util.Hashtable;

public class ReccomendEndpoint {
    
    
    public static void main(String[] args) throws Exception{
        // args will contian user ids
        ArrayList<User> users = Res.getUsers(args);
        
        ArrayList<Endpoint> endpoints = Res.getAllEndpoints();
        System.out.println("done");
    }



}