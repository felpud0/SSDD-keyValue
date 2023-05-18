package main.java.es.um.sisdist.impl;

import java.util.Optional;

import es.um.sisdist.backend.dao.DAOFactoryImpl;
import es.um.sisdist.backend.dao.IDAOFactory;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.dao.user.IUserDAO;

public class ExternalServiceImpl {
    
    private static ExternalServiceImpl instance;

    private IDAOFactory daoFactory;
    private IUserDAO userDAO;

    private ExternalServiceImpl() {
        daoFactory = new DAOFactoryImpl();
        userDAO = daoFactory.createMongoUserDAO();
    }

    public static ExternalServiceImpl getInstance() {
        if (instance == null) {
            instance = new ExternalServiceImpl();
        }
        return instance;
    }

    public Optional<User> getUser(String email) {
        return userDAO.getUserByEmail(email);
    }

    public boolean checkAuthToken(String authToken, User u, String date, String url){

        String userToken = u.getToken();
        return true;
    }


}
