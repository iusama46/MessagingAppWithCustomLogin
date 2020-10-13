package co.usam.donateblood.chatApp.adapter;

/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


import co.usam.donateblood.R;
import co.usam.donateblood.chatApp.model.Chat;

public class ChatAdapter extends ArrayAdapter<Chat> {
    private ArrayList<Chat> chats;
    private Context context;

    public ChatAdapter(@NonNull Context context, @NonNull ArrayList<Chat> objects) {
        super(context, 0, objects);
        this.chats = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verifying if list is empty
        if (chats != null) {
            //inicializing object for building the view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //building view from XML
            view = inflater.inflate(R.layout.chat_list, parent, false);

            //retrieving element for exibition
            TextView name = view.findViewById(R.id.tv_name);
            TextView message = view.findViewById(R.id.tv_message);

            Chat chat = chats.get(position);
            name.setText(chat.getName());
            message.setText(chat.getMessage());

        }

        return view;
    }
}