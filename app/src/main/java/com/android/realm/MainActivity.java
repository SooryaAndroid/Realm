package com.android.realm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.realm.model.Student;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private EditText name, mark;
    private Button save, show, delete;
    private TextView result;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.name);
        mark = (EditText) findViewById(R.id.mark);
        save = (Button) findViewById(R.id.save_button);
        show = (Button) findViewById(R.id.show_button);
        delete = (Button) findViewById(R.id.delete);
        result = (TextView) findViewById(R.id.result);

        realm = Realm.getDefaultInstance();
        buttonClick();
        showButtonClick();
        deleteButtonClick();
    }

    private void deleteButtonClick() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String sName = name.getText().toString();
                deleteFromDB();
            }
        });
    }

    private void deleteFromDB() {
        final RealmResults<Student> details = realm.where(Student.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                details.deleteFromRealm(0);
            }
        });
    }

    private void showButtonClick() {
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showData();
            }
        });
    }

    private void buttonClick() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sName = name.getText().toString().trim();
                String sMark = mark.getText().toString().trim();
                if (sName.isEmpty()) {
                    name.setError("invalid");
                } else if (sMark.isEmpty()) {
                    mark.setError("invalid");
                }
                if (!sName.isEmpty() && !sMark.isEmpty()) {
                    writeToDB(sName, Integer.parseInt(sMark));
                }

            }
        });
    }

    private void showData() {
        RealmResults<Student> details = realm.where(Student.class).findAllAsync();
        details.load();
        String output = "";
        for (Student student : details) {
            output += student.toString();
        }
        result.setText(output);

    }

    private void writeToDB(final String sName, final int sMark) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Student user = bgRealm.createObject(Student.class);
                user.setName(sName);
                user.setMarks(sMark);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
                // Transaction was a success.
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(MainActivity.this, "Data inserted failed" + error, Toast.LENGTH_SHORT).show();
                // Transaction failed and was automatically canceled.
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
