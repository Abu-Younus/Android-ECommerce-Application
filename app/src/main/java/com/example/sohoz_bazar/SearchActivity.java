package com.example.sohoz_bazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private TextView tvNotFound;
    private RecyclerView searchRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view);
        tvNotFound = findViewById(R.id.tv_not_found);
        searchRecyclerView = findViewById(R.id.search_recycler_view);

        searchRecyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        searchRecyclerView.setLayoutManager(linearLayoutManager);

        final List<WishlistModel> list = new ArrayList<>();
        final List<String> ids = new ArrayList<>();

        final FilterAdapter filterAdapter = new FilterAdapter(list, false);
        filterAdapter.setFromSearch(true);
        searchRecyclerView.setAdapter(filterAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                list.clear();
                ids.clear();
                final String[] tags = query.toLowerCase().split(" ");
                for (final String tag : tags) {
                    tag.trim();
                    FirebaseFirestore.getInstance().collection("PRODUCTS").whereArrayContains("tags", tag)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    WishlistModel model = new WishlistModel(documentSnapshot.getId(), documentSnapshot.get("product_image_1").toString(),
                                            documentSnapshot.get("product_title").toString(),
                                            (long) documentSnapshot.get("free_coupons"),
                                            documentSnapshot.get("average_ratings").toString(),
                                            (long) documentSnapshot.get("total_ratings"),
                                            documentSnapshot.get("product_price").toString(),
                                            documentSnapshot.get("cutted_price").toString(),
                                            (boolean) documentSnapshot.get("COD"),
                                            true);
                                    model.setTags((ArrayList<String>) documentSnapshot.get("tags"));
                                    if (!ids.contains(model.getProductID())) {
                                        list.add(model);
                                        ids.add(model.getProductID());
                                    }
                                }
                                if (tag.equals(tags[tags.length - 1])) {
                                    if (list.size() == 0) {
                                        tvNotFound.setVisibility(View.VISIBLE);
                                        searchRecyclerView.setVisibility(View.GONE);
                                    } else {
                                        tvNotFound.setVisibility(View.GONE);
                                        searchRecyclerView.setVisibility(View.VISIBLE);
                                        filterAdapter.getFilter().filter(query);
                                    }
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    class FilterAdapter extends WishlistAdapter implements Filterable {
        private List<WishlistModel> originalList;
        public FilterAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
            super(wishlistModelList, wishlist);
            originalList = wishlistModelList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<WishlistModel> filteredList = new ArrayList<>();
                    final String[] tags = constraint.toString().toLowerCase().split(" ");
                    for (WishlistModel model : originalList) {
                        ArrayList<String> presentTags = new ArrayList<>();
                        for (String tag : tags) {
                            if (model.getTags().contains(tag)) {
                                presentTags.add(tag);
                            }
                        }
                        model.setTags(presentTags);
                    }
                    for (int i = tags.length;i > 0;i--) {
                        for (WishlistModel model : originalList) {
                            if (model.getTags().size() == i) {
                                filteredList.add(model);
                            }
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.count > 0) {
                        setWishlistModelList((List<WishlistModel>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}
