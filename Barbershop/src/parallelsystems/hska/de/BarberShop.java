package parallelsystems.hska.de;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {

	private static final int TIME_FOR_PAYMENT_MAX = 2800;
	private static final int TIME_FOR_PAYMENT_MIN = 1700;

	private final BlockingQueue<Customer> customerSofaQueue;
	private final BlockingQueue<Customer> customerStandingQueue;

	private final ReentrantLock customerLock = new ReentrantLock();
	private final ReentrantLock paymentLock = new ReentrantLock();
	private final int customerSofaSize;
	private final int customerStandingSize;

	public BarberShop(int customerSofaSize, int customerStandingSize) {
		this.customerSofaSize = customerSofaSize;
		this.customerStandingSize = customerStandingSize;
		this.customerSofaQueue = new ArrayBlockingQueue<Customer>(customerSofaSize);
		this.customerStandingQueue = new ArrayBlockingQueue<Customer>(customerStandingSize);
	}

	/**
	 * Called by a barber thread to get his next customer from the sofa.
	 * @return next customer
	 * @throws InterruptedException
	 */
	public Customer getNextCustomerForHairCut() throws InterruptedException {
		customerLock.lock();
		if (customerSofaQueue.isEmpty()) {
			// No customers are waiting, let the barber return, so he can sleep.
			customerLock.unlock();
			return null;
		}

		Customer nextCustomer = customerSofaQueue.take();
		if (!customerStandingQueue.isEmpty()) {
			customerSofaQueue.add(customerStandingQueue.take());
			System.out.println(nextCustomer.toString() + " sits on the sofa.");
		}
		customerLock.unlock();
		return nextCustomer;
	}

	/**
	 * Called by a customer thread to get a hair cut. 
	 * The customer will be queued until he gets served.
	 * @param customer
	 */
	public void getHairCut(Customer customer) {
		customerLock.lock();
		if (customerSofaQueue.size() < customerSofaSize) {
			customerSofaQueue.add(customer);
			System.out.println(customer.toString() + " sits on the sofa.");
		} else if (customerStandingQueue.size() < customerStandingSize) {
			customerStandingQueue.add(customer);
			System.out.println(customer.toString() + " stands in the shop.");
		} else {
			System.out.println(customer.toString() + " does not enter the shop because it's too full.");
		}
		customerLock.unlock();
	}

	/**
	 * Called by the barber thread to do the payment process with his customer.
	 * @param barber
	 * @param customer
	 * @throws InterruptedException
	 */
	public void doPayment(Barber barber, Customer customer) throws InterruptedException {
		paymentLock.lock();
		System.out.println(barber.toString() + " gets payed by " + customer.toString() + ".");
		final int timeForPayment = ThreadLocalRandom.current().nextInt(TIME_FOR_PAYMENT_MIN, TIME_FOR_PAYMENT_MAX + 1);
		Thread.sleep(timeForPayment);
		paymentLock.unlock();
	}

}
