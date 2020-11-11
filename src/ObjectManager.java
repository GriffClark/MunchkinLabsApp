
import java.util.ArrayList;
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

public class ObjectManager {
    public static double defaultScoreValue = 0.5;
    public static long sleepTime = 30000; //ms
    

    

    public static void main (String[] args) throws Exception{
        while(true){
            // Update dirty users
        ArrayList<String> dirtyUsers = Res.getColFromTable("dirtyusers", "userId"); // TODO: Handle duplicate users
        ArrayList<User> userList = new ArrayList<>();

        // For each user, figure out which values need to be updated
        for(String id: dirtyUsers) {
            try{
                User user = new User();
                user.id = Integer.parseInt(id);
                user.questionAnswerTable = (Res.getValuesToUpdate(id));
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
                    case 1:
                        // Do you wear a watch?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.FINANCE, answer, defaultDelta);
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.EXTRAVAGANT, answer, defaultDelta);
                        break;

                    case 2:
                        // Do you enjoy camping?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.ADVENTUROUS, answer, defaultDelta);
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.ACTIVE, answer, defaultDelta);
                        break;

                    case 3:
                        // Are you over 35 years old?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.EXTRAVAGANT, answer, defaultDelta);
                        break;

                    case 4:
                        // Do you enjoy traveling?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.ADVENTUROUS, answer, defaultDelta);
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.CULTURE, answer, defaultDelta);
                        break;

                    case 5:
                        // Do you enjoy big groups?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.GROUPSIZE, answer, defaultDelta);
                        break;

                    case 6:
                        // Do you enjoy spending time in nature?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.BIRDWATCHER, answer, defaultDelta);
                        break;

                    case 7:
                        // Do you enjoy hiking in nature?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.ACTIVE, answer, defaultDelta);
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.BIRDWATCHER, answer, defaultDelta);
                        break;

                    case 8:
                        // Do you generally enjoy spending lots of time with people?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.GROUPSIZE, answer, defaultDelta);
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.LENGTH, answer, defaultDelta);
                        break;

                    case 9:
                        // Do you like animals?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.BIRDWATCHER, answer, defaultDelta);
                        break;

                    case 10:
                        // Do you enjoy meuseums?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.EXTRAVAGANT, answer, defaultDelta);
                        break;

                    case 11:
                        // Do you enjoy authentic foreign foods?
                        Res.updateCategoryScoreTableBinary(user, ScoreCategory.CULTURE, answer, defaultDelta);
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
            user.update();
        }

        // Next, we need to remove redundant entries from answers 
        //TODO: replace duplicate answer entries

        

        System.out.println("Done updating dirty users. Sleeping for " + sleepTime/1000 + " seconds...");
        Thread.sleep(sleepTime);

    }} // End main method and while(true) loop

   
}
