package views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.lv.isioyemohammed.datapersistence.R;

import java.util.UUID;

import adapters.TodoAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import models.Todo;

public class TodoListActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        RealmResults<Todo> todos = realm.where(Todo.class).findAll();
        final TodoAdapter adapter = new TodoAdapter(this, todos);

        ListView listView = (ListView) findViewById(R.id.todo_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Todo todo = (Todo) adapterView.getAdapter().getItem(position);
                final EditText todoEditText = new EditText(TodoListActivity.this);
                todoEditText.setText(todo.getName());
                AlertDialog alertDialog = new AlertDialog.Builder(TodoListActivity.this)
                        .setTitle("Edit Todo")
                        .setView(todoEditText)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changeTodoName(todo.getId(), String.valueOf(todoEditText));
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTodo(todo.getId());
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText todoEditText = new EditText(TodoListActivity.this);
                AlertDialog alertDialog = new AlertDialog.Builder(TodoListActivity.this)
                        .setTitle("Add Todo")
                        .setView(todoEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.createObject(Todo.class, UUID.randomUUID().toString())
                                                .setName(String.valueOf(todoEditText.getText()));
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_settings) {
            deleteAllDoneTodos();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeTodoDone(final String todoId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Todo todo =  realm.where(Todo.class).equalTo("id", todoId).findFirst();
                todo.setDone(!todo.isDone());
            }
        });
    }

    private void deleteTodo(final String todoId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Todo.class).equalTo("id", todoId)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }

    private void deleteAllDoneTodos() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Todo.class).equalTo("done", true)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void changeTodoName(final String todoId, final String name) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Todo todo =  realm.where(Todo.class).equalTo("id", todoId).findFirst();
                todo.setName(name);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
