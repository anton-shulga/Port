package dispatcher;


import org.apache.log4j.Logger;
import port.Berth;
import port.Port;
import ship.Ship;
import warehouse.Warehouse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Антон on 12.11.2015.
 */
public class Dispatcher implements Runnable {

    private final static int TIME_TO_SLEEP = 500;

    private Port port;
    private static Logger logger = Logger.getLogger("file");
    private volatile boolean stopThread = false;

    public Dispatcher(Port port) {
        this.port = port;
    }

    public void run() {
        while (!stopThread) {
            logMessage();
            sleep();
        }
    }

    public void stopThread() {
        stopThread = true;
    }

    private void logMessage() {
        Map<Ship, Berth> berths = port.getUsedBerths();
        Warehouse warehouse = port.getPortWarehouse();
        BlockingQueue<Berth> emptyBetrhs = port.getEmptyBerths();
        logger.debug("На складе порта хранится " + warehouse.getRealSize() + " контейнеров");

        for (Map.Entry<Ship, Berth> pair : berths.entrySet()) {
            logger.debug("Корабль " + pair.getKey().getName() + " находится у причала " + pair.getValue().getId() + ". "
                    + "На корабле хранится " + pair.getKey().getWarehouse().getRealSize() + " контейнеров");
        }

        for (Berth berth : emptyBetrhs) {
            logger.debug("Причал " + berth.getId() + " пустой");
        }

        logger.debug("-------------------------------------------------------");
        try {
            Thread.sleep(TIME_TO_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(TIME_TO_SLEEP);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}