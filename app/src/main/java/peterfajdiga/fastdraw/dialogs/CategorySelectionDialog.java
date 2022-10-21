package peterfajdiga.fastdraw.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import peterfajdiga.fastdraw.Categories;
import peterfajdiga.fastdraw.views.AutoGridLayoutManager;

public abstract class CategorySelectionDialog extends DialogFragment {
    private static final String INITIAL_CATEGORY_NAME_KEY = "categoryName";
    private static final String TITLE_KEY = "title";
    private static final float CATEGORY_ITEM_WIDTH_DP = 24;
    private static final float CATEGORY_ITEM_PADDING_DP = 12;

    public abstract void onCategorySelected(@NonNull String initialCategoryName, @NonNull String inputtedCategoryName);

    @Override
    @NonNull
    public final Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context context = getContext();
        assert context != null;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        final Bundle arguments = getArguments();
        assert arguments != null;
        final String initialCategoryName = arguments.getString(INITIAL_CATEGORY_NAME_KEY);
        final String title = arguments.getString(TITLE_KEY);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        final int spanWidth = Math.round(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            CATEGORY_ITEM_WIDTH_DP + 2*CATEGORY_ITEM_PADDING_DP,
            dm
        ));
        final RecyclerView categoriesView = new RecyclerView(context);
        categoriesView.setLayoutManager(new AutoGridLayoutManager(context, spanWidth, GridLayoutManager.VERTICAL, false));
        builder.setView(categoriesView);

        builder.setNegativeButton(android.R.string.cancel, null);

        final Dialog d = builder.create();
        categoriesView.setAdapter(new CategoryItemAdapter(newCategoryName -> {
            onCategorySelected(initialCategoryName, newCategoryName);
            d.dismiss();
        }));
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return d;
    }

    protected void setup(@NonNull final String initialCategoryName, @NonNull final String title) {
        final Bundle args = new Bundle();
        args.putString(INITIAL_CATEGORY_NAME_KEY, initialCategoryName);
        args.putString(TITLE_KEY, title);
        this.setArguments(args);
    }

    private static class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.ItemViewHolder> {
        private static final List<Map.Entry<String, Integer>> CATEGORY_ICONS = new ArrayList<>(Categories.MAP.entrySet());
        private final OnCategoryItemClickedListener itemClickListener;

        public CategoryItemAdapter(final OnCategoryItemClickedListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            final Context context = parent.getContext();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();
            final ImageView view = new ImageView(context);
            final int padding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CATEGORY_ITEM_PADDING_DP, dm));
            view.setPadding(padding, padding, padding, padding);
            return new ItemViewHolder(view, itemClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
            final Map.Entry<String, Integer> categoryEntry = CATEGORY_ICONS.get(position);
            final String categoryName = categoryEntry.getKey();
            final Context context = holder.view.getContext();
            final Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), categoryEntry.getValue(), context.getTheme());
            holder.bind(categoryName, drawable);
        }

        @Override
        public int getItemCount() {
            return CATEGORY_ICONS.size();
        }

        private static class ItemViewHolder extends RecyclerView.ViewHolder {
            private final ImageView view;
            private final OnCategoryItemClickedListener clickListener;

            ItemViewHolder(@NonNull final ImageView itemView, OnCategoryItemClickedListener clickListener) {
                super(itemView);
                this.view = itemView;
                this.clickListener = clickListener;
            }

            void bind(final String categoryName, final Drawable drawable) {
                view.setImageDrawable(drawable);
                view.setOnClickListener(v -> clickListener.OnCategoryItemClicked(categoryName));
            }
        }

        private interface OnCategoryItemClickedListener {
            void OnCategoryItemClicked(String categoryName);
        }
    }
}