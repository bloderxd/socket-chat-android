package com.example.bloder.socketchat.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by bloder on 28/12/15.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.InternalViewHolder>{

    private List<Message> messages;
    private Context context;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public InternalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InternalViewHolder();
    }

    @Override
    public void onBindViewHolder(InternalViewHolder holder, int position) {
        holder.bind(messages.get(position).messageUser, messages.get(position).message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class InternalViewHolder extends RecyclerView.ViewHolder {

        public InternalViewHolder() {
            super(MessageViewHolder_.build(context));
        }

        public void bind(String username, String message){
            ((MessageViewHolder) itemView).bind(username, message);
        }
    }
}
