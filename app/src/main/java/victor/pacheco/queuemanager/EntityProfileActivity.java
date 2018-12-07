package victor.pacheco.queuemanager;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityProfileActivity extends AppCompatActivity {

    //Modelo
    List<Queue> queue_list;
    List<Queue> queue_list2;
    Map<String, List> queueMap;

    // Referencias a objetos de la pantalla
    private RecyclerView entity_queue_recycler;
    private Adapter adapter;
    private Button btn_new_queue;
    private String queue_name;
    private Integer slot_time;
    private Integer closing_hour;
    private Integer closing_min;


    // Para leer y escribir datos en la base de datos, necesitamos una instancia de FirebaseStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EntityProfileActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_queue);

        queue_list = new ArrayList<>();

        /*
        user_list = new ArrayList<>();
        user_list.add(new User("Segismundo", 15));
        user_list.add(new User("Anacleto",30));
        user_list.add(new User("Austringiliano",45));
        user_list.add(new User("Beraquisio",60));
        user_list.add(new User("Onésimo",75));
        user_list.add(new User("Eufrasio",90));
        user_list.add(new User("Visitación",105));
        user_list.add(new User("Wilfredo",130));
        user_list.add(new User("Ataulfo",145));
        user_list.add(new User("Orencia",160));
        user_list.add(new User("Tenebrina",175));
        user_list.add(new User("Etelvina",190));
        user_list.add(new User("Gertrudis",205)); */



        entity_queue_recycler = findViewById(R.id.entity_queue_recycler);
        entity_queue_recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        entity_queue_recycler.setAdapter(adapter);

        btn_new_queue = findViewById(R.id.btn_create_queue);

    }


    public void new_queue (View view) {

        // Llamamos a CreateQueueActivity
        Intent intent = new Intent(this,CreateQueueActivity.class);
        intent.putExtra("queue", queue_name);
        intent.putExtra("slot", slot_time);
        intent.putExtra("close_h", closing_hour);
        intent.putExtra("close_m", closing_min);

        startActivityForResult(intent,0);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK){
                    queue_name = data.getStringExtra("queue");
                    slot_time = data.getIntExtra("slot",-1);
                    closing_hour = data.getIntExtra("close_h",-1);
                    closing_min = data.getIntExtra("close_m",-1);
                    queue_list.add(new Queue(queue_name, slot_time,closing_hour,closing_min) );
                    int pos = queue_list.size();
                    adapter.notifyItemInserted(pos - 1);

                    queueMap = new HashMap<>();
                    // Creamos una nueva lista para que no nos añada la información de todas las listas y se cree redundancia
                    queue_list2 = new ArrayList<>();
                    queue_list2.add(new Queue(queue_name, slot_time,closing_hour,closing_min) );


                    queueMap.put(queue_name, queue_list2);
                    db.collection("cues").document(queue_name).set(queueMap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }


    // El ViewHolder mantiene referencias a las partes del itemView que cambian cuando la reciclamos. Es una inner class de la clase ShoppingListActivity
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView queue_name_view;

        // constructor creado con alt return sobre ViewHolder, donde recibirá el itemView
        public ViewHolder(View itemView) {
            super(itemView);
            // Obtenemos las referencias a objetos dentro del itemView
            queue_name_view =itemView.findViewById((R.id.queue_name_view));
        }
    }

    // El Adapter es otra inner class. Le dirá al Recicler cuandos elementos hay, cuando hay que reciclar y los elementos de estos.
    class Adapter extends RecyclerView.Adapter<ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Creamos un item de la pantalla a partir del layout
            View itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            // Creamos y retornamos el ViewHolder asociado
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Vamos al modelo y obtenemos el valor en la posicion que nos pasan
            Queue queue_item  = queue_list.get(position);
            // Reciclamos el itemView
            holder.queue_name_view.setText(queue_item.getQueue_name());
            //holder.user_view.setText((user_item.getWaiting_time()));
        }

        @Override
        //Puedo acceder al item (que es un campo de la actividad) pq el Adapter es una clase interna de la actividad.
        public int getItemCount() {
            return queue_list.size();
        }
    }

}
