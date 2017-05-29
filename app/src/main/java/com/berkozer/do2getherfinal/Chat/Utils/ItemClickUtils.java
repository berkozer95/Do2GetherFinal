package com.berkozer.do2getherfinal.Chat.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.berkozer.do2getherfinal.R;

/**
 * Created by berkozer on 04/05/2017.
 */

public class ItemClickUtils {
    private final RecyclerView mRecyclerView;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        }
    };

    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener);
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    private ItemClickUtils(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.item_click_utils, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static ItemClickUtils addTo(RecyclerView view) {
        ItemClickUtils support = (ItemClickUtils) view.getTag(R.id.item_click_utils);
        if (support == null) {
            support = new ItemClickUtils(view);
        }
        return support;
    }

    public ItemClickUtils setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    public ItemClickUtils setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        return this;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_utils, null);
    }

    public interface OnItemClickListener {

        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }
}
