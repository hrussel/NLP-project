package probabilityModel;

import model.*;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Andy on 12/8/2015.
 */
public class LocationModel
{
    private StringSpan toStr;
    private Connection conn;
    private StringSpan verb_i;
    private static Map<String, Map<String, Integer>> locVerbCounts = new HashMap<>();

    public LocationModel(Connection conn) {
        this.conn = conn;
        this.toStr = conn.getToStringSpan();
        this.verb_i = conn.getToAction().getPredicate();

    }

    public double calculateProbability()
    {
        double totalProbability= 0;
        //!attention Needs to be checked if location is retrieved properly
        Action fromAction = conn.getFromAction();
        StringSpan fromLocation = fromAction.getArguments().get(0).getWords().get(0);


        if (!(toStr.equals(""))) //case it's not implicit
        {
            if (toStr.getBaseWord().matches(".*" + verb_i + ".*")) {
                totalProbability = 1;
            }
        }
        else //case implicit
        {
            if (locVerbCounts.containsKey(verb_i))
            {
                Map<String, Integer> locationCounts = locVerbCounts.get(verb_i);
                if (locationCounts.containsKey(fromLocation.getBaseWord()))
                {
                    locationCounts.put(fromLocation.getBaseWord(), locationCounts.get(fromLocation.getBaseWord()) + 1);

                } else
                {
                    locationCounts.put(fromLocation.getBaseWord(), 1);
                }

            } else
            {
                Map<String, Integer> locationCounts = new HashMap<>();
                locationCounts.put(fromLocation.getBaseWord(), 1);
                locVerbCounts.put(verb_i.getBaseWord(), locationCounts);

            }

            /*TODO total probability =
            Times a verb appears with a location
            over the times the verb appears in general */
        }




        return totalProbability;
    }
}
