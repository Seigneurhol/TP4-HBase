/**
 * Created by Seigneurhol on 21/10/2016.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Strings;

public class HBaseTable {

    private Table table;
    private Configuration config;
    private Connection connection;

    /**
     * HBaseTable Constructeur
     * @throws IOException
     */
    public HBaseTable() throws IOException {
        config = HBaseConfiguration.create();

        connection = ConnectionFactory.createConnection(config);
        table = connection.getTable(TableName.valueOf("HBaseTablePierreBressand"));
    }

    /**
     * Create a person on the table HBaseTablePierreBressand on the column family People
     * @throws IOException
     */
    public void createPeople() throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean continuer = true;
        System.out.println("Please write your name :");
        String name = sc.nextLine();
        System.out.println("Please write your firstName :");
        String firstName = sc.nextLine();
        System.out.println("Please write your gender (m or f) :");
        String gender = sc.nextLine();
        System.out.println("Please write your BFF name:");
        String BFF = sc.nextLine();
        ArrayList<String> friends = new ArrayList<String>();
        while(continuer) {
            System.out.println("Please write your friend name :");
            friends.add(sc.nextLine());
            System.out.println("Would you like to add more friend ? (Y or N) :");
            String choice = sc.nextLine();
            if (Objects.equals(choice, "N") || Objects.equals(choice, "n")) {
                continuer = false;
            }
        }

        //Put p = new Put(Bytes.toBytes("myLittleRow"));
        Put p = new Put(Bytes.toBytes(name));

        //p.addColumn(Bytes.toBytes("myLittleFamily"), Bytes.toBytes("someQualifier"), Bytes.toBytes("Some Value"));
        p.addColumn(Bytes.toBytes("People"), Bytes.toBytes("firstName"), Bytes.toBytes(firstName));
        p.addColumn(Bytes.toBytes("People"), Bytes.toBytes("gender"), Bytes.toBytes(gender));
        p.addColumn(Bytes.toBytes("People"), Bytes.toBytes("BFF"), Bytes.toBytes(BFF));
        String fullFriendName = "";
        for (String temp : friends) {
            fullFriendName = fullFriendName + temp + ", ";
        }
        p.addColumn(Bytes.toBytes("People"), Bytes.toBytes("friends"), Bytes.toBytes(fullFriendName));

        //table.put(p);
        table.put(p);
    }

    /**
     * Search a person by his name
     * @param name
     * @return String
     * @throws IOException
     */
    public String searchForAPerson(String name) throws IOException {
         /*Get g = new Get(Bytes.toBytes("myLittleRow"));
        Result r = table.get(g);
        byte[] value = r.getValue(Bytes.toBytes("myLittleFamily"), Bytes.toBytes("someQualifier"));*/

        Get g = new Get(Bytes.toBytes(name));
        Result r = table.get(g);
        byte[] valueFirstName = r.getValue(Bytes.toBytes("People"), Bytes.toBytes("firstName"));
        byte[] valueGender = r.getValue(Bytes.toBytes("People"), Bytes.toBytes("gender"));
        byte[] valueBFF = r.getValue(Bytes.toBytes("People"), Bytes.toBytes("BFF"));
        byte[] valueFriends = r.getValue(Bytes.toBytes("People"), Bytes.toBytes("friends"));

        /*String valueStr = Bytes.toString(value);
        System.out.println("GET: " + valueStr);*/

        String valueStrFirstName = Bytes.toString(valueFirstName);
        String valueStrGender = Bytes.toString(valueGender);
        String valueStrBFF = Bytes.toString(valueBFF);
        String valueStrFriends = Bytes.toString(valueFriends);
        System.out.println("Informations about the friend " + name + " are : first name : " + valueStrFirstName + ", gender : " + valueStrGender + ", BFF name : " + valueStrBFF + " and friends name : " + valueStrFriends);

        return valueStrFriends;
    }

    /**
     * Search a person by gender
     * @throws IOException
     */
    public void searchForAPersonByGender() throws IOException {
        /*Scan s = new Scan();
        s.addColumn(Bytes.toBytes("myLittleFamily"), Bytes.toBytes("someQualifier"));
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
                System.out.println("Found row: " + rr);
            }
        } finally {
            scanner.close();
        }*/

        Scan s = new Scan();
        s.addColumn(Bytes.toBytes("People"), Bytes.toBytes("gender"));
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
                System.out.println("People found : " + rr);
            }
        } finally {
            scanner.close();
        }
    }

    /**
     * Add a friend into the friend field of a person
     */
    public void addFriendToAPerson() {
        boolean continuer = true;
        String name;
        Scanner sc = new Scanner(System.in);
        String fullFriendName = "";
        ArrayList<String> friends = new ArrayList<String>();

        System.out.println("Please write the name of the person to add him/her friends :");
        name  = sc.nextLine();

        Put p = new Put(Bytes.toBytes(name));

        while(continuer) {
            System.out.println("Please write the friend name to add :");
            friends.add(sc.nextLine());
            System.out.println("Would you like to add more friend ? (Y or N) :");
            String choice = sc.nextLine();
            if (Objects.equals(choice, "N") || Objects.equals(choice, "n")) {
                continuer = false;
            }
        }
        for (String temp : friends) {
            fullFriendName = fullFriendName + temp + ", ";
        }
        p.addColumn(Bytes.toBytes("People"), Bytes.toBytes("friends"), Bytes.toBytes(fullFriendName));
    }

    /**
     * Delete a friend to a person
     * @param name
     * @throws IOException
     */
    public void deleteAFriendToAPerson(String name) throws IOException {
        Scanner sc = new Scanner(System.in);

        String friendStr = searchForAPerson(name);

        System.out.println("Please write the name of the friend you what to remove :");
        name  = sc.nextLine();

        friendStr = friendStr.replace(name, "");

        Put p = new Put(Bytes.toBytes(name));
        p.addColumn(Bytes.toBytes("People"), Bytes.toBytes("friends"), Bytes.toBytes(friendStr));
    }

    /**
     * Delete a person from the HBase Table
     * @throws IOException
     */
    public void deleteAPerson() throws IOException {
        String name;
        Scanner sc = new Scanner(System.in);

        System.out.println("Please write the name of the person to delete :");
        name  = sc.nextLine();

        List<Delete> list = new ArrayList<Delete>();
        Delete del = new Delete(name.getBytes());
        list.add(del);
        table.delete(list);
        System.out.println(name + " was deleted !");
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        HBaseTable HBaseTableObj = new HBaseTable();
        String name;

        boolean choiceDone = true;
        while(choiceDone) {
            System.out.println("Welcome to HBase Social Network !");
            System.out.println("Make a choice :");
            System.out.println("1. Create an account");
            System.out.println("2. Search for a person by name");
            System.out.println("3. Search for a person by gender");
            System.out.println("4. Add friend to a person");
            System.out.println("5. Remove a friend to a person");
            System.out.println("6. Delete a person");
            System.out.println("7. Quit");

            int nb = sc.nextInt();

            switch (nb)
            {
                case 1:
                    HBaseTableObj.createPeople();
                    break;
                case 2:
                    System.out.println("Please write the name of the person you are looking for :");
                    name = sc.nextLine();
                    HBaseTableObj.searchForAPerson(name);
                    break;
                case 3:
                    HBaseTableObj.searchForAPersonByGender();
                    break;
                case 4:
                    HBaseTableObj.addFriendToAPerson();
                    break;
                case 5:
                    System.out.println("Please write the name of the person you are looking for :");
                    name = sc.nextLine();
                    HBaseTableObj.deleteAFriendToAPerson(name);
                case 6:
                    HBaseTableObj.deleteAPerson();
                    break;
                case 7:
                    choiceDone = false;
                    break;
                default:
                    System.out.println("Bad choice do it again !");
            }
        }
    }
}
