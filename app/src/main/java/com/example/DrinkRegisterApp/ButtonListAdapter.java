package com.example.DrinkRegisterApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


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
                Change change = new Change(user.getFirstName(), user.getLastName(), 1);
                List<Change> changes = app.getChanges();
                if (changes.contains(change)) {
                    int index = changes.indexOf(change);
                    app.getChanges().get(index).setAmount(changes.get(index).getAmount() + 1);
                } else {
                    app.getChanges().add(change);
                }
            } else {
                if (!user.getFirstName().equals("admin")) {
                    app.setLogin(app.getMdbHelper().findUserByName(user.getFirstName(), user.getLastName()));
                } else {
                    app.setLogin(user);
                }
                app.getPpuHelper().setUpdate(false);
                app.getPpuHelper().showPincode(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return app.getUsers().size();
    }

    // Inner class to hold a reference to each item of RecyclerView
    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        public final Button button;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
