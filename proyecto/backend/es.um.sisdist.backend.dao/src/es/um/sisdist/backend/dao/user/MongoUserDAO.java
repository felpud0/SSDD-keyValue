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
	public Optional<User> addUsr(String email, String name, String passwd) {
		//collection.drop();

		User u = new User();
		u.setEmail(email);
		u.setName(name);
		u.setPassword_hash(UserUtils.md5pass(passwd));
		u.setVisits(0);
		
		//u.setUid(new ObjectId().toString());
		
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String formattedDate = now.format(isoFormatter);
		u.setToken(UserUtils.md5pass("/register"+formattedDate+email));

		//u.setUid(new ObjectId().toString());
		
		System.out.println("EL USUARIO JUSTO ANTES DE METER A LA BBDD: "+u);		

		try {
			collection.insertOne(u);
			System.out.println("EL USUARIO QUE QUIERO AÑADIR: "+u);
			imprimir();
			return Optional.of(u);
		}catch (Exception e) {
			
			/*System.out.println("MIRA LOS ÍNDICES JUSTO ANTES:");
			for (Document d : collection.listIndexes()) {
				System.out.println(d);
			}
			//collection.dropIndex("uid");
		//	collection.dropIndex("email");
			//collection.dropIndex("id");
			collection.drop();
			

			
			System.out.println("MIRA LOS ÍNDICES JUSTO DESPUÉS:");
			for (Document d : collection.listIndexes()) {
				System.out.println(d);
			}
			*/
			System.out.println("AQUÍ PETO");
			System.out.println(e);
			imprimir();
			return Optional.empty();
		}

		
		
		
		
		
	}
    
	@Override
	public boolean deleteUsr(String email) {
				
	    if (collection.countDocuments(eq("email", email)) != 0) { // Comprobamos si existe ya un mail con esto
	    	collection.deleteOne(eq("email", email)); // Lo eliminamos
	    	
	    	
	       /* FindIterable<User> cursor = collection.find();

	    	System.out.println("ELEMENTOS BBDD:");
	        for (User doc : cursor) 
	            System.out.println(doc);*/
	    	
	    	return true;
	    }
	    
	    return false;


	}
    
    //Para ver qué usuarios hay en la bbdd
	private void imprimir() {
		FindIterable<User> cursor = collection.find();

    	System.out.println("ELEMENTOS BBDD:");
        for (User doc : cursor) 
            System.out.println(doc);
	}

	@Override
	public boolean updateUsr(String email) {
		Optional<User> user = getUserByEmail(email);
		if (!user.isPresent()) {
			return false;
		}
		User u = user.get();
		//Update in mongo
		collection.findOneAndReplace(eq("email", email), u);
		return true;
	}

}
