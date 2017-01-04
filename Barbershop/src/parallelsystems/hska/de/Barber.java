package parallelsystems.hska.de;

public class Barber implements Runnable {
	
	private static final int TIME_FOR_HAIRCUT = 4000;
	private static final int TIME_FOR_PAYMENT = 1000;
	private static final int TIME_FOR_SLEEPING = 1000;
	
	private int id;
	private BarberShop shop;
	
	public Barber(int id, BarberShop shop){
		this.id = id;
		this.shop = shop;
	}
	
	@Override
	public void run(){
		try {
			while(true){
				System.out.println(this.toString() + " sleeps.");
				Thread.sleep(TIME_FOR_SLEEPING);
	//			System.out.println(this.toString() + " pulls next customer.");
				Customer customer = this.shop.getNextCustomerForHairCut();
				System.out.println(this.toString() + " cuts hair of customer " + customer.getId() + ".");
				Thread.sleep(TIME_FOR_HAIRCUT);
				System.out.println(this.toString() + " gets payed by customer " + customer.getId() + ".");
				Thread.sleep(TIME_FOR_PAYMENT);
				}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(this.toString() + " goes home ...");
//			e.printStackTrace();
		}		
	}
	
	@Override
	public String toString(){
		return "Barber " + id;
	}
	
}
