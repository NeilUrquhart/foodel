package edu.napier.foodel.problem;



@SuppressWarnings("serial")
public class FoodelVisit extends IndexVisit {

	public FoodelVisit(String name, double lat, double lon, int demand) {
		super(name, lat, lon, demand);
		
	}
	
	private String address;
	private String order;
	private String postCode;
	private String shippingDate;
	private String locality; //Used as well as postcode for EFD
	private String deliveryNote;
	private String allergyDetails;
	private boolean vip;
	private long winstart =-1;//-1 = no time window
	private long winend = -1;
	
	
	public long getWinstart() {
		return winstart;
	}
	public void setWinstart(long winstart) {
		this.winstart = winstart;
	}
	public long getWinend() {
		return winend;
	}
	public void setWinend(long winend) {
		this.winend = winend;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
	public void setName(String name) {
		this.theName = name;
	}
	
	public void setDemand(int demand) {
		super.demand = demand;
	}
	public String getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}


	public String getdeliveryNote() {
		return deliveryNote;
	}
	public void setdeliveryNote(String deliveryNote) {
		this.deliveryNote = deliveryNote;
	}
	public String getAllergyDetails() {
		return allergyDetails;
	}
	public void setAllergyDetails(String allergyDetails) {
		this.allergyDetails = allergyDetails;
	}
	public boolean isVip() {
		return vip;
	}
	public void setVip(boolean vip) {
		this.vip = vip;
		super.theName = "* " + super.theName;
	}
	
	
}
