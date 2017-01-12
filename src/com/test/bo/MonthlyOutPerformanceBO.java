package com.test.bo;

public class MonthlyOutPerformanceBO {
	
	String fundName;
	String date;
	double Excess;
	String OutPerformance;
	double returns;
	int rank;
	
	public MonthlyOutPerformanceBO(String fundName, String date, double excess,
			String outPerformance, double returns, int rank) {
		super();
		this.fundName = fundName;
		this.date = date;
		Excess = excess;
		OutPerformance = outPerformance;
		this.returns = returns;
		this.rank = rank;
	}
	public MonthlyOutPerformanceBO() {
		// TODO Auto-generated constructor stub
	}
	public String getFundName() {
		return fundName;
	}
	public void setFundName(String fundName) {
		this.fundName = fundName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getExcess() {
		return Excess;
	}
	public void setExcess(double excess) {
		Excess = excess;
	}
	public String getOutPerformance() {
		return OutPerformance;
	}
	public void setOutPerformance(String outPerformance) {
		OutPerformance = outPerformance;
	}
	public double getReturns() {
		return returns;
	}
	public void setReturns(double returns) {
		this.returns = returns;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	
	

}
