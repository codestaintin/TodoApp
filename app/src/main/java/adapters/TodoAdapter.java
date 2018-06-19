package adapters;

import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.lv.isioyemohammed.datapersistence.R;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import models.Todo;
import views.TodoListActivity;

/**
 * Created by isioyemohammed on 19/06/2018.
 * DataPersistence
 */

public class TodoAdapter extends RealmBaseAdapter<Todo> implements ListAdapter {
    private TodoListActivity todoListActivity;

    private static class ViewHolder {
        TextView todoName;
        CheckBox isTodoDone;
    }

    public TodoAdapter(TodoListActivity todoListActivity, @Nullable OrderedRealmCollection<Todo> data) {
        super(data);
        this.todoListActivity = todoListActivity;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.todoName = (TextView) convertView.findViewById(R.id.todo_item_name);
            viewHolder.isTodoDone = (CheckBox) convertView.findViewById(R.id.todo_item_done);
            viewHolder.isTodoDone.setOnClickListener(listener);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Todo todo = adapterData.get(position);
            viewHolder.todoName.setText(todo.getName());
            viewHolder.isTodoDone.setChecked(todo.isDone());
            viewHolder.isTodoDone.setTag(position);
        }
        return convertView;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public  void onClick(View view) {
            int position = (Integer) view.getTag();
            if (adapterData != null) {
                Todo todo = adapterData.get(position);
                todoListActivity.changeTodoDone(todo.getId());
            }
        }
    };
}
