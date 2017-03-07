package database;

import java.util.concurrent.ConcurrentHashMap;

import protocols.ProtocolCallback;

public class Room {
	
	/**
	 * Initialize the room for a specific game 
	 */
	private String roomName="";
	private boolean gameIsOnAtRoom = false;
	private Game gameInRoom = null;	
	private ConcurrentHashMap<String ,ProtocolCallback<String>> PeopleInRoom = new ConcurrentHashMap<String ,ProtocolCallback<String>>();	
	private ConcurrentHashMap<String ,String> incomingAnswer = new ConcurrentHashMap<String ,String>();	
	private ConcurrentHashMap<String ,String> incomingChoice = new ConcurrentHashMap<String ,String>();	
	private int acceptIntForSelect=0;
	
	public Room(String roomName){
		this.roomName=roomName;
	}
	
	public synchronized void startGame(String game){
		if (!gameIsOnAtRoom){
			gameIsOnAtRoom=true;
			gameInRoom=DatabaseSingelton.getInstance().getGame(game);	
			gameInRoom.setGame(this);
		}
	}
	
	public void addTxt(String name, String txt){
		incomingAnswer.put(name, txt);
		gameInRoom.checkStatus();
	}
	
	public void addChoise(String name, String choise){
		incomingChoice.put(name, choise);
		gameInRoom.checkStatus();
	}
	
	public String getRoomName(){
		return roomName;
	}

	public boolean getGameIsOnAtRoom(){
		return gameIsOnAtRoom;
	}
	
	public Game getGameInRoom(){
		return gameInRoom;
	}

	public ConcurrentHashMap<String, ProtocolCallback<String>> getPeopleInRoom(){
		return PeopleInRoom;
	}
	
	public void setGameIsOnAtRoom(boolean gameIsOnAtRoom){
		this.gameIsOnAtRoom=gameIsOnAtRoom;
	}

	public void setGameInRoom(Game gameInRoom){
		this.gameInRoom=gameInRoom;
	}

	public ConcurrentHashMap<String, String> getIncomingAnswer() {
		return incomingAnswer;
	}

	public void setIncomingAnswer(ConcurrentHashMap<String, String> incomingAnswer) {
		this.incomingAnswer = incomingAnswer;
	}

	public int getAccepteIntForSelect() {
		return acceptIntForSelect;
	}

	public void setAccepteIntForSelect(int accepteIntForSelect) {
		this.acceptIntForSelect = accepteIntForSelect;
	}

	public ConcurrentHashMap<String, String> getIncomingChoice() {
		return incomingChoice;
	}

	public void setIncomingChoice(ConcurrentHashMap<String, String> incomingChoice) {
		this.incomingChoice = incomingChoice;
	}

}
