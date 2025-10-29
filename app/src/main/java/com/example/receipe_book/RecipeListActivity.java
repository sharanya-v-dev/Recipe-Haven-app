package com.example.receipe_book;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    private RecipeDBHelper dbHelper;
    private SQLiteDatabase database;
    private List<Recipe> recipeList;
    private RecipeListAdapter adapter;
    private ListView recipeListView;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        dbHelper = new RecipeDBHelper(this);
        database = dbHelper.getWritableDatabase();
        recipeList = new ArrayList<>();
        adapter = new RecipeListAdapter(this, recipeList);

        recipeListView = findViewById(R.id.recipeListView);
        emptyTextView = findViewById(R.id.emptyTextView);

        recipeListView.setAdapter(adapter);

        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Recipe selectedRecipe = recipeList.get(position);
                Intent intent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
                intent.putExtra("recipeId", selectedRecipe.getId());
                startActivity(intent);
            }
        });

        findViewById(R.id.addRecipeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecipeListActivity.this, RecipeCreationActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        recipeList.clear();
        Cursor cursor = database.rawQuery("SELECT * FROM recipes", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                Recipe recipe = new Recipe(id, name, description);
                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();

        if (recipeList.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            recipeListView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recipeListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}
