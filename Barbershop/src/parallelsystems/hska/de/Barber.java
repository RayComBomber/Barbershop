package parallelsystems.hska.de;

import java.util.concurrent.ThreadLocalRandom;

public class Barber implements Runnable {

	private static final int TIME_FOR_HAIRCUT_MAX = 6000;
	private static final int TIME_FOR_HAIRCUT_MIN = 4000;
	private static final int TIME_FOR_SLEEPING_MAX = 1500;
	private static final int TIME_FOR_SLEEPING_MIN = 700;

	private int id;
	private boolean shutdown = false;
	private BarberShop shop;

	public Barber(int id, BarberShop shop) {
		this.id = id;
		this.shop = shop;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Customer customer = null;
				
				// Wait for customer or exit
				while (customer == null) {
					customer = this.shop.getNextCustomerForHairCut();
					if (customer == null) {
						if (this.shutdown) {
							// Exit if all customers are gone AND if shutdown flag is true
							System.out.println(this.toString() + " goes home ...");
							return;
						} else {
							System.out.println(this.toString() + " sleeps.");
							final int timeForSleeping = ThreadLocalRandom.current().nextInt(TIME_FOR_SLEEPING_MIN,
									TIME_FOR_SLEEPING_MAX + 1);
							Thread.sleep(timeForSleeping);
						}
					}
				}
				
				// Serve customer
				System.out.println(this.toString() + " cuts hair of " + customer.toString() + ".");
				final int timeForHairCut = ThreadLocalRandom.current().nextInt(TIME_FOR_HAIRCUT_MIN,
						TIME_FOR_HAIRCUT_MAX + 1);
				Thread.sleep(timeForHairCut);
				shop.doPayment(this, customer);
				System.out.println(customer.toString() + " leaves the Shop.");

			}
		} catch (InterruptedException e) {
			System.err.println("Catched InterruptedException from " + this.toString());
		}

	}

	public void shutdown() {
		this.shutdown = true;
	}

	@Override
	public String toString() {
		return "Barber " + id;
	}

}
