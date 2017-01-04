package parallelsystems.hska.de;

public class Customer implements Runnable {
	
	private static int globalCustomerCount = 0;
	
	private int id;
	private BarberShop shop;
	
	public Customer(int id, BarberShop shop){
		this.id = id;
		this.shop = shop;
	}

	
	@Override
	public void run(){
		shop.getHairCut(this);
	}
	
	@Override
	public String toString(){
		return "Customer " + id;
	}
	
	public int getId() {
		return id;
	}
	
	public static synchronized int getAndIncrementGlobalCustomerCount() {
		return globalCustomerCount++;
	}
}
