package thesis.schedule;

public class Maneuver implements Comparable<Maneuver>
{
    final double initialDelay;
    final double maneuverDuration;
    final double timeToFailure;
    final double timeToRecovery;
    final double timeToLastManeuver;
    final double windowToManeuver;
    final double windowRatio;
    final double buffer;

    public Maneuver(double initialDelay, double maneuverDuration, double buffer)
    {
        if (initialDelay < 0 || maneuverDuration < 0 || buffer < 0)
        {
            System.err.println("Times are never less than zero!");
            System.exit(1);
        }

        this.initialDelay = initialDelay;
        this.maneuverDuration = maneuverDuration;
        this.buffer = buffer;

        timeToFailure = initialDelay + maneuverDuration + buffer;
        timeToRecovery = initialDelay + maneuverDuration;
        timeToLastManeuver = timeToFailure - maneuverDuration;
        windowToManeuver = timeToFailure - initialDelay;
        windowRatio =  maneuverDuration / windowToManeuver;
    }

    @Override
    public int compareTo(Maneuver o)
    {
        return Double.compare(windowRatio, o.windowRatio);
    }

    @Override
    public String toString()
    {
        return toNormalizedString((int) (initialDelay + maneuverDuration + buffer));
    }

    public String toNormalizedString(int width)
    {
        int delay = Math.toIntExact(Math.round((initialDelay * width) / timeToFailure));

        int duration = Math.toIntExact(Math.round((maneuverDuration * width) / timeToFailure));
        if (duration < 1) duration = 1;

        int remaining = width - (delay + duration);

        return  "|"
                + ManeuverUtils.repeat(" ", delay)
                + ManeuverUtils.repeat("=", duration)
                + ManeuverUtils.repeat(" ", remaining)
                + "X";
    }

    public Maneuver withDelay(double newDelay)
    {
        if (newDelay > initialDelay + buffer)
        {
            throw new RuntimeException("New delay is too big...");
        }

        return new Maneuver(newDelay, maneuverDuration, buffer - (newDelay - initialDelay));
    }

    public Maneuver withZeroBuffer()
    {
        return new Maneuver(initialDelay + buffer, maneuverDuration, 0.0);
    }
}
