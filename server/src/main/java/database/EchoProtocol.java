package database;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import protocols.AsyncServerProtocol;
import protocols.ProtocolCallback;

public class EchoProtocol implements AsyncServerProtocol<String> {
	
	private String name = "";
	private Room room = new Room("temp");
	private boolean loggedIN = false;
	private boolean loggedINRoom = false;
	private boolean askedForText = true;
	private boolean askedForSelect = false;
	private boolean _shouldClose = false;
	private boolean _connectionTerminated = false;
	
	@Override
	public void processMessage(String msg, ProtocolCallback<String> callback) {
		if (!this._connectionTerminated){
			if (this.isEnd(msg)) {
				if(!room.getGameIsOnAtRoom()){
					try {
						callback.sendMessage("<-SYSMSG QUIT ACCEPTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
					DatabaseSingelton.getInstance().getListOfPeople().remove(name);
					this._shouldClose = true;
				}else{
					try {
						callback.sendMessage("<-SYSMSG QUIT REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			}
		
			
			else if (msg.contains("NICK")){
				//check if there is another name like this
				String [] name = msg.split(" ");
				if(name.length>1 && !DatabaseSingelton.getInstance().getListOfPeople().contains(name[1]) && !loggedIN){
					DatabaseSingelton.getInstance().getListOfPeople().add(name[1]);
					this.name=name[1];
					loggedIN=true;
					try {
						callback.sendMessage("<-SYSMSG NICK ACCEPTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						callback.sendMessage("<-SYSMSG NICK REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			
			
			
			else if (msg.contains("JOIN")){
				String [] room = msg.split(" ");
				if(this.room.getGameIsOnAtRoom() || !loggedIN || room.length<2 || room[1].equals(this.room.getRoomName())){
					try {
						callback.sendMessage("<-SYSMSG JOIN REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					if (!DatabaseSingelton.getInstance().getMapOfRooms().containsKey(room[1])){
						Room curr = new Room (room[1]);
						curr.getPeopleInRoom().put(name, callback);
						DatabaseSingelton.getInstance().getMapOfRooms().put(room[1], curr);
						this.room.getPeopleInRoom().remove(name);
						this.room=curr;
						loggedINRoom=true;
						try {
							callback.sendMessage("<-SYSMSG JOIN ACCEPTED");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						Room curr = DatabaseSingelton.getInstance().getMapOfRooms().get(room[1]);
						if (curr.getGameIsOnAtRoom()){
							try {
								callback.sendMessage("<-SYSMSG JOIN REJECTED");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else{
							this.room.getPeopleInRoom().remove(name);
							curr.getPeopleInRoom().put(name, callback);
							this.room=curr;
							loggedINRoom=true;
							try {
								callback.sendMessage("<-SYSMSG JOIN ACCEPTED");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}	
			}
			
			
			
			
			else if (msg.contains("MSG")){
				if (!loggedINRoom || !loggedIN || msg.length()<4){
					try {
						callback.sendMessage("<-SYSMSG MSG REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					String message = msg.substring(4);		
					Set<String> keys = room.getPeopleInRoom().keySet();
					Iterator<String> itr = keys.iterator();
				    String str;
					    while (itr.hasNext()) { 
					       str = itr.next();
					       if(str!=name){
								try {
									room.getPeopleInRoom().get(str).sendMessage("<-USRMSG "+name + ": " + message);
								} catch (IOException e) {
									e.printStackTrace();
								}
					       }
					    } 
					try {
						callback.sendMessage("<-SYSMSG MSG ACCEPTED");
					} catch (IOException e) {
						e.printStackTrace();
					}

					}
				}
		
			else if (msg.contains("LISTGAMES") && msg.length()==9){
				if(loggedIN){
					String ans = "";
					for(int i=0; i<DatabaseSingelton.getInstance().getListOfGames().size() ; i++){
						ans = ans+ DatabaseSingelton.getInstance().getListOfGames().get(i) + " ";
					}
					try {
						callback.sendMessage("<-SYSMSG LISTGAMES ACCEPTED " + ans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						callback.sendMessage("<-SYSMSG LISTGAMES REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
			
			else if (msg.contains("STARTGAME")){
				String [] game = msg.split(" ");
				if ( game.length<2 || !DatabaseSingelton.getInstance().getListOfGames().contains(game[1])|| !loggedIN ||!loggedINRoom ||room.getGameIsOnAtRoom()){
					try {
						callback.sendMessage("<-SYSMSG STARTGAME REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						callback.sendMessage("<-SYSMSG STARTGAME ACCEPTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
					room.startGame(game[1]);

				}
			}
			
			else if (msg.contains("TXTRESP")){
				String [] text = msg.split(" ");
				if (!askedForText || !loggedIN || !room.getGameIsOnAtRoom() || text.length<2){
					try {
						callback.sendMessage("<-SYSMSG TXTRESP REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						callback.sendMessage("<-SYSMSG TXTRESP ACCEPTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
					room.addTxt(name, text[1]);
					askedForSelect=true;
					askedForText=false;
				}

			}
			
			else if (msg.contains("SELECTRESP")){
				String [] select = msg.split(" ");
				if (select.length<2 || !askedForSelect || !select[1].matches("^-?\\d+$")|| Integer.parseInt(select[1])>room.getGameInRoom().getAcceptIntForSelect() || Integer.parseInt(select[1])<0 || !loggedIN || !room.getGameIsOnAtRoom()){
					try {
						callback.sendMessage("<-SYSMSG SELECTRESP REJECTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						callback.sendMessage("<-SYSMSG SELECTRESP ACCEPTED");
					} catch (IOException e) {
						e.printStackTrace();
					}
					room.addChoise(name, select[1]);
					askedForSelect=false;
					askedForText=true;
				}
			}
			
			else{
				try {
					callback.sendMessage("<-SYSMSG UNIDENTIFIED");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public boolean isEnd(String msg) {
		return msg.equals("QUIT");
	}
	@Override
	public boolean shouldClose() {
		return this._shouldClose;
	}
	@Override
	public void connectionTerminated() {
		this._connectionTerminated = true;		
		
	}	    
   

}
