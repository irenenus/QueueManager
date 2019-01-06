package victor.pacheco.queuemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class EntityProfileActivity extends AppCompatActivity {

    //Modelo
    List<Queue> queue_set_list;
    List<String> users_list;

    // Referencias a objetos de la pantalla
    private RecyclerView entity_queue_recycler;
    private Adapter adapter;
    private Button btn_new_queue;
    private String queue_name;
    private Integer slot_time;
    private Integer closing_hour;
    private Integer closing_min;

    private String current_user;
    private Integer current_pos;


        // Para leer y escribir datos en la base de datos, necesitamos una instancia de FirebaseStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_profile);

        queue_set_list = new ArrayList<>();
        users_list = new ArrayList<>();


        // db.collection("Queues").document(queueId).collection("users").addSnapsh

        entity_queue_recycler = findViewById(R.id.entity_queue_recycler);
        entity_queue_recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        entity_queue_recycler.setAdapter(adapter);

        btn_new_queue = findViewById(R.id.btn_create_queue);
        readProfileData();

    }

    public void readProfileData(){
        db.collection("Queues").addSnapshotListener(new EventListener<QuerySnapshot>() { // actualiza la queue_set_list con
            // la lista que tenemos en firebase
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                queue_set_list.clear(); //borra la lista
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Queue q = doc.toObject(Queue.class);
                    q.setId(doc.getId());
                    queue_set_list.add(q);
                }
                adapter.notifyDataSetChanged();
            }
        });

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
                    current_user ="";
                    current_pos = 0;
                    queue_set_list.add(new Queue(queue_name, slot_time,closing_hour,closing_min, 0, current_user, current_pos,false) );
                    int pos = queue_set_list.size();

                    // Notificamos cambios en el Recycler
                    adapter.notifyItemInserted(pos - 1);


                    // Añadimos la nueva cola a Firebase

                    db.collection("Queues").document(queue_name).set(new Queue(queue_name, slot_time,closing_hour,closing_min,0, current_user, current_pos, false));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.queue_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_remove_selected:

                int i = 0;
                while (i < queue_set_list.size()) {
                    if (queue_set_list.get(i).isChecked()) {
                        db.collection("Queues").document(queue_set_list.get(i).getId()).delete();
                        queue_set_list.remove(i);


                    } else {
                        i++;
                    }
                }
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    public void onLongClickItem(final int position) {
        Queue queue = queue_set_list.get(position);
        queue.setChecked(true);
    }


    // El ViewHolder mantiene referencias a las partes del itemView que cambian cuando la reciclamos. Es una inner class de la clase ShoppingListActivity
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView queue_name_view;

        // constructor creado con alt return sobre ViewHolder, donde recibirá el itemView
        public ViewHolder(View itemView) {
            super(itemView);
            // Obtenemos las referencias a objetos dentro del itemView
            queue_name_view =itemView.findViewById((R.id.queue_name_view));
            queue_name_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String queueId = queue_set_list.get(pos).getId();
                    Intent intent = new Intent(getApplicationContext(), EntityQueueActivity.class);
                    intent.putExtra("queueId", queueId);
                    startActivity(intent);
                }
            });

            queue_name_view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickItem(getAdapterPosition());

                    return true;
                }
            });


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
            Queue queue_item  = queue_set_list.get(position);
            // Reciclamos el itemView
            holder.queue_name_view.setText(queue_item.getQueue_name());
        }

        @Override
        //Puedo acceder al item (que es un campo de la actividad) pq el Adapter es una clase interna de la actividad.
        public int getItemCount() {
            return queue_set_list.size();
        }
    }

}
