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
import co.usam.donateblood.chatApp.MainActivity;
import co.usam.donateblood.chatApp.helper.Preferences;
import co.usam.donateblood.chatApp.model.Message;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private ArrayList<Message> messages;

    public MessageAdapter(@NonNull Context context, @NonNull ArrayList<Message> objects) {
        super(context, 0, objects);
        this.context = context;
        this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if (messages != null) {
            Preferences preferences = new Preferences(context);
            String idSender = MainActivity.mAuth.getCurrentUser().getPhoneNumber();

            //initializing object for building the view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //retrieving message
            Message message = messages.get(position);

            //building view from XML
            if (idSender.equals(message.getUserId())) {
                view = inflater.inflate(R.layout.item_message_right, parent, false);
            } else {
                view = inflater.inflate(R.layout.item_message_left, parent, false);
            }


            //retrieving element for exibition
            TextView messageText = view.findViewById(R.id.tv_message);
            messageText.setText(message.getMessage());

        }

        return view;
    }
}
