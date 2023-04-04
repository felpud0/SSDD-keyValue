/**
 *
 */
package es.um.sisdist.backend.dao.user;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Optional;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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

    public MongoUserDAO()
    {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "mongodb://root:root@" 
        		+ Optional.ofNullable(System.getenv("MONGO_SERVER")).orElse("localhost")
                + ":27017/ssdd?authSource=admin";

        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient
        		.getDatabase(Optional.ofNullable(System.getenv("DB_NAME")).orElse("ssdd"))
        		.withCodecRegistry(pojoCodecRegistry);
        collection = database.getCollection("users", User.class);
        
        
        /*
        Para ver qué usuarios hay en la bbdd
        
        FindIterable<User> cursor = collection.find();

        System.out.println("ELEMENTOS BBDD:");
        for (User doc : cursor) 
            System.out.println(doc);
        */
        
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
		
		User u = new User();
		u.setEmail(email);
		u.setName(name);
		u.setPassword_hash(UserUtils.md5pass(passwd));
		
		collection.insertOne(u);
		
		return Optional.of(u);
	}
    
    

}
