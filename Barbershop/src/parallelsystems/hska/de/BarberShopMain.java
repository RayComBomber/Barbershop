package parallelsystems.hska.de;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BarberShopMain {

	private static final int BARBER_COUNT = 3;
	private static final int customerSofaSize = 4;
	private static final int customerStandingSize = 16;
	private static final int MAX_CUSTOMER_COUNT = 35;
	private static final int TIME_BETWEEN_CUSTOMERS = 1000;

	private static ScheduledExecutorService schedulePool = Executors.newScheduledThreadPool(1);
	private static ExecutorService pool = Executors.newFixedThreadPool(8);

	private static ScheduledFuture<?> futureCustomers;
	private static List<Barber> barberThreads = new ArrayList<>();
	private static List<Future<?>> barberFutures = new ArrayList<>();
	private static List<Future<?>> cutomerFutures = new ArrayList<>();

	public static void main(String[] args) {
		System.out.println("Barber Shop Simulation:");

		// Init
		BarberShop shop = new BarberShop(customerSofaSize, customerStandingSize);

		// create barber
		for (int i = 0; i < BARBER_COUNT; i++) {
			Barber newBarber = new Barber(i, shop);
			barberThreads.add(newBarber);
			barberFutures.add(pool.submit(newBarber));
		}

		// create customers
		futureCustomers = schedulePool.scheduleAtFixedRate(() -> {
			createCustomers(shop);
		}, 0, TIME_BETWEEN_CUSTOMERS, TimeUnit.MILLISECONDS);
	}

	private static synchronized void createCustomers(BarberShop shop) {
		int customerId = Customer.getAndIncrementGlobalCustomerCount();
		Customer newCostomer = new Customer(customerId, shop);
		cutomerFutures.add(pool.submit(newCostomer));

		if (customerId == MAX_CUSTOMER_COUNT) {
			futureCustomers.cancel(false);
			for (Barber barberThread : barberThreads) {
				barberThread.shutdown();
			}
			waitForTermination();
		}
	}

	private static void waitForTermination() {
		// Wait until all open tasks are done
		// Iterate ALL futures
		cutomerFutures.addAll(barberFutures);
		for (Future<?> f : cutomerFutures) {
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		// end ExecutorServices
		pool.shutdown();
		schedulePool.shutdown();

		try {
			pool.awaitTermination(10, TimeUnit.SECONDS);
			schedulePool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Barber Shop closed.");
	}
}
