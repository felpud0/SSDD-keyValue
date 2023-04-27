package es.um.sisdist.backend.dao.user;

import java.util.List;
import java.util.Optional;

import es.um.sisdist.backend.dao.models.DB;
import es.um.sisdist.backend.dao.models.User;

public interface IUserDAO
{
    public Optional<User> getUserById(String id);

    public Optional<User> getUserByEmail(String id);
    
    public Optional<User> addUsr(String email, String name, String passwd);
    
    public boolean deleteUsr(String email);

    public boolean updateUsr(String email);    
    
}
