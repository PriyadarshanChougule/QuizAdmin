package com.example.quizadmin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import static com.example.quiz.SplashActivity.catList;


public class CategoryActivity extends AppCompatActivity {

    private RecyclerView cat_recycler_view;
    private Button addCatB;
    public static List<CaregoryModel> catList = new ArrayList<>();
    public static  int selected_cat_index=0;

    private FirebaseFirestore firestore;
    private Dialog loadingDialog;
    private Dialog addCatDialog;
    private EditText dialogCatName;
    private Button dialogAddB;
    private CategoryAdapter adapter;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Category");


        cat_recycler_view = findViewById(R.id.cat_recycler);
        addCatB = findViewById(R.id.addCatB);


        loadingDialog = new Dialog(CategoryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addCatDialog= new Dialog(CategoryActivity.this);
        addCatDialog.setContentView(R.layout.add_category_dialog);
        addCatDialog.setCancelable(true);
        addCatDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogCatName =addCatDialog.findViewById(R.id.ac_cat_name);
        dialogAddB = addCatDialog.findViewById(R.id.ac_add_btn);

        firestore = FirebaseFirestore.getInstance();

        addCatB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCatName.getText().clear();
                addCatDialog.show();

            }
        });
        dialogAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogCatName.getText().toString().isEmpty()){
                    dialogCatName.setError("Enter Category Name");
                    return;
                }

                //new added code
                /*else if (catList.contains(dialogCatName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Category exists alreaDy",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                //new added code ends here

                else {
                    addNewCategory(dialogCatName.getText().toString());
                }
            }
        });



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cat_recycler_view.setLayoutManager(layoutManager);

        loadData();

    }

    private void loadData(){
        loadingDialog.show();
        catList.clear();

        firestore.collection("quiz").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists()){
                        long count = (long)doc.get("COUNT");
                        for(int i=1; i<=count;i++){
                            String catName = doc.getString("CAT" + String.valueOf(i)+ "_NAME");
                            String catid = doc.getString("CAT" + String.valueOf(i)+ "_ID");

                            catList.add(new CaregoryModel(catid,catName,"0","1"));
                        }
                        adapter = new CategoryAdapter(catList);
                        cat_recycler_view.setAdapter(adapter);
                    }
                    else{
                        Toast.makeText(CategoryActivity.this,"No Catagory document Exists!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else{
                    Toast.makeText(CategoryActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }


    private void addNewCategory(String title){
        addCatDialog.dismiss();
        loadingDialog.show();

        Map<String,Object> catData = new ArrayMap<>();
        catData.put("NAME",title);
        catData.put("SETS",0);
        catData.put("COUNTER","1");

        String doc_id = firestore.collection("quiz").document().getId();

        firestore.collection("quiz").document(doc_id).set(catData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                    Map<String, Object> catDoc = new ArrayMap<>();
                    catDoc.put("CAT" + String.valueOf(catList.size() + 1) + "_NAME", title);
                    catDoc.put("CAT" + String.valueOf(catList.size() + 1) + "_ID", doc_id);
                    catDoc.put("COUNT", catList.size() + 1);

                firestore.collection("quiz").document("Categories").update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CategoryActivity.this,"Category Added Successfully",Toast.LENGTH_SHORT).show();
                        catList.add(new CaregoryModel(doc_id,title,"0","1"));
                        adapter.notifyItemInserted(catList.size());

                        loadingDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CategoryActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
            }



        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CategoryActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });



}





    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.menu_about){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(CategoryActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        //new code
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        AlertDialog diaBox = AskOption();
        diaBox.show();

    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finishAffinity();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }




}