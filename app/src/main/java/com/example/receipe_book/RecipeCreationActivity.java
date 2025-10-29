package com.example.receipe_book;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RecipeCreationActivity extends AppCompatActivity {
    private RecipeDBHelper dbHelper;
    private SQLiteDatabase database;
    private EditText recipeNameEditText;
    private LinearLayout ingredientsLayout;
    private EditText preparationStepsEditText;
    private EditText notesEditText;
    private Button addIngredientButton;
    private Button saveButton;
    private List<EditText> ingredientEditTextList;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_creation);

        dbHelper = new RecipeDBHelper(this);
        database = dbHelper.getWritableDatabase();

        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        preparationStepsEditText = findViewById(R.id.preparationStepsEditText);
        notesEditText = findViewById(R.id.notesEditText);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        saveButton = findViewById(R.id.saveButton);

        ingredientEditTextList = new ArrayList<>();

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngredientEditText();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecipe();
            }
        });

        recipeId = getIntent().getIntExtra("recipeId", -1);
        if (recipeId != -1) {
            loadRecipeDetails();
        }
    }

    private void addIngredientEditText() {
        EditText ingredientEditText = new EditText(this);
        ingredientEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ingredientEditText.setHint("Ingredient");
        ingredientsLayout.addView(ingredientEditText);
        ingredientEditTextList.add(ingredientEditText);
    }

    private void saveRecipe() {
        String recipeName = recipeNameEditText.getText().toString().trim();
        String ingredients = getIngredientsAsString();
        String preparationSteps = preparationStepsEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();

        if (recipeName.isEmpty()) {
            Toast.makeText(this, "Please enter a recipe name", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", recipeName);
        values.put("description", ""); // Add description if needed
        values.put("ingredients", ingredients);
        values.put("preparation_steps", preparationSteps);
        values.put("notes", notes);

        if (recipeId == -1) {
            long id = database.insert("recipes", null, values);
            if (id != -1) {
                Toast.makeText(this, "Recipe saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = database.update("recipes", values, "id = ?",
                    new String[]{String.valueOf(recipeId)});
            if (rowsAffected > 0) {
                Toast.makeText(this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private String getIngredientsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (EditText editText : ingredientEditTextList) {
            String ingredient = editText.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                stringBuilder.append(ingredient).append("\n");
            }
        }
        return stringBuilder.toString().trim();
    }

    private void loadRecipeDetails() {
        Cursor cursor = database.rawQuery("SELECT * FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String ingredients = cursor.getString(cursor.getColumnIndex("ingredients"));
            @SuppressLint("Range") String preparationSteps = cursor.getString(cursor.getColumnIndex("preparation_steps"));
            @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex("notes"));

            recipeNameEditText.setText(name);
            preparationStepsEditText.setText(preparationSteps);
            notesEditText.setText(notes);

            if (ingredients != null && !ingredients.isEmpty()) {
                String[] ingredientArray = ingredients.split("\n");
                for (String ingredient : ingredientArray) {
                    EditText ingredientEditText = new EditText(this);
                    ingredientEditText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    ingredientEditText.setHint("Ingredient");
                    ingredientEditText.setText(ingredient);
                    ingredientsLayout.addView(ingredientEditText);
                    ingredientEditTextList.add(ingredientEditText);
                }
            }
        }
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}