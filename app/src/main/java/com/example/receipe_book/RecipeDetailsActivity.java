package com.example.receipe_book;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {
    private RecipeDBHelper dbHelper;
    private SQLiteDatabase database;
    private int recipeId;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView preparationStepsTextView;
    private TextView notesTextView;
    private Button editButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        dbHelper = new RecipeDBHelper(this);
        database = dbHelper.getWritableDatabase();

        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        preparationStepsTextView = findViewById(R.id.preparationStepsTextView);
        notesTextView = findViewById(R.id.notesTextView);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        recipeId = getIntent().getIntExtra("recipeId", -1);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailsActivity.this, RecipeCreationActivity.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecipe();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipeDetails();
    }

    private void loadRecipeDetails() {
        Cursor cursor = database.rawQuery("SELECT * FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String ingredients = cursor.getString(cursor.getColumnIndex("ingredients"));
            @SuppressLint("Range") String preparationSteps = cursor.getString(cursor.getColumnIndex("preparation_steps"));
            @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex("notes"));

            recipeNameTextView.setText(name);
            ingredientsTextView.setText("Ingredients:\n" + ingredients);
            preparationStepsTextView.setText("Preparation Steps:\n" + preparationSteps);
            notesTextView.setText("Notes:\n" + notes);
        }
        cursor.close();
    }

    private void deleteRecipe() {
        database.delete("recipes", "id = ?", new String[]{String.valueOf(recipeId)});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}
