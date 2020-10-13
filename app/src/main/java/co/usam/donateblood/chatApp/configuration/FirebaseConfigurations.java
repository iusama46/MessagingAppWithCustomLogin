package co.usam.donateblood.chatApp.configuration;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseConfigurations {
    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getFirebaseDatabase(){
        if(databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference;
    }

    public static  FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }

        return firebaseAuth;
    }

}
