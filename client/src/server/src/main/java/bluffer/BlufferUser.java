package bluffer;

import protocols.ProtocolCallback;

public class BlufferUser {
	/**
	 * a player in the Bluffer game
	 */
	private String name="";
	private int numOfAns=-1;
	private boolean currectAtThisAns=false;
	private ProtocolCallback<String> callback;
	private int score=0;
	private int totalScore=0;
	private String bluffAns="";
	
	/**
	 * @param callback the way to send a msg to the user
	 * @param name the name of the user
	 */
	public BlufferUser(ProtocolCallback<String> callback, String name){
		this.callback=callback;
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public int getnumOfAns(){
		return numOfAns;
	}

	public boolean getCurrectAtThisAns(){
		return currectAtThisAns;
	}

	public ProtocolCallback<String> getCallback(){
		return callback;
	}

	public int getScore(){
		return score;
	}
	
	public int getTotalScore(){
		return totalScore;
	}

	public String getBluffAns(){
		return bluffAns;
	}

	public void setBluffAns(String bluffAns){
		this.bluffAns=bluffAns;
	}
	
	public void setNumOfAns(int UserId){
		this.numOfAns=UserId;
	}

	public void setTotalScore(int totalScore){
		this.totalScore=totalScore;
	}
	
	public void setScore(int score){
		this.score=score;
	}
	
	public void setCurrectAtThisAns(boolean currectAtThisAns){
		this.currectAtThisAns=currectAtThisAns;
	}

}
