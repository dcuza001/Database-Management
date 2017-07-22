/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 * Student: Daniel Cuza
 * SID: 861059923
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.text.SimpleDateFormat;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {
   private static int cListID;//ID of contact list
   private static int bListID;//ID of block list
   // reference to physical database connection.
   private Connection _connection = null;
   static String current_user = null;
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   public void setCID(int ID){
      cListID = ID;
   }
   public void setBID(int ID){
      bListID = ID;
   }
   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }
   //function to create statement and then execute query with RS
   public ResultSet executeQueryRS(String query) throws SQLException{
      Statement s = this._connection.createStatement();
      ResultSet r = s.executeQuery(query);
      return r;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   public int getNextSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select nextval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql);current_user = authorisedUser; break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                 boolean chatmenu = false;
                System.out.println("MAIN MENU");
                System.out.println("---------"); 
                System.out.println("1. Chat Menu");
                System.out.println("2. Add to contact list");
                System.out.println("3. Browse contact list");
                System.out.println("4. Delete contact");
                System.out.println("5. Add to block list");
                System.out.println("6. Browse block");
                System.out.println("7. Delete block");
                System.out.println("8. Delete account");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: chatmenu = true; break;
                   case 2: AddToContact(esql); break;
                   case 3: BrowseContacts(esql); break;
                   case 4: DeleteContact(esql); break;
                   case 5: AddToBlocked(esql); break;
                   case 6: BrowseBlocks(esql); break;
                   case 7: DeleteBlocked(esql); break;
                   case 8: usermenu= DeleteMe(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
                  while(chatmenu){
                     System.out.println("CHAT MENU");
                     System.out.println("---------");
                     System.out.println("1. New Chat");
                     System.out.println("2. View All Chats");
                     //System.out.println("1. New Chat/Message");
                     //System.out.println("1. New Chat/Message");
                     System.out.println("9. Back to User Menu");
                     switch(readChoice()){
                        case 1: NewChat(esql); break;
                        case 2: ViewAllChats(esql); break;
                        case 9: chatmenu = false; break;
                        default: System.out.println("Unrecognized choice!"); break;
                     }
                  }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
//Norma
//8c0bb848dc6691e9e8580f1b5eff110880d3
	 //Creating empty contact\block lists for a user
	 esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
	 int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
    //bListID = block_id;
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
	 int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
    //cListID = contact_id;
    
    //cListID = contact_id;


	 String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         //DO FIRST NEXTVAL CALL TO GET CURRVALS
         esql.getNextSeqVal("chat_chat_id_seq");
         esql.getNextSeqVal("message_msg_id_seq");
         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         ResultSet r = esql.executeQueryRS(query);
         while(r.next()){
            cListID = r.getInt("contact_list");
            bListID = r.getInt("block_list");
         }

         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end
  
   public static boolean DeleteMe(Messenger esql){
      try{
         String deleteQ = String.format("SELECT * FROM USR WHERE login='%s' AND login NOT IN (SELECT init_sender FROM CHAT WHERE init_sender='%s')", current_user, current_user);
         if(esql.executeQuery(deleteQ) > 0){
            System.out.println("Permanently delete your account?(y/n): ");
            //String selection = in.readLine();
            if(in.readLine().equals("y")){
               deleteQ = String.format("DELETE FROM USR WHERE login='%s'", current_user);
               esql.executeUpdate(deleteQ);
               System.out.println("Account deleted. Goodbye:)");
               return false;
            }
         }
         else
            System.out.println("Delete all chats you started first. Sorry.");
         return true;
      }catch(Exception e){
         System.err.println(e.getMessage());
         return true;
      }
   }


   public static void AddToContact(Messenger esql){
      try{

      // Your code goes here.
      System.out.println("\tEnter user to add to contacts: ");
      String uname = in.readLine();
      String query = String.format("SELECT * FROM USR WHERE login = '%s'", uname);
      int uExist = esql.executeQuery(query);//do you exist?
      if (uExist > 0){
         System.out.println("Contact list ID: " + cListID);
         String query2 = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES ('%s', '%s')", cListID, uname);
         esql.executeUpdate(query2);
      }
      else
        System.out.println("User "+ uname + " invalid!");
      
      }catch(Exception e){
         System.err.println(e.getMessage() );
      }
   }//end
   public static void DeleteContact(Messenger esql){
      try{

      // Your code goes here.
      System.out.println("\tEnter user to delete from contacts: ");
      String uname = in.readLine();
      String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_member = '%s' AND list_id = '%s'", uname, cListID);
      int uExist = esql.executeQuery(query);//do you exist?
      if (uExist > 0){
         //System.out.println("Contact list ID: " + cListID);
         String query2 = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_member = '%s' AND list_id = '%s'", uname, cListID);
         esql.executeUpdate(query2);
      }
      else
        System.out.println("User "+ uname + " invalid!");
      
      }catch(Exception e){
         System.err.println(e.getMessage() );
      }
   }//end



   public static void BrowseContacts(Messenger esql){
      // Your code goes here.
      try{
         String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id = '%s'", cListID);
         ResultSet rs = esql.executeQueryRS(query);
         while(rs.next()){
            String currUser = rs.getString("list_member");
            String query2 = String.format("SELECT status FROM USR WHERE login='%s'", currUser);
            System.out.println(esql.executeQueryAndReturnResult(query2));
            System.out.println("\t"+rs.getString("list_member"));   
         }

      }
      catch(Exception e){
         System.err.println(e.getMessage() );
      }
   }//end
   //------------------------------------------------------
   //BLOCK STUFF
   //----------------------------

   public static void AddToBlocked(Messenger esql){
      try{

      // Your code goes here.
      System.out.println("\tEnter user to block: ");
      String uname = in.readLine();
      String query = String.format("SELECT * FROM USR WHERE login = '%s'", uname);
      int uExist = esql.executeQuery(query);//do you exist?
      if (uExist > 0){
         System.out.println("Block list ID: " + bListID);
         String query2 = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES ('%s', '%s')", bListID, uname);
         esql.executeUpdate(query2);
      }
      else
        System.out.println("User "+ uname + " invalid!");
      
      }catch(Exception e){
         System.err.println(e.getMessage() );
      }
   }//end
   public static void DeleteBlocked(Messenger esql){
      try{

      // Your code goes here.
      System.out.println("\tEnter user to unblock: ");
      String uname = in.readLine();
      String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_member = '%s' AND list_id = '%s'", uname, bListID);
      int uExist = esql.executeQuery(query);//do you exist?
      if (uExist > 0){
         //System.out.println("Contact list ID: " + cListID);
         String query2 = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_member = '%s' AND list_id = '%s'", uname, bListID);
         esql.executeUpdate(query2);
      }
      else
        System.out.println("User "+ uname + " invalid!");
      
      }catch(Exception e){
         System.err.println(e.getMessage() );
      }
   }//end



   public static void BrowseBlocks(Messenger esql){
      // Your code goes here.
      try{
         System.out.println("\tBrowsing Blocked Users: \n");
         String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id = '%s'", bListID);
         ResultSet rs = esql.executeQueryRS(query);
         while(rs.next()){
            System.out.println("\t"+rs.getString("list_member"));
         }

      }
      catch(Exception e){
         System.err.println(e.getMessage() );
      }
   }//end
//--------------------------------------------------
//----MESSAGE AND CHAT-----------------------------

   public static void NewMessage(Messenger esql, int chatID){
      // Your code goes here.
      try{
         System.out.println("-----------------------");
         System.out.println("-------New Mesage------");
         System.out.println("-----------------------\n");
         //Enter the messaage body----------------
         String body = null;
         System.out.println("\t\tInsert message body: ");
         body = in.readLine();
//-------------------------CHANGE!!!!!!!!!!!!!!
         
         Timestamp t = new Timestamp(0);
         t.setTime(0);
	      int msgID = esql.getNextSeqVal("message_msg_id_seq");
         //String msgID = esql.executeQueryAndReturnResult("SELECT CURRVAL('message_msg_id_seq');").get(0).get(0);
         String msgQuery = String.format("INSERT INTO MESSAGE(msg_id, msg_text, msg_timestamp, sender_login, chat_id) VALUES ('%s', '%s', '%s', '%s', '%s')", msgID, body, t, current_user, chatID);
	      esql.executeUpdate(msgQuery); 

 //---------------------------CHANGE!!!!!!!!!!!!!!!!!
            
         System.out.println("\n\n----SENT MESSAGE----\n\n");
         
      }catch(Exception e){//
         System.err.println(e.getMessage() );
      }
   }//end 
   //-------------
   //to check if a user exists in db
   public static void NewChat(Messenger esql){
      try{
         System.out.println("------------------------ ");
         System.out.println("---------NEW CHAT------- ");
         System.out.println("------------------------ ");
         //Private chat(1) or group(2)?
         //Chat(chat_id,chat_type, init_sender )!!!!!!!!!!!!!
         //get chat_id from sequence right here
         //
         int chatID = esql.getCurrSeqVal("chat_chat_id_seq");
         
         System.out.println("Chat id: "+ chatID);
         //pass all three into Private or Group function
         System.out.println("\tPrivate(1) or group(2) chat: ");
         boolean addSuccess = false;
         String ctype = null;
         String priv = "private";
         String grp = "group";
         switch(readChoice()){
            case 1: initChat(esql, current_user, chatID, priv); addChat1(esql, current_user, chatID, priv) ; addSuccess = true; ctype = "private"; break;
            case 2: ; initChat(esql, current_user, chatID, grp);  addChat1(esql, current_user, chatID, priv); addSuccess = true; ctype = "group"; break;
            default : System.out.println("Unrecognized choice!");addSuccess = false; break; 
         }
         if(addSuccess){ 
            //----------------------------
            //Add current user to chat
            addToChat(esql, chatID, current_user);
            //"CHAT SUCCESSFULLY STARTED!! :)"
         }
         //else
            //error chat failed
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   public static void initChat(Messenger esql, String currLogin, int chatID, String ctype ){
      try{
            String makeChat = String.format("INSERT INTO CHAT (chat_id, chat_type, init_sender) VALUES ('%s','%s','%s');", chatID, ctype, current_user);
            esql.executeUpdate(makeChat);            

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }

   }


   public static void addChat1(Messenger esql, String currLogin, int chatID, String ctype ){
      try{
         System.out.println("\tEnter user to add to chat: ");
         String uname = in.readLine();
         //-------CHANGE!!!!!!!!!!!!!!!!vvvvvvvv
         String query = String.format("SELECT * FROM USR WHERE login = '%s'", uname);
         int uExist = esql.executeQuery(query);//do you exist?
         if (uExist > 0){//First chec if this is a valid user 
            //Add Friend to Private Chat
            addToChat(esql, chatID, uname);            
         }//----------------DOUBLE CHECK!!!!!^^^^^^^
         else
            System.out.println("\tUser "+ uname + " invalid!");

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }

   }
   //simple add new user to a chat
   public static void addToChat(Messenger esql, int chatID, String uname){
      try{
         String addThem = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES ('%s', '%s')", chatID, uname);
         esql.executeUpdate(addThem);
         System.out.println("\tSuccessfully added "+ uname + " to chat!:)\n");

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }

   }
   public static void removeFromChat(Messenger esql, int chatID){
      try{
         //check if curr user initialized this chat
         String check = String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id='%s'", current_user, chatID);
         if(esql.executeQuery(check) > 0){
            System.out.println("\tEnter the user to remove from chat: ");
            String removeMe = in.readLine();
            check = String.format("SELECT * FROM USR WHERE login = '%s'", removeMe);
            if(esql.executeQuery(check)> 0){
               check = String.format("DELETE FROM CHAT_LIST WHERE chat_id= '%s' AND member= '%s'", chatID, removeMe);
               esql.executeUpdate(check);
               System.out.println("The user "+ removeMe + " was removed from chat:)");
            }
            else
               System.out.println("User is not a member!");
         }
         else
            System.out.println("Must be initial sender to remove members!:(");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void ChatDelete(Messenger esql, int chatID){
      try{
         //make sure i'm init sender
         String deleteQ = String.format("SELECT * FROM CHAT WHERE init_sender='%s' AND chat_id= '%s'", current_user, chatID);
         if(esql.executeQuery(deleteQ)> 0){
            System.out.println("Confirm delete(y/n): ");
            //String confirm = in.readLine();
            if(in.readLine().equals("y")){
               deleteQ = String.format("DELETE FROM MESSAGE WHERE chat_id='%s'", chatID);
               esql.executeUpdate(deleteQ);
               deleteQ = String.format("DELETE FROM CHAT_LIST WHERE chat_id='%s'", chatID);
               esql.executeUpdate(deleteQ);
               deleteQ = String.format("DELETE FROM CHAT WHERE chat_id='%s'", chatID);
               esql.executeUpdate(deleteQ);
               System.out.println("Chat Successfully Deleted!:)");
            }
            else
               System.out.println("DID NOT DELETE");
         }
         else
            System.out.println("You have to be initial sender of chat in order to delete it! :(");
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   
   public static void DeleteMessage(Messenger esql){
      try{
         //make sure i'm init sender
         System.out.println("Enter MsgID to delete: ");
         int msgID = readChoice();
         String deleteM = String.format("SELECT * FROM MESSAGE WHERE sender_login='%s' AND msg_id= '%s'", current_user, msgID);
         if(esql.executeQuery(deleteM)> 0){
            System.out.println("Confirm delete(y/n): ");
            //String confirm = in.readLine();
            if(in.readLine().equals("y")){
               deleteM = String.format("DELETE FROM MESSAGE WHERE sender_login='%s' AND msg_id= '%s'", current_user, msgID);
               esql.executeUpdate(deleteM);
               
               System.out.println("Message Successfully Deleted!:)");
            }
            else
               System.out.println("DID NOT DELETE MESSAGE!");
         }
         else
            System.out.println("You have to be initial sender of message in order to delete it! :(");
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }


//-----------------------------------------------
//View Messages in a chat and give chat options as well
//----------------------------------------------
   public static void ViewMessages(Messenger esql, int chatID, String ctype){
      try{
         int selection = 10;
         int msgViewSize = 0;//for displays of 10
         while(selection != 9){
            //add more display stuff here
            System.out.println("---------------------------");
            System.out.println("----------MESSAGES---------");
            System.out.println("---------------------------");
            System.out.println("Ctype: "+ctype+"!");
            //-----------------
            //EDIT QUERY TO FILTER OUT BLOCKED??
            String query = String.format("SELECT sender_login, msg_timestamp, msg_text, msg_id FROM MESSAGE WHERE chat_id= '%s' ORDER BY msg_timestamp DESC LIMIT 10 OFFSET '%s';", chatID, msgViewSize);
            for(List<String> msg : esql.executeQueryAndReturnResult(query)){
               System.out.println("MsgID: "+ msg.get(3).trim());
               System.out.println("User: "+ msg.get(0).trim());
               System.out.println("Time: "+ msg.get(1).trim());
               System.out.println("Message: "+ msg.get(2).trim());
               System.out.println("---------------------------------");
               System.out.println("---------------------------------");
               //-----------
            }
            //TODO: ADD OPTIONS DOWN HERE!!!!vvvvvvv
            System.out.println("1. Write new message ");
            System.out.println("2. Previous 10 messsages ");
            System.out.println("3. Delete a message");
            System.out.println("-------If Initial Sender-------");
            
            String grp = "group";
            if(ctype.equals(grp)) 
               System.out.println("4. Add user to chat ");
            System.out.println("5. Remove user from chat ");
            System.out.println("6. Delete chat ");
            System.out.println("9. Return to chat menu");
            if(ctype.equals("group")){
               switch(readChoice()){
                  case 1: NewMessage(esql, chatID); break;
                  case 2: msgViewSize += 10; break;
                  case 3: DeleteMessage(esql); break;
                  case 4: addChat1(esql, current_user, chatID, grp); break;
                  case 5: removeFromChat(esql, chatID); break;
                  case 6: ChatDelete(esql, chatID); selection = 9; break;
                  case 9: selection = 9; break;
                  default: break;
               }
            }
            else{
               switch(readChoice()){
                  case 1: NewMessage(esql, chatID); break;
                  case 2: msgViewSize += 10; break;
                  case 3: DeleteMessage(esql); break;
                  //case 4: addChat1(esql, current_user, chatID, grp); break;
                  case 5: removeFromChat(esql, chatID); break;
                  case 6: ChatDelete(esql, chatID); selection = 9; break;
                  case 9: selection = 9; break;
                  default: break;
               }
            }
         }

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
//------------------------------------
//---------VIEW ALL CHATS-------------
//-----------------------------------
   public static void ViewAllChats(Messenger esql){
      try{
         int selection = 1;
         while(selection != 0){
            int numChat = 0;
            List<Integer> CIDs = getAllChatIDs(esql);
            //-------DISPLAY CHAT BROWSER UI
            System.out.println("---------------------------");
            System.out.println("--------CHAT BROWSER-------");
            System.out.println("---------------------------");
            System.out.println("You have "+ CIDs.size()+" active chats!");
            System.out.println("===========================");
            
            //String ctype = "group";
            for(Integer cid : CIDs){
               numChat++;
               String ctypeQ = String.format("SELECT chat_type FROM CHAT WHERE chat_id='%s'", cid);
               String ctype = (esql.executeQueryAndReturnResult(ctypeQ)).get(0).get(0);
               
               System.out.println(numChat +". Chat: "+ cid);
               System.out.println("Chat type: "+ ctype);
               System.out.println("Users in chat:\n ");
               for(String aUser : getChatUsersByChatID(esql, cid)){
                  System.out.print(aUser.trim()+", ");
               }
               //Show initial sender?

               //Show a message?
               
               System.out.println("-------------------------");
            }
            System.out.println("Enter number to select chat(0 return to Chat Menu): ");
            selection = readChoice();
            String ctypeQ = String.format("SELECT chat_type FROM CHAT WHERE chat_id='%s'", CIDs.get(selection-1));
            String ctype = (esql.executeQueryAndReturnResult(ctypeQ)).get(0).get(0).trim(); 
            if(selection <= CIDs.size() && selection > 0){
               ViewMessages(esql, CIDs.get(selection-1), ctype);
            }
            else if(selection != 0){
               System.out.println("Invalid chat selection");
            }
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   public static List<Integer> getAllChatIDs(Messenger esql){
      try{
         List<Integer> chats = new ArrayList<Integer>();
         String query = String.format("SELECT chat_id FROM chat_list WHERE member= '%s';", current_user);
         for(List<String> aChat : esql.executeQueryAndReturnResult(query)){
            chats.add(Integer.parseInt(aChat.get(0)));
         }
      return chats;
      }catch(Exception e){
         System.err.println (e.getMessage ()); return null;
      }
   }

   public static List<String> getChatUsersByChatID(Messenger esql, int chatID){
      try{
         List<String> users = new ArrayList<String>();
         String query = String.format("SELECT member FROM chat_list WHERE chat_id= '%s';", chatID);
         for(List<String> chatUser : esql.executeQueryAndReturnResult(query)){
            users.add(chatUser.get(0));
         }
         return users;
      }catch(Exception e){
         System.err.println(e.getMessage()); return null;
      }
   }

//-------------------------------------------------
//-------------------------------------------------------------
   public static boolean userExists(Messenger esql, String userid){
      try{
         String query = String.format("SELECT * FROM USR WHERE USR.userId='%s'", userid);
         List<List<String> > i = esql.executeQueryAndReturnResult(query);
         if(i.isEmpty()){
            System.out.println("ERR: USER NOT FOUND");
            return false;
         }
         return true;
      }
      catch(Exception e){
         System.out.println("userExists() exception");
      }
      return true;
   }

   public static void Query6(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end Query6

}//end Messenger
