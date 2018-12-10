package victor.pacheco.queuemanager;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EntityQueueActivity extends AppCompatActivity {

    private ImageView wt_icon;
    private ImageView list_size_icon;
    private TextView timming_view;
    private TextView queue_size_view;
    private RecyclerView entity_userlist_recycler;
    private EntityQueueActivity.Adapter adapter;
    private Button btn_next;
    List<String> users_list;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usrListRef = db.collection("Queues").document("User lists");


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



        Glide.with(this).load("file:///android_asset/wait-time-icon.png").into(wt_icon);
        Glide.with(this).load("file:///android_asset/user-size-list.png").into(list_size_icon);

        users_list = new ArrayList<>();

        users_list.add("Segismundo");
        users_list.add("Pancracio");
        users_list.add("Anacleto");
        users_list.add("Nefertina");

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
            String user_item  = users_list.get(position);
            // Reciclamos el itemView
            holder.queue_name_view.setText(user_item);
        }

        @Override
        //Puedo acceder al item (que es un campo de la actividad) pq el Adapter es una clase interna de la actividad.
        public int getItemCount() {
            return users_list.size();
        }
    }

}
