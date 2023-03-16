package com.example.tasktrack;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagsFragment extends Fragment {

    private TagsViewAdapter adapter;
    private RecyclerView recycleView = null;
    private User user;
    private RecycleViewInterface recycleViewInterface;
    private Button newTagBtn, newTagAddBtn, newTagBackBtn;
    private AlertDialog.Builder newTagDialogBuilder;
    private AlertDialog newTagDialog;
    private EditText newTagTitle;

    //DB
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

    public TagsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        newTagBtn = (Button) view.findViewById(R.id.newTagBtn);


        recycleView = (RecyclerView) view.findViewById(R.id.tagsRecyclerView);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleView.setItemAnimator(new DefaultItemAnimator());

        if (this.getArguments() != null) {
            user = (User) this.getArguments().getSerializable("user");
            if (user != null){
                adapter = new TagsViewAdapter(getActivity(), R.layout.tag_row_layout, user.getTags(),this.recycleViewInterface);
                recycleView.setAdapter(adapter);
            }
        }

        newTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTagDialog();
            }
        });

        return view;

    }

    public void newTagDialog(){
        newTagDialogBuilder = new AlertDialog.Builder(getActivity());
        final View newTagPopupView = getLayoutInflater().inflate(R.layout.new_tag_popup, null);
        newTagAddBtn = newTagPopupView.findViewById(R.id.addTagBtn);
        newTagBackBtn = newTagPopupView.findViewById(R.id.newTagBackBtn);
        newTagTitle = newTagPopupView.findViewById(R.id.newTagTitleEditText);

        newTagDialogBuilder.setView(newTagPopupView);
        newTagDialog = newTagDialogBuilder.create();
        newTagDialog.show();

        newTagAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTagToDB(newTagTitle.getText().toString().trim());
            }
        });

        newTagBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTagDialog.dismiss();
            }
        });
    }

    public void addTagToDB(String tag){
        ArrayList<String> newTagsList = user.getTags();
        newTagsList.add(tag);
        user.setTags(newTagsList);
        db = FirebaseFirestore.getInstance();

        if (TextUtils.isEmpty(tag)) {
            newTagTitle.setError("Please Enter A Name For The Tag!");
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getActivity(), "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .update("tags", newTagsList)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();
                            newTagDialog.dismiss();
                            onResume();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                            newTagDialog.dismiss();
                            onResume();
                        }
                    });
    }

}

}