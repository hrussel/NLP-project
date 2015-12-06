package model;

import java.util.List;

/**
 * Created by Andy on 12/5/2015.
 */
public class ArgumentTypesModel {

    Argument arg;
    Recipe recipe;

    public ArgumentTypesModel(Recipe recipe, Argument arg)
    {
        this.recipe =  recipe;
        this.arg = arg;
    }

    /* Arguments types probability
        For all strings in argument I J
        check whether all connections coming into them are of the same type (tsym tsem)
        iff true probability = 1
            else probability = 0
     */


    public double calculateProbability ()
    {


        double totalprobability=1;

        for (StringSpan str : arg.getWords()  ) {
            boolean firstPass=true;
            Connection previousConnection=  null;
            List<Connection> connections = recipe.getConnectionsGoingTo(str);
            for(Connection con : connections)
            {
                if(firstPass)
                {
                    previousConnection = con;
                    firstPass=false;
                }


                SemanticType previousSemType = previousConnection.getSemanticType();
                SyntacticType previousSynType = previousConnection.getSyntacticType();


                if (! (con.getSemanticType().equals(previousSemType))&& !(con.getSyntacticType().equals(previousSynType )))
                {

                    totalprobability = 0;
                }
                else totalprobability =1;

                previousConnection = con;
            }

        }




        return totalprobability;
    }

}
