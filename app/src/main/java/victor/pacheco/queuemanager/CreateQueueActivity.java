package victor.pacheco.queuemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import javax.annotation.Nullable;

import java.sql.Time;

public class CreateQueueActivity extends AppCompatActivity {

    private EditText queue_name_edit;
    private EditText closing_hour_edit;
    private EditText closing_min_edit;
    private EditText slot_time_edit;
    private Button btn_create_queue;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_queue);

        // Obtenemos referencias a los objetos de la pantalla
        queue_name_edit = findViewById(R.id.queue_name_edit);
        closing_hour_edit = findViewById(R.id.closing_hour_edit);
        closing_min_edit = findViewById(R.id.closing_min_edit);
        slot_time_edit = findViewById(R.id.slot_time_edit);
        btn_create_queue = findViewById(R.id.btn_create_queue);

        // Detectamos clicks para generar una cola
        btn_create_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String queue_name = queue_name_edit.getText().toString();
                if(queue_name.equals("")) {
                    Toast.makeText(CreateQueueActivity.this, "Please, fill the Queue name field", Toast.LENGTH_SHORT).show();
                }
                else{
                    db.collection("Queues").whereEqualTo("queue_name", queue_name).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.isEmpty()){
                                        CreateQue();
                                    }
                                    else {
                                        Toast.makeText(CreateQueueActivity.this, "The queueId already exists.", Toast.LENGTH_SHORT).show();

                                    }
                                }

                            });
                }

            }
        });

    }

    public void CreateQue(){
        final String queue_name = queue_name_edit.getText().toString();
        Integer slot_time = Integer.parseInt(slot_time_edit.getText().toString());
        Integer closing_hour = Integer.parseInt(closing_hour_edit.getText().toString());
        Integer closing_min = Integer.parseInt(closing_min_edit.getText().toString());

        Intent data = new Intent();
        data.putExtra("queue", queue_name );
        data.putExtra("slot", slot_time);
        data.putExtra("close_h", closing_hour);
        data.putExtra("close_m", closing_min);



        setResult(RESULT_OK, data);
        finish();

    }

}
