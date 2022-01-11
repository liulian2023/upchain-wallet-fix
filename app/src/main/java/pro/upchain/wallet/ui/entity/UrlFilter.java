package pro.upchain.wallet.ui.entity;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import pro.upchain.wallet.ui.adapter.AutoCompleteUrlAdapter;

public class UrlFilter extends Filter {
    AutoCompleteUrlAdapter adapter;
    List<String> originalList;
    List<String> filteredList;

    public UrlFilter(AutoCompleteUrlAdapter adapter, List<String> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = originalList;
        this.filteredList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();

            // Filtering logic goes in here
            for (final String url : originalList) {
                if (url.toLowerCase().contains(filterPattern)) {
                    filteredList.add(url);
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.filteredUrls.clear();
        adapter.filteredUrls.addAll((List) results.values);
        adapter.notifyDataSetChanged();
    }
}
