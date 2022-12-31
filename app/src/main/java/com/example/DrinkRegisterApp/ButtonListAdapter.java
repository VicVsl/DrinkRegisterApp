package com.example.DrinkRegisterApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;


public class ButtonListAdapter extends RecyclerView.Adapter<ButtonListAdapter.ButtonViewHolder> {

    private final MainActivity app;

    public ButtonListAdapter(MainActivity app) {
        this.app = app;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_item, parent, false);
        return new ButtonViewHolder(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ButtonViewHolder holder, int position) {
        User user = app.getUsers().get(position);

        // Sets text and updates background every two buttons for alternating layout
        holder.button.setText(user.getFirstName());
        if (position % 2 == 0) {
            holder.button.setBackgroundColor(app.getResources().getColor(R.color.yellow));
            holder.button.setTextColor(app.getResources().getColor(R.color.black));
        }

        // Configures the addition of balance
        holder.button.setOnClickListener(view -> {
            if (app.isVerified()) {
                user.addBalance(0.7);
                app.getMdbHelper().updateBalance(user);
            } else {
                app.setLogin(user);
                app.onButtonShowPopupWindowClick(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return app.getUsers().size();
    }

    // Inner class to hold a reference to each item of RecyclerView
    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        public Button button;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
