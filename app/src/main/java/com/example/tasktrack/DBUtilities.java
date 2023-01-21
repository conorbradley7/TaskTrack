package com.example.tasktrack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DBUtilities {
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

//==================================================================================================
// Checks user logged in status
    public static Boolean checkLoggedIn(){
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            return false;
        }
        return true;
    }

//==================================================================================================
//Sign In
//    public static Boolean signIn(String email, String password){
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            System.out.println(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(LoginActivity.this, "Login failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//==================================================================================================
// Gets User's tasks

//    public static ArrayList<String> getTasks(Context context){
//        db = FirebaseFirestore.getInstance();
//        System.out.println("=========="+mAuth.getCurrentUser().getUid());
//        ArrayList<String> tasks = new ArrayList<String>();
//
//        db.collection("users")
//                .document(mAuth.getCurrentUser().getUid())
//                .collection("tasks")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                System.out.println("================"+document.getData().toString());
//                                tasks.add(document.getData().toString());
////                                populateData(tasks);
//                            }
//                            System.out.println("+++++++++++++="+tasks);
////                            Intent intent = new Intent(context, TasksPageActivity.class);
////                            context.startActivity(intent);
//                        }
//                    }
//                });
//
//        System.out.println("1111111111111");
//        System.out.println(tasks);
//        return tasks;
//    }

//==================================================================================================

    public static void signOut(){
        mAuth.getInstance().signOut();
    }

//==================================================================================================

    public static String getUserName(){
        db = FirebaseFirestore.getInstance();
        String name = "";
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                        }
                    }
                });
        return name;
    }}
