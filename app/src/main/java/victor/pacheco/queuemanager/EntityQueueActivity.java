package victor.pacheco.queuemanager;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class EntityQueueActivity extends AppCompatActivity {

    private String queueId;
    private ImageView wt_icon;
    private ImageView list_size_icon;
    private TextView timming_view;
    private TextView queue_size_view;
    private RecyclerView entity_userlist_recycler;
    private EntityQueueActivity.Adapter adapter;
    private Button btn_next;
    List<User> users_list;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_queue);

        wt_icon = findViewById(R.id.wt_icon);
        list_size_icon = findViewById(R.id.list_size_icon);
        timming_view = findViewById(R.id.timming_view);
        queue_size_view = findViewById(R.id.queue_size_view);
        entity_userlist_recycler = findViewById(R.id.entity_userlist_recycler);
        entity_userlist_recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        entity_userlist_recycler.setAdapter(adapter);

        queueId = getIntent().getStringExtra("queueId");

        Glide.with(this).load("file:///android_asset/wait-time-icon.png").into(wt_icon);
        Glide.with(this).load("file:///android_asset/user-size-list.png").into(list_size_icon);

        users_list = new ArrayList<>();
        readProfileData();
    }

    public void readProfileData(){

        db.collection("Queues").document(queueId).collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() { // actualiza la queue_set_list con
            // la lista que tenemos en firebase
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                users_list.clear(); //borra la lista
                for (DocumentSnapshot doc : queryDocumentSnapshots) {  //la rellena de nuevo la lista
                    User u = doc.toObject(User.class);
                    users_list.add(u);
                }
                adapter.notifyDataSetChanged();

                //añado en el firebase el numero de usuarios que tiene cada cola
                Integer usr_list_size = users_list.size();
                db.collection("Queues").document(queueId).update("numuser",usr_list_size);
                if(usr_list_size == 1){
                    User u = users_list.get(0);
                    db.collection("Queues").document(queueId).update("current_user", u.getUsr_id() );
                }

                queue_size_view.setText(usr_list_size.toString());
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView queue_name_view;
        public ViewHolder(View itemView) {
            super(itemView);
            this.queue_name_view = itemView.findViewById(R.id.queue_name_view);
        }
    }

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
            User user_item  = users_list.get(position);
            // Reciclamos el itemView
            holder.queue_name_view.setText(user_item.getUsr_id());
        }

        @Override
        //Puedo acceder al item (que es un campo de la actividad) pq el Adapter es una clase interna de la actividad.
        public int getItemCount() {
            return users_list.size();
        }
    }

}
