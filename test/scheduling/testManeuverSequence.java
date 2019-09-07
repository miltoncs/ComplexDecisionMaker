package scheduling;

import org.junit.Test;
import scheduling.Maneuver;
import scheduling.ManeuverSequence;
import scheduling.ManeuverUtils;

import java.util.List;
import java.util.Random;

public class testManeuverSequence
{
    private Random rng = new Random();

    @Test
    public void simpleTest()
    {
        ManeuverSequence plan = new ManeuverSequence();
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        System.out.println(ManeuverUtils.getSchedule(plan.maneuvers));
    }

    private Maneuver newMan()
    {
        return new Maneuver(
                0,
                rng.nextInt(20) + rng.nextDouble(),
                rng.nextInt(200) + rng.nextDouble());
    }

    @Test
    public void printScheduleTest()
    {
        ManeuverSequence plan = new ManeuverSequence();
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        plan.addManeuver(newMan());
        System.out.println(ManeuverUtils.getSchedule(plan.maneuvers));
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
        System.out.println(ManeuverUtils.getSchedule(plan.maneuvers));

        System.out.println();

        System.out.println("Schedule sorted by TimeToRecovery:");
        System.out.println(ManeuverUtils.getSchedule(plan.maneuvers, m -> m.timeToRecovery));

        System.out.println();

        System.out.println("Most non-overlapping maneuvers:");
        System.out.println(ManeuverUtils.getSchedule(plan.maximizeManeuverCount()));

        System.out.println();

        System.out.println("All maneuvers, least overlap:");
        System.out.println(ManeuverUtils.getSchedule(plan.minimizeOverlap()));
    }

    @Test
    public void testOverlapFactor()
    {
        ManeuverSequence sequence = new ManeuverSequence();

        for (int i = 0; i < 3; i++) {
            sequence.addManeuver(newMan());
        }

        ManeuverUtils.printTimelines(sequence.maneuvers);
        printOverlapFactors(sequence.maneuvers);
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
        System.out.println(ManeuverUtils.getSchedule(plan.maneuvers));

        System.out.println();

        System.out.println("Schedule sorted by TimeToRecovery:");
        System.out.println(ManeuverUtils.getSchedule(plan.maneuvers, m -> m.timeToRecovery));

        System.out.println();

        System.out.println("Most non-overlapping maneuvers:");
        System.out.println(ManeuverUtils.getSchedule(plan.maximizeManeuverCount()));

        System.out.println();

        System.out.println("All maneuvers, least overlap:");
        System.out.println(ManeuverUtils.getSchedule(plan.minimizeOverlap()));
        printOverlapFactors(plan.minimizeOverlap());

        ManeuverUtils.printTimelines(plan.maneuvers);
        printOverlapFactors(plan.maneuvers);
    }

    private void printOverlapFactors(List<Maneuver> mans) {
        System.out.printf("Overlap Factor (simple): %.2f\n", ManeuverUtils.overlapFactor_simple(mans));
        System.out.printf("Overlap Factor (relative): %.2f\n", ManeuverUtils.overlapFactor_TotalRelative(mans));
    }
}
