/**
 *
 */
package es.um.sisdist.backend.dao.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import es.um.sisdist.backend.dao.models.utils.UserUtils;

public class User
{
	@BsonId // Esto indica que Mongo nos va a generar el ID
	@BsonRepresentation(BsonType.OBJECT_ID) // Nos ayuda a tratarlo como String, aunque en realidad es un ObjectID
    private String uid;
	
    private String email;
    private String password_hash;
    private String name;

    private String token;

    private int visits;

    private Map<String, DB> dbs;

    /**
     * @return the id
     */
    public String getUid()
    {
        return uid;
    }

    /**
     * @param id the id to set
     */
    public void setUid(final String uid)
    {
        this.uid = uid;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(final String email)
    {
        this.email = email;
    }

    /**
     * @return the password_hash
     */
    public String getPassword_hash()
    {
        return password_hash;
    }

    /**
     * @param password_hash the password_hash to set
     */
    public void setPassword_hash(final String password_hash)
    {
        this.password_hash = password_hash;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @return the TOKEN
     */
    public String getToken()
    {
        return token;
    }

    /**
     * @param tOKEN the tOKEN to set
     */
    public void setToken(final String TOKEN)
    {
        this.token = TOKEN;
    }

    /**
     * @return the visits
     */
    public int getVisits()
    {
        return visits;
    }

    /**
     * @param visits the visits to set
     */
    public void setVisits(final int visits)
    {
        this.visits = visits;
    }

    public Map<String, DB> getDbs()
    {
        return Collections.unmodifiableMap(dbs);
    }

    public void addDB(DB db)
    {
        dbs.put(db.getDbname(), db);
    }

    public User(String email, String password_hash, String name, String tOKEN, int visits)
    {
        this(email, email, password_hash, name, tOKEN, visits);
        this.uid = UserUtils.md5pass(email);
    }

    public User(String uid, String email, String password_hash, String name, String tOKEN, int visits)
    {
        this.uid = uid;
        this.email = email;
        this.password_hash = password_hash;
        this.name = name;
        token = tOKEN;
        this.visits = visits;
        this.dbs = new HashMap<>();
    }

    @Override
    public String toString()
    {
        return "User [uid=" + uid + ", email=" + email + ", password_hash=" + password_hash + ", name=" + name
                + ", TOKEN=" + token + ", visits=" + visits + "]";
    }

    public User()
    {
    }
}