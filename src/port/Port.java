package port;

import org.apache.log4j.Logger;
import ship.Ship;
import warehouse.Container;
import warehouse.Warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {

	private final static Logger logger = Logger.getLogger("console");
	
	private BlockingQueue<Berth> berthList; // очередь причалов
	private Warehouse portWarehouse; // хранилище порта
	
	private Map<Ship, Berth> usedBerths; // какой корабль у какого причала стоит
	private Lock lock;


	public Port(int berthSize, int warehouseSize) {
		portWarehouse = new Warehouse(warehouseSize); // создаем пустое хранилище
		berthList = new ArrayBlockingQueue<Berth>(berthSize); // создаем очередь причалов
		for (int i = 0; i < berthSize; i++) { // заполняем очередь причалов непосредственно самими причалами
			berthList.add(new Berth(i, portWarehouse));
		}
		usedBerths = new HashMap<Ship, Berth>(); // создаем объект, который будет
		// хранить связь между кораблем и причалом
		logger.debug("Порт создан.");
		lock = new ReentrantLock();
	}
	
	public void setContainersToWarehouse(List<Container> containerList){
		portWarehouse.addContainer(containerList);
	}

	public  boolean lockBerth(Ship ship) {
		Berth berth;
		try {

				berth = berthList.take();
				usedBerths.put(ship, berth);

		} catch (InterruptedException e) {
			logger.debug("Кораблю " + ship.getName() + " отказано в швартовке.");
			return false;
		}
		return true;
	}
	
	
	public boolean unlockBerth(Ship ship) {
		Berth berth = usedBerths.get(ship);
		
		try {
			berthList.put(berth);
			usedBerths.remove(ship);
		} catch (InterruptedException e) {
			logger.debug("Корабль " + ship.getName() + " не смог отшвартоваться.");
			return false;
		}		
		return true;
	}
	
	public Berth getBerth(Ship ship) throws PortException {
		
		Berth berth = usedBerths.get(ship);
		if (berth == null){
			throw new PortException("Try to use Berth without blocking.");
		}
		return berth;		
	}

	public Map<Ship, Berth> getUsedBerths(){return usedBerths;}

	public BlockingQueue<Berth> getEmptyBerths(){return berthList;}

	public Warehouse getPortWarehouse(){return portWarehouse;}

	public Lock getLock() {return lock;}
}
