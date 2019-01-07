package victor.pacheco.queuemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                String closing_hour = closing_hour_edit.getText().toString();
                String closing_min = closing_min_edit.getText().toString();
                int h = Integer.parseInt(closing_hour);
                int m = Integer.parseInt(closing_min);

                if ((h > 24 || h < 0) || (m > 59 || m < 0)) {
                    Toast.makeText(CreateQueueActivity.this, "The time is not correct", Toast.LENGTH_SHORT).show();

                }

                else{
                    db.collection("Queues").addSnapshotListener(new EventListener<QuerySnapshot>() { // actualiza la queue_set_list con
                        // la lista que tenemos en firebase
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            Boolean colaencontrada = false;
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                Queue q = doc.toObject(Queue.class);
                                q.setId(doc.getId());
                                if (q.getId().equals(queue_name)) {
                                    colaencontrada = true;

                                }


                            }
                            if (colaencontrada == true) {
                                Toast.makeText(CreateQueueActivity.this, "The queueId already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                CreateQue();
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
