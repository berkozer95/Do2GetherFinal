package com.berkozer.do2getherfinal.Chat.Core.Users.Get;

import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.berkozer.do2getherfinal.Chat.Model.User;
import com.berkozer.do2getherfinal.Chat.UI.Activities.ChatActivity;
import com.berkozer.do2getherfinal.Chat.UI.Activities.UserListingActivity;
import com.berkozer.do2getherfinal.Chat.UI.Fragments.UsersFragment;
import com.berkozer.do2getherfinal.Chat.Utils.Constants;
import com.berkozer.do2getherfinal.SelectedUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by berkozer on 04/05/2017.
 */

public class GetUserInteractor implements GetUserInterface.Interactor {

    private GetUserInterface.OnGetAllUsersListener mOnGetAllUsersListener;

    GetUserInteractor(GetUserInterface.OnGetAllUsersListener onGetAllUsersListener) {
        this.mOnGetAllUsersListener = onGetAllUsersListener;
    }

    @Override
    public void getAllUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = new User();
                currentUser = dataSnapshot.getValue(User.class);
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<>();
                int count = 0;
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    User user = dataSnapshotChild.getValue(User.class);


                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        if (user.hobbies != null) {
                            createFile("User"+ count + ".txt" , user.hobbies);
                            count++;
                        }
                        users.add(user);

                    } else {
                        currentUserFile("CurrentUser.txt" , user.hobbies);
                    }

                }
                writeFileCount("Count.txt" , count);


                mOnGetAllUsersListener.onGetAllUsersSuccess(sortUsers(users));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public List<User> sortUsers(List<User> users){
        HashMap<String,String > map = new HashMap<String ,String>();
        ArrayList<String > userScores = new ArrayList<>();
        userScores.addAll(UsersFragment.scores);

        List<User> sortedUsers = new ArrayList<>();
        List<String > listToSort = new ArrayList<>();


        for (int i = 0; i < users.size(); i++){
            users.get(i).setMatchScore(userScores.get(i));
            System.out.println(users.get(i).uid+ " score :" + users.get(i).getMatchScore());
            map.put(users.get(i).uid, users.get(i).getMatchScore());
        }

        System.out.println(entriesSortedByValues(map));
        for (int i = 0; i < map.size(); i ++){
            listToSort.add(entriesSortedByValues(map).get(i).getKey());
            System.out.println("LIST TO SORT :" + listToSort.get(i));
        }

        for (int i = 0; i < listToSort.size(); i++){
            for (int j =0; j < listToSort.size();j++){
                if (listToSort.get(i).equals(users.get(j).uid)){
                    sortedUsers.add(users.get(j));
                }
            }
        }

        map.clear();
        userScores.clear();
        UsersFragment.scores.clear();

        return sortedUsers;
    }

    static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K,V>>() {
                    @Override
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }

    public ArrayList<String> readFile() throws IOException {
        File root = new File(Environment.getExternalStorageDirectory(), "Hobbies");
        File[] files = root.listFiles();
        ArrayList<String> scores  = new ArrayList<>();

        for (File f : files) {
            if(f.isFile()) {
                BufferedReader inputStream = null;

                try {
                    inputStream = new BufferedReader(
                            new FileReader(f));
                    String line;

                    while ((line = inputStream.readLine()) != null) {
                        System.out.println(line);
                        scores.add(line);
                    }
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }

        /*for (int i=0; i < count; i ++){
          file = new File(root, "User"+ String.valueOf(count)+ ".txt");

        }

        String result;
        String score;
        ArrayList<String > scores = new ArrayList<>();


        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            StringBuffer stringBuffer = new StringBuffer();

            while ((result=bufferedReader.readLine()) != null){
                stringBuffer.append(result);
            }

            score = stringBuffer.toString();

            scores.add(score);

        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
*/
        return scores;

    }

    public void createFile(String sFileName, ArrayList<String > sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Hobbies");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            for (int i=0; i < sBody.size();i ++){
                writer.append(sBody.get(i)+"\n");

            }
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    public void writeFileCount(String sFileName, int sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "HobbyCount");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(String.valueOf(sBody));

            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    public void currentUserFile(String sFileName, ArrayList<String> sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "CurrentHobbies");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            for (int i=0; i < sBody.size();i ++){
                writer.append(sBody.get(i)+"\n");

            }
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }


}
