package thesis.realtime;

import thesis.schedule.Maneuver;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public class Supervisor
{
    private ConcurrentLinkedQueue<Maneuver> maneuvers = new ConcurrentLinkedQueue<>();
    private static boolean running = true;

    public static void main(String[] args)
    {
        Executors.newSingleThreadExecutor().execute(() -> new Supervisor().startSupervisor());
    }

    private void startSupervisor()
    {
        while (running)
        {
            try
            {
                checkAndPerformManeuver(maneuvers.remove());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                running = false;
            }
        }
    }

    public void addManuever(Maneuver m)
    {
        maneuvers.add(m);
    }

    private void checkAndPerformManeuver(Maneuver m)
    {
        // Given constraint representation, check proposed maneuvers
    }
}
