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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class EntityQueueActivity extends AppCompatActivity {

    private String queueId;
    private ImageView wt_icon;
    private ImageView list_size_icon;
    private TextView queue_size_view;
    private RecyclerView entity_userlist_recycler;
    private EntityQueueActivity.Adapter adapter;
    private Button btn_next;
    private boolean siguiente=false;
    private Integer n=0;
    private Integer i;
    List<User> users_list;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_queue);

        wt_icon = findViewById(R.id.wt_icon);
        list_size_icon = findViewById(R.id.list_size_icon);
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
        btn_next = findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                siguiente=true;
                n++;
                readProfileData();
            }
        });
    }

    public void readProfileData(){
        // Ordenamos la lista por oden de hora:min:sec de registro
        db.collection("Queues").document(queueId).collection("Users")
                .orderBy("acces_time",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() { // actualiza la queue_set_list con
            //Si hay cambios en la colección Usuario de dicha cola...
            @Override
            public void onEvent(@Nullable final QuerySnapshot query, @Nullable FirebaseFirestoreException e) {
                users_list.clear(); //borra la lista
                i=1;
                // Obtenemos el documento de firestore de cada Usuario
                for (final DocumentSnapshot doc : query) {  //la rellena de nuevo la lista
                    User u = doc.toObject(User.class);
                    // Lo añadimos a la lista que utilizará el recycler
                    users_list.add(u);
                    // Obtenemos el id del documento de cada usuario
                    String usr_id = doc.getId();
                    // Le asignamos su posición en la cola
                    db.collection("Queues").document(queueId).collection("Users").document(usr_id).update("usr_pos", i);
                    i++;
                }
                // Nofitifcamos al adaptador que se han producido cambios, para que refresce el recycler
                adapter.notifyDataSetChanged();
                //añado en el firebase el numero de usuarios que tiene cada cola
                Integer usr_list_size = users_list.size();
                // refrescamos el tamaño de la cola
                db.collection("Queues").document(queueId).update("numuser",usr_list_size);
                //Si solo hay un usuario, le asignamos la primera posición y le damos el token para ser atendido
                if(usr_list_size == 1){
                    User u = users_list.get(n);
                    db.collection("Queues").document(queueId).update("current_user", u.getUsr_id() );
                    db.collection("Queues").document(queueId).update("current_pos",1);
                }
                //LLamamos al siguiente usuario cuando detectamos un click en siguiente
                else{
                    if (siguiente==true){
                        User u = users_list.get(n);
                        db.collection("Queues").document(queueId).update("current_user", u.getUsr_id() );
                        db.collection("Queues").document(queueId).update("current_pos", n+1);
                        siguiente=false;

                    }
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
/*            if() {
                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
          } */
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
