package thesis.schedule;

import java.util.*;
import java.util.function.ToDoubleFunction;

public class ManeuverUtils
{
    public static String getScheduleString(List<Maneuver> maneuvers)
    {
        double maxTimeToFailure = maneuvers
                .stream()
                .mapToDouble(m -> m.timeToFailure)
                .max()
                .orElseThrow(() -> new RuntimeException("No max found?"));


        StringBuilder result = new StringBuilder();

        for (Maneuver m : maneuvers)
        {
            int delay = Math.toIntExact(Math.round((m.initialDelay * 100) / maxTimeToFailure));

            int durat = Math.toIntExact(Math.round((m.maneuverDuration * 100) / maxTimeToFailure));
            if (durat == 0) durat++;

            int buffr = Math.toIntExact(Math.round((m.buffer * 100) / maxTimeToFailure));

            int reman = 100 - (delay + durat + buffr);
            if (reman < 0) reman = 0;

            String timeline =
                    m.toNormalizedString(delay + durat + buffr)
                    + repeat(" ", reman)
                    + "|";
            result.append(String.format("%s (%f, %f, %f)\n", timeline, m.initialDelay, m.maneuverDuration, m.buffer));
        }
        return result.toString();
    }

    public static void printTimelines(List<Maneuver> maneuvers)
    {
        for (Maneuver m : maneuvers)
        {
            System.out.println(m.toString());
        }
    }

    public static String repeat(String c, int count)
    {
        return String.join("", Collections.nCopies(count, c));
    }

    public static String getScheduleString(List<Maneuver> maneuvers, ToDoubleFunction<Maneuver> fun)
    {
        maneuvers.sort(Comparator.comparingDouble(fun));
        return getScheduleString(maneuvers);
    }

    public static double overlapFactor_TotalRelative(List<Maneuver> maneuvers)
    {
        maneuvers = new ArrayList<>(maneuvers);

        double overlap = 0;

        for (int firstPlace = 0; firstPlace < maneuvers.size() - 1; firstPlace++)
        {
            for (int secondPlace = firstPlace + 1; secondPlace < maneuvers.size(); secondPlace++)
            {
                Maneuver firstVal = maneuvers.get(firstPlace);
                Maneuver secondVal = maneuvers.get(secondPlace);

                double gap;

                if (firstVal.initialDelay <= secondVal.initialDelay)
                {
                    gap = firstVal.timeToRecovery - secondVal.initialDelay;
                    if (gap > secondVal.maneuverDuration) gap = secondVal.maneuverDuration;
                }
                else
                {
                    gap = secondVal.timeToRecovery - firstVal.initialDelay;
                    if (gap > firstVal.maneuverDuration) gap = firstVal.maneuverDuration;
                }
                if (gap > 0) overlap += gap ;
            }
        }
        return overlap;
    }

    public static double overlapFactor_simple(List<Maneuver> maneuvers)
    {
        maneuvers = new ArrayList<>(maneuvers);
        maneuvers.sort(Comparator.comparingDouble(m -> m.initialDelay));

        double overlap = 0;

        while(!maneuvers.isEmpty())
        {
            Maneuver firstVal = maneuvers.get(0);

            for (int secondPlace = 1; secondPlace < maneuvers.size(); secondPlace++)
            {
                Maneuver secondVal = maneuvers.get(secondPlace);

                double gap;

                if (firstVal.initialDelay <= secondVal.initialDelay)
                {
                    gap = firstVal.timeToRecovery - secondVal.initialDelay;
                    if (gap > secondVal.maneuverDuration) gap = secondVal.maneuverDuration;
                }
                else
                {
                    gap = secondVal.timeToRecovery - firstVal.initialDelay;
                    if (gap > firstVal.maneuverDuration) gap = firstVal.maneuverDuration;
                }
                if (gap > 0) overlap += gap ;
            }
            maneuvers.remove(0);


            for (int i = 0; i < maneuvers.size(); i++)
            {
                double shrinkTime = firstVal.timeToRecovery;
                Maneuver m = maneuvers.get(i);

                double delay;

                if (m.initialDelay < shrinkTime)
                {
                    delay = 0;
                    shrinkTime -= m.initialDelay;
                }
                else
                {
                    delay = m.initialDelay - shrinkTime;
                    shrinkTime = 0;
                }

                double durat;

                if (m.maneuverDuration < shrinkTime)
                {
                    durat = 0;
                    shrinkTime -= m.maneuverDuration;
                }
                else
                {
                    durat = m.maneuverDuration - shrinkTime;
                    shrinkTime = 0;
                }

                double reman;

                if (m.buffer < shrinkTime)
                {
                    reman = 0;
                }
                else
                {
                    reman = m.buffer - shrinkTime;
                }

                maneuvers.set(i, new Maneuver(delay, durat, reman));
            }
        }
        return overlap;
    }
}
