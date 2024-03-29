/**
 *
 */
package es.um.sisdist.backend.dao.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.util.stream.Collectors;
import es.um.sisdist.backend.dao.models.utils.UserUtils;

public class User
{
	//@BsonId // Esto indica que Mongo nos va a generar el ID
    //@BsonRepresentation(BsonType.OBJECT_ID) // Nos ayuda a tratarlo como String, aunque en realidad es un ObjectID
    private String uid;
	
    private String email;
    private String password_hash;
    private String name;

    private String token;

    private int visits;

    private List<DB> dbs;

    private List<String> mrHistory;

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
    
    public void addVisits() {
    	this.visits++;
    }

    public List<DB> getDbs()
    {
        return Collections.unmodifiableList(dbs);
    }

    public Optional<DB> getDB(String dbName)
    {
        return dbs.stream().filter(db -> db.getDbname().equals(dbName)).findFirst();
    }

    public boolean deleteDB(String dbName)
    {
        return dbs.removeIf(db -> db.getDbname().equals(dbName));
    }

    public void setDbs(ArrayList<DB> dbs) {
        this.dbs = dbs;
    }

    public void addDB(DB db)
    {
        dbs.add(db);
    }

    public boolean updateDB(String dbName, DB db)
    {
        int index = dbs.stream().map(DB::getDbname).collect(Collectors.toList()).indexOf(dbName);
        if (index != -1)
        {
            dbs.set(index, db);
            return true;
        }
        return false;

    }

    public Optional<Pair> getPair(String dbName, String pairName)
    {
        return getDB(dbName).orElseThrow().getPair(pairName);
    }

    public boolean updatePair(String dbName, String key, String value)
    {
        return getDB(dbName).orElseThrow().updatePair(key, value);
    }

    public boolean deletePair(String dbName, String key)
    {
        return getDB(dbName).orElseThrow().deletePair(key);
    }

    public boolean addPair(String dbName, String key, String value)
    {
        return getDB(dbName).orElseThrow().addPair(key,value);
    }

    public List<Pair> queryDB(String dbName, String pattern)
    {
        return getDB(dbName).orElseThrow().query(pattern);
    }

    public List<String> getMRHistory()
    {
        return mrHistory;
    }

    public void setMRHistory(List<String> mrHistory)
    {
        this.mrHistory = mrHistory;
    }

    public boolean isInMRHistory(String mrId)
    {
        return mrHistory.contains(mrId);
    }

    public void addToMRHistory(String mrId)
    {
        mrHistory.add(mrId);
    }

    
    public User(String email, String password_hash, String name, String tOKEN, int visits, List<DB> dbs)
    {
        this(email, email, password_hash, name, tOKEN, visits, dbs, new ArrayList<>());
        this.uid = UserUtils.md5pass(email);
    }

    public User(String uid, String email, String password_hash, String name, String tOKEN, int visits, List<DB> dbs, List<String> mrHistory)
    {
        this.uid = uid;
        this.email = email;
        this.password_hash = password_hash;
        this.name = name;
        token = tOKEN;
        this.visits = visits;
        this.dbs = dbs;
        this.mrHistory = mrHistory;
    }

    @Override
    public String toString()
    {
        return "User [uid=" + uid + ", email=" + email + ", password_hash=" + password_hash + ", name=" + name
                + ", TOKEN=" + token + ", visits=" + visits + ", dbs=" + dbs + "]";
    }

    public User()
    {
    }
}