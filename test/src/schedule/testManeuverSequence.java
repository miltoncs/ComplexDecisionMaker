package schedule;

import org.junit.Test;

import java.util.Random;

public class testManeuverSequence
{
    private long seed = 0;
    private Random rng = new Random();

    @Test
    public void simpleTest()
    {
        ManeuverSequence plan = new ManeuverSequence();
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        System.out.println(ManeuverUtils.getScheduleString(plan.maneuvers));
    }

    private Maneuver newMan()
    {
        if (seed == 0)
        {
            seed = Math.abs(rng.nextLong());
            rng.setSeed(seed);
            System.out.println("Seed: " + seed);
        }

        return new Maneuver(
                rng.nextInt(20) + rng.nextDouble(),
                rng.nextInt(20) + rng.nextDouble(),
                rng.nextInt(20) + rng.nextDouble());
    }

    @Test
    public void printScheduleTest()
    {
        ManeuverSequence plan = new ManeuverSequence();
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        System.out.println(ManeuverUtils.getScheduleString(plan.maneuvers));
    }

    @Test
    public void testMaximizer()
    {
        ManeuverSequence plan = new ManeuverSequence();

        for (int i = 0; i < 5; i++)
        {
            plan.addManeuver(newMan());
        }

        System.out.println("Randomized Maneuvers:");
        System.out.println(ManeuverUtils.getScheduleString(plan.maneuvers));

        System.out.println();

        System.out.println("Schedule sorted by TimeToRecovery:");
        System.out.println(ManeuverUtils.getScheduleString(plan.maneuvers, m -> m.timeToRecovery));

        System.out.println();

        System.out.println("Most non-overlapping maneuvers:");
        System.out.println(ManeuverUtils.getScheduleString(plan.maximizeManeuverCount()));

        System.out.println();

        System.out.println("All maneuvers, least overlap:");
        System.out.println(ManeuverUtils.getScheduleString(plan.minimizeOverlap()));
    }

    @Test
    public void testOverlapFactor()
    {
        ManeuverSequence sequence = new ManeuverSequence();

        for (int i = 0; i < 3; i++) {
            sequence.addManeuver(newMan());
        }

        ManeuverUtils.printTimelines(sequence.maneuvers);
        System.out.printf("Overlap Factor (simple): %.2f\n", ManeuverUtils.overlapFactor_simple(sequence.maneuvers));

        System.out.printf("Overlap Factor (relative): %.2f\n", ManeuverUtils.overlapFactor_TotalRelative(sequence.maneuvers));
    }

    @Test
    public void comprehensiveTest()
    {
        ManeuverSequence plan = new ManeuverSequence();

        for (int i = 0; i < 5; i++)
        {
            plan.addManeuver(newMan());
        }

        System.out.println("Randomized Maneuvers:");
        System.out.println(ManeuverUtils.getScheduleString(plan.maneuvers));

        System.out.println();

        System.out.println("Schedule sorted by TimeToRecovery:");
        System.out.println(ManeuverUtils.getScheduleString(plan.maneuvers, m -> m.timeToRecovery));

        System.out.println();

        System.out.println("All maneuvers, least overlap:");
        System.out.println(ManeuverUtils.getScheduleString(plan.minimizeOverlap()));
        System.out.printf("Overlap Factor (simple): %.2f\n", ManeuverUtils.overlapFactor_simple(plan.minimizeOverlap()));
        System.out.printf("Overlap Factor (relative): %.2f\n", ManeuverUtils.overlapFactor_TotalRelative(plan.minimizeOverlap()));

        System.out.println();

        System.out.println("Most non-overlapping maneuvers:");
        System.out.println(ManeuverUtils.getScheduleString(plan.maximizeManeuverCount()));
    }
}
