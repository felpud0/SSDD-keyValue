/**
 *
 */
package es.um.sisdist.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.um.sisdist.backend.dao.models.DB;
import es.um.sisdist.backend.dao.models.User;

/**
 * @author dsevilla
 *
 */
public class UserDTOUtils
{
    public static User fromDTO(UserDTO udto)
    {
        System.out.println("UserDTOUtils.fromDTO: " + udto.toString());
        List<DB> dbs = new ArrayList<DB>(); 
        //udto.getDbs().forEach((dbname, db) -> dbs.add("/"+dbname)));
        return new User(udto.getId(),udto.getEmail(), udto.getPassword(), udto.getName(), udto.getToken(),
                udto.getVisits(), dbs);
    }

    public static UserDTO toDTO(User u)
    {
        System.out.println("UserDTOUtils.toDTO: " + u.toString());
        List<String> dbs = new ArrayList<String>();
        u.getDbs().forEach((db) -> dbs.add("/db/" + db.getDbname()));
        return new UserDTO(u.getUid(), u.getEmail(), "", // Password never is returned back
                u.getName(), u.getToken(), u.getVisits(), dbs);
    }
}
