/**
 *
 */
package es.um.sisdist.backend.dao.user;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.dao.models.utils.UserUtils;

/**
 * @author dsevilla
 *
 */
public class MongoUserDAO implements IUserDAO
{
    private MongoCollection<User> collection;
    private MongoClient mongoClient;

    public MongoUserDAO()
    {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "mongodb://root:root@" 
        		+ Optional.ofNullable(System.getenv("MONGO_SERVER")).orElse("localhost")
                + ":27017/ssdd?authSource=admin";

        mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient
        		.getDatabase(Optional.ofNullable(System.getenv("DB_NAME")).orElse("ssdd"))
        		.withCodecRegistry(pojoCodecRegistry);
        collection = database.getCollection("users", User.class);
        
        //collection.deleteMany(new Document());
       
        imprimir();
                
        
    }

    @Override
    public Optional<User> getUserById(String id)
    {
        Optional<User> user = Optional.ofNullable(collection.find(eq("id", id)).first());
        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String id)
    {
        Optional<User> user = Optional.ofNullable(collection.find(eq("email", id)).first());
        return user;
    }

	@Override
	public void addUsr(User u) {

		System.out.println("EL USUARIO JUSTO ANTES DE METER A LA BBDD: "+u);		

		try {
			collection.insertOne(u);
			System.out.println("EL USUARIO QUE QUIERO AÑADIR: "+u);
			imprimir();
		}catch (Exception e) {
			
			//System.out.println("AQUÍ PETO");
			System.out.println(e);
			imprimir();
		}

	}
    
	@Override
	public void deleteUsr(User u) {
		String email = u.getEmail(); // Obtenemos el email del usuario que queremos eliminar
	    collection.deleteOne(eq("email", email)); // Lo eliminamos
	}
    
    //Para ver qué usuarios hay en la bbdd
	private void imprimir() {
		FindIterable<User> cursor = collection.find();

    	System.out.println("ELEMENTOS BBDD:");
        for (User doc : cursor) 
            System.out.println(doc);
	}

	@Override
	public void updateUsr(User u) {
		//Update in mongo
		collection.findOneAndReplace(eq("email", u.getEmail()), u);
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		FindIterable<User> cursor = collection.find();
		
		for (User doc : cursor) 
			users.add(doc);
		
		return users;
	}

}
