package bluffer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import database.Game;
import database.Room;

public class Bluffer implements Game {
	/**
	 * The Bluffer Game
	 */
	  private ArrayList<Question> listOfQuestions = new ArrayList<Question>();
	  private ArrayList<BlufferUser> usersList = new ArrayList<BlufferUser>() ;
	  //connects between the names and the bluffer users
	  private ConcurrentHashMap<String ,BlufferUser> usersHash = new ConcurrentHashMap<String ,BlufferUser>() ;
	  //connect between the number of the answer to the user
	  private ConcurrentHashMap<Integer ,BlufferUser> intToUser = new ConcurrentHashMap<Integer ,BlufferUser>() ;
	  private Room room ;
	  private Question curr=null;
	  private int realAns = -1;
	  private int size=0;
	  private int AccepteIntForSelect=0;

	  @Override
	  public void setGame(Room room){			
		  this.room=room;
		  this.size=room.getPeopleInRoom().size();
		  Set<String> keys = room.getPeopleInRoom().keySet();
		  Iterator<String> itr = keys.iterator();
		  String str;
		  	while (itr.hasNext()) { 
		      str = itr.next();
		      BlufferUser curr = new BlufferUser(room.getPeopleInRoom().get(str), str);
		      usersList.add(curr);
		      usersHash.put(str, curr);
			}
		  loadQuestions();
		  sendQuestion();
		}    
	  
	  
	  /**
	   * in this function we are reading the JSON file as an input
	   * and loading 3 random questions
	   */
	  public void loadQuestions (){
		   	ArrayList<Question> tempQuestions = new ArrayList<Question>();
	        JSONParser parser = new JSONParser();
			try {
	            Object obj = parser.parse(new FileReader("bluffer.json"));
	            JSONObject jsonObject = (JSONObject) obj;
	            JSONArray questions = (JSONArray) jsonObject.get("questions");
	            
	            for (int i = 0 ; i<questions.size() ; i++ ){
	            	  String curr = questions.get(i).toString();
	            	  int start_questionText=curr.indexOf("questionText")+15;
	                  int finish_questionText = curr.indexOf("}")-1;
	                  String questionText = curr.substring(start_questionText, finish_questionText);
	                  int start_realAnswer = curr.indexOf("realAnswer")+13;
	                  int finish_realAnswer = curr.indexOf("questionText")-3;
	                  String realAnswer = curr.substring(start_realAnswer, finish_realAnswer);
	                  tempQuestions.add(new Question( questionText, realAnswer));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
			  for (int j=0 ; j<3 ; j++){
				  int rand = (int) Math.floor((Math.random() * tempQuestions.size()));
				  Question curr= tempQuestions.remove(rand);
				  listOfQuestions.add(curr);
			  }			
	  }
	  
	  	/**
	  	 * sending the same question to all the users
	  	 * sending the total score of the game when there is no questions left
	  	 */
		private void sendQuestion() {
			if (listOfQuestions.size()==0){
				  String totalScoreString = "GAMEMSG Summary:";
				  for (int j=0 ; j<usersList.size() ; j++){
					  totalScoreString=totalScoreString+ " " + usersList.get(j).getName() + ": " + usersList.get(j).getTotalScore() + "pts,";
				  }
				  totalScoreString=totalScoreString.substring(0, totalScoreString.length()-1);
				  for (int j=0 ; j<usersList.size() ; j++){
					  try {
						usersList.get(j).getCallback().sendMessage("<-"+totalScoreString);
					} catch (IOException e) {
						e.printStackTrace();
					}
				  }
					room.setGameInRoom(null);
					room.setGameIsOnAtRoom(false);				
				return;
			}
			
			  for (int j=0 ; j<size ; j++){
				  try {
					  usersList.get(j).getCallback().sendMessage("<-ASKTXT "+ listOfQuestions.get(0).getQuestionText());
				  } catch (IOException e) {
					  e.printStackTrace();
				  }
			  }	
			  curr=listOfQuestions.remove(0);
		}
	  
		@Override
	  public void checkStatus() {
		
		//the response after all the users put answers
		  if (room.getIncomingAnswer().size()==size){
			  ArrayList<BlufferUser> usersAns = new ArrayList<BlufferUser>();
			  Set<String> keys = room.getIncomingAnswer().keySet();
			  Iterator<String> itr = keys.iterator();
			  String name;
			  	while (itr.hasNext()) { 
			      name = itr.next();
			      String ans= room.getIncomingAnswer().get(name);
			      //checking if the answer of the user is the real answer and if not updating the list of the possible answers
			      if (!ans.toLowerCase().equals(curr.getRealAnswer().toLowerCase())){
						usersHash.get(name).setBluffAns(ans);
						usersAns.add(usersHash.get(name));
			      }
			      room.getIncomingAnswer().remove(name);
			    }
				  BlufferUser temp = new BlufferUser(null, "realAns");
				  temp.setBluffAns(curr.getRealAnswer());
				  usersAns.add(temp);
				  Collections.shuffle(usersAns);
				  				  
				  //sending possible answers to all the users
				  String toSend = "";
				  for (int j=0 ; j<usersAns.size() ; j++){
					  toSend= toSend+j+"."+(usersAns.get(j).getBluffAns()).toLowerCase()+" ";
					  usersAns.get(j).setNumOfAns(j);
					  intToUser.put(j, usersAns.get(j));
					  if (usersAns.get(j).getBluffAns().equals(curr.getRealAnswer())){
						  realAns=j;
					  }
				  }
				  AccepteIntForSelect = (usersAns.size()-1);
				  for (int j=0 ; j<usersList.size() ; j++){
					  try {
						  usersList.get(j).getCallback().sendMessage("<-ASKCHOICES " +toSend);
					  } catch (IOException e) {
						  e.printStackTrace();
					  }
				  }
				  return;
			  }
		  
		  //giving scores to the users after finish selecting the number
		  if (room.getIncomingChoice().size()==size){
			  //get answers and update score
			  Set<String> keys = room.getIncomingChoice().keySet();
			  Iterator<String> itr = keys.iterator();
			  String name;
			  	while (itr.hasNext()) { 
			      name = itr.next();
			      String ans= room.getIncomingChoice().get(name);
				  if (Integer.parseInt(ans)==realAns){
					  usersHash.get(name).setScore(usersHash.get(name).getScore()+10);
					  usersHash.get(name).setCurrectAtThisAns(true);
				  }else{
					  intToUser.get(Integer.parseInt(ans)).setScore(intToUser.get(Integer.parseInt(ans)).getScore() +5 );
				  }
			      room.getIncomingChoice().remove(name);
			    }
			  	
				  // sending callbacks to all the users
				  for (int j=0 ; j<usersList.size() ; j++){
					  try {
						  usersList.get(j).getCallback().sendMessage("<-GAMEMSG The correct answer is: "+curr.getRealAnswer());
					} catch (IOException e) {
						e.printStackTrace();
					}
					  
					  //giving the user his score
					  int currScore = usersList.get(j).getScore();
					  String currScoreString ;
					  if (currScore>=0){
						  currScoreString="+"+currScore+"pts";
					  }else{
						  currScoreString="-"+currScore+"pts"; 
					  }
					  if (usersList.get(j).getCurrectAtThisAns()){
						  try {
							  usersList.get(j).getCallback().sendMessage("<-GAMEMSG correct! "+currScoreString);
						} catch (IOException e) {
							e.printStackTrace();
						}
					  }else{
						  try {
							  usersList.get(j).getCallback().sendMessage("<-GAMEMSG wrong! "+currScoreString);
						} catch (IOException e) {
							e.printStackTrace();
						}
					  }
				  }
				  
				  //update total score
				  for (int j=0 ; j<usersList.size() ; j++){
					  usersList.get(j).setTotalScore(usersList.get(j).getTotalScore() + usersList.get(j).getScore());
					  usersList.get(j).setScore(0);
				  }
				  intToUser.clear();
				  room.getIncomingChoice().clear();
				  sendQuestion();
				  return;
			  }

	  }


	@Override
	public int getAcceptIntForSelect() {
		return AccepteIntForSelect;
	}


}
