package scheduling;

import java.util.*;
import java.util.function.ToDoubleFunction;

import static util.StringUtils.repeat;

public class ManeuverUtils
{
    
    public static String getSchedule(List<Maneuver> maneuvers)
    {
        double horizon = maneuvers
                .stream()
                .mapToDouble(m -> m.timeToFailure)
                .max()
                .orElseThrow(() -> new RuntimeException("No max found?"));


        StringBuilder result = new StringBuilder();
        for (Maneuver m : maneuvers)
        {
            int delay = Math.toIntExact(Math.round((m.initialDelay * 100) / horizon));

            int durat = Math.toIntExact(Math.round((m.maneuverDuration * 100) / horizon));
            if (durat == 0) durat++;

            int buffr = Math.toIntExact(Math.round((m.buffer * 100) / horizon));

            int reman = 100 - (delay + durat + buffr);
            if (reman < 0) reman = 0;

            String timeline = "<"
                    + repeat("-", delay)
                    + repeat("*", durat)
                    + repeat("-", buffr)
                    + "X"
                    + repeat("-", reman)
                    + ">";
            result.append(String.format("%s (%f, %f, %f)\n", timeline, m.initialDelay, m.maneuverDuration, m.buffer));
        }
        return result.toString();
    }

    static void printTimelines(List<Maneuver> maneuvers)
    {
        for (Maneuver m : maneuvers)
        {
            System.out.printf("%s%s%s\n",
                    repeat("-", (int) Math.round(m.initialDelay)),
                    repeat("*", (int) Math.round(m.maneuverDuration)),
                    repeat(".", (int) Math.round(m.buffer))
            );
        }
    }

    public static String getSchedule(List<Maneuver> maneuvers, ToDoubleFunction<Maneuver> fun)
    {
        maneuvers.sort(Comparator.comparingDouble(fun));
        return getSchedule(maneuvers);
    }

    static double overlapFactor_TotalRelative(List<Maneuver> maneuvers)
    {
        maneuvers = new ArrayList<>(maneuvers);

        double overlap = 0;

        for (int firstPlace = 0; firstPlace < maneuvers.size() - 1; firstPlace++)
        {
            for (int secondPlace = firstPlace + 1; secondPlace < maneuvers.size(); secondPlace++)
            {
                overlap += getOverlap(maneuvers.get(firstPlace), maneuvers.get(secondPlace));
            }
        }
        return overlap;
    }

    private static double getOverlap(Maneuver first, Maneuver second)
    {
        double gap;

        if (first.initialDelay > second.timeToRecovery || second.initialDelay > first.timeToRecovery)
        {
            gap = 0.0;
        }
        else if (first.initialDelay <= second.initialDelay)
        {
            gap = first.timeToRecovery - second.initialDelay;
            if (gap > second.maneuverDuration) gap = second.maneuverDuration;
        }
        else
        {
            gap = second.timeToRecovery - first.initialDelay;
            if (gap > first.maneuverDuration) gap = first.maneuverDuration;
        }

        if (gap < 0)
        {
            gap = 0;
        }
        return gap;
    }

    static double overlapFactor_simple(List<Maneuver> maneuvers)
    {
        maneuvers = new ArrayList<>(maneuvers);
        maneuvers.sort(Comparator.comparingDouble(m -> m.initialDelay));

        double overlap = 0;

        while(!maneuvers.isEmpty())
        {
            Maneuver firstVal = maneuvers.get(0);

            for (int secondPlace = 1; secondPlace < maneuvers.size(); secondPlace++)
            {
                overlap += getOverlap(firstVal, maneuvers.get(secondPlace));
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
