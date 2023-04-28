package es.um.sisdist.backend.dao.user;

import java.util.List;
import java.util.Optional;

import es.um.sisdist.backend.dao.models.DB;
import es.um.sisdist.backend.dao.models.User;

public interface IUserDAO
{
    public Optional<User> getUserById(String id);

    public Optional<User> getUserByEmail(String id);
    
    public void addUsr(User u);
    
    public void deleteUsr(User u);

    public void updateUsr(User u);    

    public List<User> getAllUsers();
    
}
