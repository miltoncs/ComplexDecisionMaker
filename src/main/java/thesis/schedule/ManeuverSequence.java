package thesis.schedule;

import java.util.*;

public class ManeuverSequence
{
    private double horizon = 1;
    public List<Maneuver> maneuvers = new LinkedList<>();

    public void addManeuver(Maneuver m)
    {
        if (horizon < m.timeToFailure)
        {
            horizon = m.timeToFailure;
        }

        maneuvers.add(m);
    }

    public List<Maneuver> maximizeManeuverCount()
    {
        List<Maneuver> sequence = new LinkedList<>(maneuvers);
        sequence.sort(Comparator.comparingDouble(m -> m.timeToFailure));

        sequence.set(0, sequence.get(0).withDelay(0.0));

        for (int i = 0; i < sequence.size()-1;)
        {
            Maneuver first = sequence.get(i);
            Maneuver second = sequence.get(i+1);

            if (first.timeToRecovery < (second.initialDelay + second.buffer))
            {
                sequence.set(i+1, second.withDelay(first.timeToRecovery));
                i++;
            }
            else
            {
                sequence.remove(second);
            }
        }
        return sequence;
    }

    public List<Maneuver> minimizeOverlap()
    {
        List<Maneuver> sequence = new LinkedList<>(maneuvers);
        sequence.sort(Comparator.comparingDouble(m -> m.timeToFailure));

        sequence.set(0, sequence.get(0).withDelay(0.0));

        for (int i = 0; i < sequence.size()-1; i++  )
        {
            Maneuver first = sequence.get(i);
            Maneuver second = sequence.get(i+1);

            if (first.timeToRecovery < (second.initialDelay + second.buffer))
            {
                sequence.set(i+1, second.withDelay(first.timeToRecovery));
            }
            else
            {
                sequence.set(i+1, second.withZeroBuffer());
            }
        }
        return sequence;
    }
}